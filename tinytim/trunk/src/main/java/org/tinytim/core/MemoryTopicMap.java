/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinytim.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.core.value.Literal;
import org.tinytim.index.IndexManager;
import org.tinytim.index.IIndexManager;
import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.IConstructFactory;
import org.tinytim.internal.api.IEventHandler;
import org.tinytim.internal.api.IEventPublisher;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.MergeUtils;

import org.tmapi.core.Association;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Variant;
import org.tmapi.index.Index;

/**
 * {@link org.tmapi.core.TopicMap} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class MemoryTopicMap extends ConstructImpl implements ITopicMap, 
        IEventHandler, IEventPublisher {

    private final IConstructFactory _factory;
    private final IdentityManager _identityManager;
    private final IIndexManager _indexManager;
    private final Locator _locator;
    private final Set<Topic> _topics;
    private final Set<Association> _assocs;
    private AbstractTopicMapSystem _sys;
    private Topic _reifier;
    private final Map<Event, List<IEventHandler>> _evtHandlers;
    private EventMultiplier _eventMultiplier;

    MemoryTopicMap(AbstractTopicMapSystem sys, Locator locator) {
        super(null);
        super._tm = this;
        _factory = new MemoryConstructFactory(this);
        _sys = sys;
        _locator = locator;
        _topics = CollectionFactory.createIdentitySet(IConstant.TM_TOPIC_SIZE);
        _assocs = CollectionFactory.createIdentitySet(IConstant.TM_ASSOCIATION_SIZE);
        _evtHandlers = CollectionFactory.createIdentityMap();
        _identityManager = new IdentityManager(this);
        _indexManager = new IndexManager();
        _indexManager.subscribe(this);
        _eventMultiplier = new EventMultiplier(this);
    }

    Locator getLocator() {
        return _locator;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getTopicMap()
     */
    @Override
    public TopicMap getTopicMap() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.tinytim.api.internal.ITopicMap#getConstructFactory()
     */
    public IConstructFactory getConstructFactory() {
        return _factory;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createLocator(java.lang.String)
     */
    public Locator createLocator(String reference) {
        return Literal.createIRI(reference);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getTopics()
     */
    public Set<Topic> getTopics() {
        return Collections.unmodifiableSet(_topics);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ITopicMap#createTopicWithoutIdentity()
     */
    public TopicImpl createTopicWithoutIdentity() {
        TopicImpl topic = new TopicImpl(this);
        addTopic(topic);
        return topic;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createTopic()
     */
    public Topic createTopic() {
        Topic topic = createTopicWithoutIdentity();
        topic.addItemIdentifier(Literal.createIRI("urn:x-tinytim:" + IdGenerator.nextId()));
        return topic;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createTopicByItemIdentifier(org.tmapi.core.Locator)
     */
    public Topic createTopicByItemIdentifier(Locator iid) {
        if (iid == null) {
            throw new ModelConstraintException(null, "The item identifier must not be null");
        }
        Construct construct = getConstructByItemIdentifier(iid);
        if (construct != null) {
            if (construct instanceof Topic) {
                return (Topic) construct;
            }
            throw new IdentityConstraintException(null, construct, iid, "A construct with the item identifier '" + iid.getReference() + "' already exists");
        }
        else {
            Topic topic = getTopicBySubjectIdentifier(iid);
            if (topic != null) {
                topic.addItemIdentifier(iid);
                return topic;
            }
        }
        TopicImpl topic = createTopicWithoutIdentity();
        topic.addItemIdentifier(iid);
        return topic;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic createTopicBySubjectIdentifier(Locator sid) {
        if (sid == null) {
            throw new ModelConstraintException(null, "The subject identifier must not be null");
        }
        Topic topic = getTopicBySubjectIdentifier(sid);
        if (topic != null) {
            return topic;
        }
        else {
            Construct construct = getConstructByItemIdentifier(sid);
            if (construct != null && construct instanceof Topic) {
                topic = (Topic) construct;
                topic.addSubjectIdentifier(sid);
                return topic;
            }
        }
        topic = createTopicWithoutIdentity();
        topic.addSubjectIdentifier(sid);
        return topic;
    }

    public Topic createTopicBySubjectLocator(Locator slo) {
        if (slo == null) {
            throw new ModelConstraintException(null, "The subject locator must not be null");
        }
        Topic topic = getTopicBySubjectLocator(slo);
        if (topic != null) {
            return topic;
        }
        topic = createTopicWithoutIdentity();
        topic.addSubjectLocator(slo);
        return topic;
    }

    /**
     * Adds a topic to the topics property.
     *
     * @param topic The topic to add.
     */
    void addTopic(TopicImpl topic) {
        if (topic._parent == this) {
            return;
        }
        _fireEvent(Event.ADD_TOPIC, null, topic);
        topic._parent = this;
        _topics.add(topic);
    }

    public void removeTopic(Topic topic_) {
        TopicImpl topic = (TopicImpl) topic_; 
        if (topic._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_TOPIC, topic, null);
        _topics.remove(topic);
        topic._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getAssociations()
     */
    public Set<Association> getAssociations() {
        return Collections.unmodifiableSet(_assocs);
    }

    public Association createAssociation(Topic type, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createAssociation(type, Arrays.asList(scope));
    }

    public Association createAssociation(Topic type, Collection<Topic> scope) {
        Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        AssociationImpl assoc = new AssociationImpl(this, type, Scope.create(scope));
        addAssociation(assoc);
        return assoc;
    }

    void addAssociation(Association assoc_) {
        AssociationImpl assoc = (AssociationImpl) assoc_;
        if (assoc._parent == this) {
            return;
        }
        _fireEvent(Event.ADD_ASSOCIATION, null, assoc);
        assoc._parent = this;
        _assocs.add(assoc);
    }

    public void removeAssociation(Association assoc_) {
        AssociationImpl assoc = (AssociationImpl) assoc_;
        if (assoc._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_ASSOCIATION, assoc, null);
        for (Role role: assoc.getRoles()) {
            TopicImpl player = (TopicImpl) role.getPlayer();
            if (player != null) {
                player.removeRolePlayed(role);
            }
        }
        _assocs.remove(assoc);
        assoc._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getConstructById(java.lang.String)
     */
    public Construct getConstructById(String id) {
        return _identityManager.getConstructById(id);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
        return _identityManager.getTopicBySubjectIdentifier(subjectIdentifier);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getTopicBySubjectLocator(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator) {
        return _identityManager.getTopicBySubjectLocator(subjectLocator);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getConstructByItemIdentifier(org.tmapi.core.Locator)
     */
    public Construct getConstructByItemIdentifier(Locator itemIdentifier) {
        return _identityManager.getConstructByItemIdentifier(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getReifier()
     */
    public Topic getReifier() {
        return _reifier;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IReifiable#setReifier(org.tmapi.core.Topic)
     */
    public void setReifier(Topic reifier) {
        if (_reifier == reifier) {
            return;
        }
        _fireEvent(Event.SET_REIFIER, _reifier, reifier);
        if (_reifier != null) {
            ((TopicImpl) _reifier)._reified = null;
        }
        _reifier = reifier;
        if (reifier != null) {
            ((TopicImpl) reifier)._reified = this;
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getIndex(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <I extends Index> I getIndex(Class<I> indexInterface) {
        if (indexInterface.getName().equals("org.tmapi.index.TypeInstanceIndex")) {
            return (I) _indexManager.getTypeInstanceIndex();
        }
        if (indexInterface.getName().equals("org.tmapi.index.ScopedIndex")) {
            return (I) _indexManager.getScopedIndex();
        }
        if (indexInterface.getName().equals("org.tmapi.index.LiteralIndex")) {
            return (I) _indexManager.getLiteralIndex();
        }
        throw new UnsupportedOperationException("Index '" + indexInterface.getName() + "'  is unknown");
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#mergeIn(org.tmapi.core.TopicMap)
     */
    public void mergeIn(TopicMap other) {
        MergeUtils.merge(other, this);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#close()
     */
    public void close() {
        remove();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isTopicMap()
     */
    @Override
    public boolean isTopicMap() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#remove()
     */
    public void remove() {
        _sys.removeTopicMap(this);
        _sys = null;
        _topics.clear();
        _assocs.clear();
        _indexManager.close();
        _identityManager.close();
        _eventMultiplier = null;
        super.dispose();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#_fireEvent(org.tinytim.core.Event, java.lang.Object, java.lang.Object)
     */
    @Override
    protected final void _fireEvent(Event evt, Object oldValue, Object newValue) {
        handleEvent(evt, this, oldValue, newValue);
    }

    /* (non-Javadoc)
     * @see org.tinytim.IEventHandler#handleEvent(org.tinytim.Event, org.tinytim.IConstruct, java.lang.Object, java.lang.Object)
     */
    public void handleEvent(Event evt, IConstruct sender, Object oldValue, Object newValue) {
        if (!_evtHandlers.containsKey(evt)) {
            _eventMultiplier.handleEvent(evt, sender, oldValue, newValue);
            return;
        }
        List<IEventHandler> handlers = _evtHandlers.get(evt);
        for (IEventHandler handler: handlers) {
            handler.handleEvent(evt, sender, oldValue, newValue);
        }
        _eventMultiplier.handleEvent(evt, sender, oldValue, newValue);
    }

    /* (non-Javadoc)
     * @see org.tinytim.IEventPublisher#subscribe(org.tinytim.Event, org.tinytim.IEventHandler)
     */
    public void subscribe(Event event, IEventHandler handler) {
        List<IEventHandler> handlers = _evtHandlers.get(event);
        if (handlers == null) {
            handlers = CollectionFactory.createList();
            _evtHandlers.put(event, handlers);
        }
        handlers.add(handler);
    }

    /* (non-Javadoc)
     * @see org.tinytim.IEventPublisher#unsubscribe(org.tinytim.Event, org.tinytim.IEventHandler)
     */
    public void unsubscribe(Event event, IEventHandler handler) {
        List<IEventHandler> handlers = _evtHandlers.get(event);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    public IIndexManager getIndexManager() {
        return _indexManager;
    }

    private static class EventMultiplier implements IEventHandler {

        private MemoryTopicMap _handler;

        EventMultiplier(MemoryTopicMap handler) {
            _handler = handler;
        }

        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            switch (evt) {
                case ADD_TOPIC:         _topicAdd((TopicImpl)newValue); break;
                case ADD_ASSOCIATION:   _associationAdd((AssociationImpl)newValue); break;
                case ADD_NAME:          _nameAdd((NameImpl)newValue); break;
                case ADD_ROLE:
                case ADD_OCCURRENCE:
                case ADD_VARIANT:       _constructAdd((IConstruct)newValue); break;
                case REMOVE_TOPIC:      _topicRemove((TopicImpl) oldValue); break;
                case REMOVE_ASSOCIATION: _associationRemove((AssociationImpl) oldValue); break;
                case REMOVE_NAME:       _nameRemove((NameImpl)oldValue); break;
                case REMOVE_ROLE:
                case REMOVE_OCCURRENCE:
                case REMOVE_VARIANT:    _constructRemove((IConstruct) oldValue); break;
            }
        }

        private void _topicAdd(TopicImpl sender) {
            _constructAdd(sender);
            for (Locator sid: sender.getSubjectIdentifiers()) {
                _handler.handleEvent(Event.ADD_SID, sender, null, sid);
            }
            for (Locator slo: sender.getSubjectLocators()) {
                _handler.handleEvent(Event.ADD_SLO, sender, null, slo);
            }
            for (Topic type: sender.getTypes()) {
                _handler.handleEvent(Event.ADD_TYPE, sender, null, type);
            }
            for (Occurrence occ: sender.getOccurrences()) {
                _handler.handleEvent(Event.ADD_OCCURRENCE, sender, null, occ);
            }
            for (Name name: sender.getNames()) {
                _handler.handleEvent(Event.ADD_NAME, sender, null, name);
            }
        }

        private void _associationAdd(AssociationImpl sender) {
            _constructAdd(sender);
            for (Role role: sender.getRoles()) {
                _handler.handleEvent(Event.ADD_ROLE, sender, null, role);
            }
        }

        private void _nameAdd(NameImpl sender) {
            _constructAdd(sender);
            for (Variant variant: sender.getVariants()) {
                _handler.handleEvent(Event.ADD_VARIANT, sender, null, variant);
            }
        }

        private void _constructAdd(IConstruct construct) {
            for (Locator iid: construct.getItemIdentifiers()) {
                _handler.handleEvent(Event.ADD_IID, construct, null, iid);
            }
        }

        private void _constructRemove(IConstruct sender) {
            for (Locator iid: sender.getItemIdentifiers()) {
                _handler.handleEvent(Event.REMOVE_IID, sender, iid, null);
            }
        }

        private void _topicRemove(TopicImpl sender) {
            _constructRemove(sender);
            for (Locator sid: sender.getSubjectIdentifiers()) {
                _handler.handleEvent(Event.REMOVE_SID, sender, sid, null);
            }
            for (Locator slo: sender.getSubjectLocators()) {
                _handler.handleEvent(Event.REMOVE_SLO, sender, slo, null);
            }
            for (Topic type: sender.getTypes()) {
                _handler.handleEvent(Event.REMOVE_TYPE, sender, type, null);
            }
            for (Occurrence occ: sender.getOccurrences()) {
                _handler.handleEvent(Event.REMOVE_OCCURRENCE, sender, occ, null);
            }
            for (Name name: sender.getNames()) {
                _handler.handleEvent(Event.REMOVE_NAME, sender, name, null);
            }
        }

        private void _associationRemove(AssociationImpl sender) {
            _constructRemove(sender);
            for (Role role: sender.getRoles()) {
                _handler.handleEvent(Event.REMOVE_ROLE, sender, role, null);
            }
        }

        private void _nameRemove(NameImpl sender) {
            _constructRemove(sender);
            for (Variant variant: sender.getVariants()) {
                _handler.handleEvent(Event.REMOVE_VARIANT, sender, variant, null);
            }
        }

    }

}
