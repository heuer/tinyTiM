/*
 * This is tinyTiM, a tiny Topic Maps engine.
 *
 * Copyright (C) 2008 Lars Heuer (heuer[at]semagia.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
package org.tinytim;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.index.IndexManager;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.HelperObjectConfigurationException;
import org.tmapi.core.HelperObjectInstantiationException;
import org.tmapi.core.Locator;
import org.tmapi.core.MergeException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicName;
import org.tmapi.core.UnsupportedHelperObjectException;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.TopicMap} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TopicMapImpl extends Construct implements TopicMap, 
        IReifiable, IEventHandler, IEventPublisher {

    // Fixed set of helper objects, against the TMAPI 1.0 spec., though
    private static final Map<String, String> _SUPPORTED_HELPER_OBJECTS = new HashMap<String, String>();

    static {
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.TopicMapObjectsIndex.class.getName(), 
                org.tinytim.index.tmapi.TopicMapObjectsIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.ScopedObjectsIndex.class.getName(), 
                org.tinytim.index.tmapi.ScopedObjectsIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.TopicsIndex.class.getName(), 
                org.tinytim.index.tmapi.TopicsIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.AssociationsIndex.class.getName(), 
                org.tinytim.index.tmapi.AssociationsIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.AssociationRolesIndex.class.getName(), 
                org.tinytim.index.tmapi.AssociationRolesIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.OccurrencesIndex.class.getName(), 
                org.tinytim.index.tmapi.OccurrencesIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.TopicNamesIndex.class.getName(), 
                org.tinytim.index.tmapi.TopicNamesIndexImpl.class.getName());
        _SUPPORTED_HELPER_OBJECTS.put(org.tmapi.index.core.VariantsIndex.class.getName(), 
                org.tinytim.index.tmapi.VariantsIndexImpl.class.getName());
    };

    private IdentityManager _identityManager;
    private IndexManager _indexManager;
    private ICollectionFactory _collectionFactory;
    private Locator _locator;
    private Set<Topic> _topics;
    private Set<Association> _assocs;
    private TopicMapSystemImpl _sys;
    private Topic _reifier;
    private Map<Event, List<IEventHandler>> _evtHandlers;
    private EventMultiplier _eventMultiplier;
    private Map<String, Object> _helperObjects;
    boolean _oldReification;

    TopicMapImpl(TopicMapSystemImpl sys, Locator locator) {
        super(null);
        super._tm = this;
        _sys = sys;
        _locator = locator;
        _collectionFactory = _sys.getCollectionFactory();
        _topics = _collectionFactory.createSet(100);
        _assocs = _collectionFactory.createSet(100);
        _evtHandlers = _collectionFactory.createMap();
        _helperObjects = _collectionFactory.createMap();
        _identityManager = new IdentityManager(this);
        _indexManager = new IndexManager(this, _collectionFactory);
        _eventMultiplier = new EventMultiplier(this);
        _oldReification = "true".equalsIgnoreCase(sys.getProperty(Property.XTM10_REIFICATION));
    }

    ICollectionFactory getCollectionFactory() {
        return _collectionFactory;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getTopicMapSystem()
     */
    public TopicMapSystem getTopicMapSystem() {
        return _sys;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getBaseLocator()
     */
    public Locator getBaseLocator() {
        return _locator;
    }

    /* (non-Javadoc)
     * @see org.tinytim.Construct#getTopicMap()
     */
    @Override
    public TopicMap getTopicMap() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createLocator(java.lang.String)
     */
    public Locator createLocator(String reference) {
        return new IRI(reference);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createLocator(java.lang.String, java.lang.String)
     */
    public Locator createLocator(String reference, String notation) {
        assert "URI".equals(notation);
        return createLocator(reference);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getTopics()
     */
    public Set<Topic> getTopics() {
        return Collections.unmodifiableSet(_topics);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createTopic()
     */
    public Topic createTopic() {
        TopicImpl topic = new TopicImpl(this);
        addTopic(topic);
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

    /**
     * Removes a topic from the topics property.
     * 
     * Caution: This method does not check if a topic has any dependencies;
     * this method never reports that a topic is not removable. This
     * method should only be used if a topic should be detached.
     *
     * @param topic The topic to remove.
     */
    void removeTopic(TopicImpl topic) {
        if (topic._parent != this) {
            return;
        }
        assert topic._parent == null;
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

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#createAssociation()
     */
    public Association createAssociation() {
        AssociationImpl assoc = new AssociationImpl(this);
        addAssociation(assoc);
        return assoc;
    }

    void addAssociation(AssociationImpl assoc) {
        if (assoc._parent == this) {
            return;
        }
        _fireEvent(Event.ADD_ASSOCIATION, null, assoc);
        assoc._parent = this;
        _assocs.add(assoc);
    }

    void removeAssociation(AssociationImpl assoc) {
        if (assoc._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_ASSOCIATION, assoc, null);
        for (AssociationRole role: assoc.getAssociationRoles()) {
            TopicImpl player = (TopicImpl) role.getPlayer();
            if (player != null) {
                player.removeRolePlayed(role);
            }
        }
        _assocs.remove(assoc);
        assoc._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getObjectById(java.lang.String)
     */
    public TopicMapObject getObjectById(String id) {
        return _identityManager.getConstructById(id);
    }

    /**
     * Returns a toic by its subject identifier.
     *
     * @param subjectIdentifier The subject identifier.
     * @return A topic or <code>null</code> if no topic with the specified
     *          subject identifier exists. 
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
        return _identityManager.getTopicBySubjectIdentifier(subjectIdentifier);
    }

    /**
     * Returns a toic by its subject locator.
     *
     * @param subjectLocator The subject locator.
     * @return A topic or <code>null</code> if no topic with the specified
     *          subject locator exists. 
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator) {
        return _identityManager.getTopicBySubjectLocator(subjectLocator);
    }

    /**
     * Returns a Topic Maps construct by its item identifier.
     *
     * @param itemIdentifier The item identifier.
     * @return A Topic Maps construct or <code>null</code> if no topic with 
     *          the specified item identifier exists. 
     */
    public TopicMapObject getObjectByItemIdentifier(Locator itemIdentifier) {
        return _identityManager.getConstructByItemIdentifier(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getReifier()
     */
    public Topic getReifier() {
        if (_oldReification) {
            return ReificationUtils.getReifier(this);
        }
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
     * @see org.tmapi.core.TopicMap#mergeIn(org.tmapi.core.TopicMap)
     */
    public void mergeIn(TopicMap other) throws MergeException {
        MergeUtils.merge(other, this);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#close()
     */
    public void close() {
        try {
            remove();
        }
        catch (TMAPIException ex) {
            // noop.
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#remove()
     */
    public void remove() throws TMAPIException {
        _sys.removeTopicMap(this);
        _sys = null;
        _locator = null;
        _topics = null;
        _assocs = null;
        _indexManager.close();
        _indexManager = null;
        _identityManager.close();
        _identityManager = null;
        _eventMultiplier = null;
        super.dispose();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMap#getHelperObject(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getHelperObject(Class implInterface)
            throws UnsupportedHelperObjectException,
            HelperObjectInstantiationException,
            HelperObjectConfigurationException {
        String interfaceName = implInterface.getName();
        Object instance = _helperObjects.get(interfaceName);
        if (instance == null) {
            String className = _SUPPORTED_HELPER_OBJECTS.get(interfaceName);
            if (className == null) {
                throw new UnsupportedHelperObjectException("A helper object of class " + implInterface.getName() + " is not supported");
            }
            try {
                Class klass = Class.forName(className);
                // Caution: Bypassing the configure step etc since we know
                // that this constructor exists.
                Constructor constr = klass.getConstructor(TopicMapImpl.class, ICollectionFactory.class);
                instance = constr.newInstance(this, this._collectionFactory);
                _helperObjects.put(interfaceName, instance);
            }
            catch (Exception ex) {
                throw new HelperObjectInstantiationException("");
            }
        }
        return instance;
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
            handlers = new ArrayList<IEventHandler>();
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

    public IndexManager getIndexManager() {
        return _indexManager;
    }

    private static class EventMultiplier implements IEventHandler {

        private TopicMapImpl _handler;

        EventMultiplier(TopicMapImpl handler) {
            _handler = handler;
        }

        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            switch (evt) {
                case ADD_TOPIC:         _topicAdd((TopicImpl)newValue); break;
                case ADD_ASSOCIATION:   _associationAdd((AssociationImpl)newValue); break;
                case ADD_NAME:          _nameAdd((TopicNameImpl)newValue); break;
                case ADD_ROLE:
                case ADD_OCCURRENCE:
                case ADD_VARIANT:       _constructAdd((IConstruct)newValue); break;
                case REMOVE_TOPIC:      _topicRemove((TopicImpl) oldValue); break;
                case REMOVE_ASSOCIATION: _associationRemove((AssociationImpl) oldValue); break;
                case REMOVE_NAME:       _nameRemove((TopicNameImpl)oldValue); break;
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
            for (TopicName name: sender.getTopicNames()) {
                _handler.handleEvent(Event.ADD_NAME, sender, null, name);
            }
        }

        private void _associationAdd(AssociationImpl sender) {
            _constructAdd(sender);
            for (AssociationRole role: sender.getAssociationRoles()) {
                _handler.handleEvent(Event.ADD_ROLE, sender, null, role);
            }
        }

        private void _nameAdd(TopicNameImpl sender) {
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
            for (TopicName name: sender.getTopicNames()) {
                _handler.handleEvent(Event.REMOVE_NAME, sender, name, null);
            }
        }

        private void _associationRemove(AssociationImpl sender) {
            _constructRemove(sender);
            for (AssociationRole role: sender.getAssociationRoles()) {
                _handler.handleEvent(Event.REMOVE_ROLE, sender, role, null);
            }
        }

        private void _nameRemove(TopicNameImpl sender) {
            _constructRemove(sender);
            for (Variant variant: sender.getVariants()) {
                _handler.handleEvent(Event.REMOVE_VARIANT, sender, variant, null);
            }
        }

    }
}
