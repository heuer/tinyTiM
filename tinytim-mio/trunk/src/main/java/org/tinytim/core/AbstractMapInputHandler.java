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

import java.util.List;

import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.utils.TypeInstanceConverter;
import org.tinytim.voc.TMDM;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * Abstract {@link com.semagia.mio.IMapHandler} implementation.
 * <p>
 * This class utilises the <tt>.core</tt> package since some <tt>...Impl</tt>
 * classes have only package visibility.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractMapInputHandler implements IMapHandler {

    private static final int 
        INITIAL = 1,
        TOPIC = 2,
        ASSOCIATION = 3,
        ROLE = 4,
        OCCURRENCE = 5,
        NAME = 6,
        VARIANT = 7,
        SCOPE = 8,
        THEME = 9,
        REIFIER = 10,
        PLAYER = 11,
        ISA = 12,
        TYPE = 13;

    private static final int _CONSTRUCT_SIZE = 6;
    private static final int _STATE_SIZE = 10;
    private static final int _SCOPE_SIZE = 6;

    private ITopicMap _tm;
    private int[] _stateStack;
    private int _stateSize;
    private IConstruct[] _constructStack;
    private int _constructSize;
    private List<Topic> _scope;

    protected AbstractMapInputHandler(TopicMap topicMap) {
        setTopicMap(topicMap);
    }

    /**
     * Sets the topic map instance to operate on.
     *
     * @param topicMap The topic map.
     */
    private final void setTopicMap(TopicMap topicMap) {
        if (topicMap == null) {
            throw new IllegalArgumentException("The topic map must not be null");
        }
        _tm = (ITopicMap) topicMap;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopicMap()
     */
    public final void startTopicMap() throws MIOException {
        _constructStack = new IConstruct[_CONSTRUCT_SIZE];
        _stateStack = new int[_STATE_SIZE];
        _constructSize = 0;
        _stateSize = 0;
        _scope = CollectionFactory.createList(_SCOPE_SIZE);
        _enterState(INITIAL, _tm);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopicMap()
     */
    public final void endTopicMap() throws MIOException {
        TypeInstanceConverter.convertAssociationsToTypes(_tm);
        _constructStack = null;
        _stateStack = null;
        _scope = null;
        _tm = null;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopic(com.semagia.mio.IRef)
     */
    public void startTopic(IRef identity) throws MIOException {
        _enterState(TOPIC, _createTopic(identity));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopic()
     */
    public final void endTopic() throws MIOException {
        _handleTopic((Topic) _leaveStatePopConstruct(TOPIC));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startAssociation()
     */
    public final void startAssociation() throws MIOException {
        _enterState(ASSOCIATION, new AssociationImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endAssociation()
     */
    public final void endAssociation() throws MIOException {
        AssociationImpl assoc = (AssociationImpl) _leaveStatePopConstruct(ASSOCIATION);
        _tm.addAssociation(assoc);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startRole()
     */
    public final void startRole() throws MIOException {
        assert _state() == ASSOCIATION;
        _enterState(ROLE, new RoleImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endRole()
     */
    public final void endRole() throws MIOException {
        Role role = (Role) _leaveStatePopConstruct(ROLE);
        ((AssociationImpl) _peekConstruct()).addRole(role);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startPlayer()
     */
    public final void startPlayer() throws MIOException {
        assert _state() == ROLE;
        _enterState(PLAYER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endPlayer()
     */
    public final void endPlayer() throws MIOException {
        _leaveState(PLAYER);
        assert _state() == ROLE;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startOccurrence()
     */
    public final void startOccurrence() throws MIOException {
        _enterState(OCCURRENCE, new OccurrenceImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endOccurrence()
     */
    public final void endOccurrence() throws MIOException {
        Occurrence occ = (Occurrence) _leaveStatePopConstruct(OCCURRENCE);
        _peekTopic().addOccurrence(occ);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startName()
     */
    public final void startName() throws MIOException {
        _enterState(NAME, new NameImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endName()
     */
    public final void endName() throws MIOException {
        Name name = (Name) _leaveStatePopConstruct(NAME);
        if (name.getType() == null) {
            name.setType(_tm.createTopicBySubjectIdentifier(TMDM.TOPIC_NAME));
        }
        _peekTopic().addName(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startVariant()
     */
    public final void startVariant() throws MIOException {
        assert _state() == NAME;
        _enterState(VARIANT, new VariantImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endVariant()
     */
    public final void endVariant() throws MIOException {
        VariantImpl variant = (VariantImpl) _leaveStatePopConstruct(VARIANT);
        NameImpl name = (NameImpl) _peekConstruct();
        IScope scope = variant.getScopeObject();
        if (scope.isUnconstrained() || name.getScopeObject() == scope) {
            _reportError("The variant has no scope");
        }
        name.addVariant(variant);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startType()
     */
    public final void startType() throws MIOException {
        assert _peekConstruct() instanceof Typed;
        _enterState(TYPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endType()
     */
    public final void endType() throws MIOException {
        _leaveState(TYPE);
        assert _peekConstruct() instanceof Typed;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startScope()
     */
    public final void startScope() throws MIOException {
        assert _peekConstruct() instanceof Scoped;
        _enterState(SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endScope()
     */
    public final void endScope() throws MIOException {
        _leaveState(SCOPE);
        ((IScoped) _peekConstruct()).setScopeObject(Scope.create(_scope));
        _scope.clear();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTheme()
     */
    public final void startTheme() throws MIOException {
        assert _state() == SCOPE;
        _enterState(THEME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTheme()
     */
    public final void endTheme() throws MIOException {
        _leaveState(THEME);
        assert _state() == SCOPE;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectIdentifier(java.lang.String)
     */
    public final void subjectIdentifier(String subjectIdentifier) throws MIOException {
        Locator sid = _tm.createLocator(subjectIdentifier);
        TopicImpl topic = _peekTopic();
        Topic existing = _tm.getTopicBySubjectIdentifier(sid);
        if (existing != null && !(existing == topic)) {
            _merge(existing, topic);
        }
        else {
            IConstruct tmo = (IConstruct) _tm.getConstructByItemIdentifier(sid);
            if (tmo != null && tmo.isTopic() && !tmo.equals(topic)) {
                _merge((Topic) tmo, topic);
            }
        }
        topic.addSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectLocator(java.lang.String)
     */
    public final void subjectLocator(String subjectLocator) throws MIOException {
        Locator slo = _tm.createLocator(subjectLocator);
        TopicImpl topic = _peekTopic();
        Topic existing = _tm.getTopicBySubjectLocator(slo);
        if (existing != null && !(existing == topic)) {
            _merge(existing, topic);
        }
        topic.addSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#itemIdentifier(java.lang.String)
     */
    public final void itemIdentifier(String itemIdentifier) throws MIOException {
        Locator iid = _tm.createLocator(itemIdentifier);
        IConstruct tmo = _peekConstruct();
        if (_state() == TOPIC) {
            IConstruct existing = (IConstruct) _tm.getConstructByItemIdentifier(iid);
            if (existing != null && existing.isTopic() && !existing.equals(tmo)) {
                _merge((Topic) existing, (TopicImpl) tmo);
            }
            else {
                Topic topic = _tm.getTopicBySubjectIdentifier(iid);
                if (topic != null && !topic.equals(tmo)) {
                    _merge(topic, (TopicImpl) tmo);
                }
            }
        }
        tmo.addItemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startIsa()
     */
    public final void startIsa() throws MIOException {
        assert _state() == TOPIC;
        _enterState(ISA);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endIsa()
     */
    public final void endIsa() throws MIOException {
        _leaveState(ISA);
        assert _state() == TOPIC;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startReifier()
     */
    public final void startReifier() throws MIOException {
        assert _peekConstruct() instanceof Reifiable;
        _enterState(REIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endReifier()
     */
    public final void endReifier() throws MIOException {
        _leaveState(REIFIER);
        assert _peekConstruct() instanceof Reifiable;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#topicRef(com.semagia.mio.IRef)
     */
    public final void topicRef(IRef identity) throws MIOException {
        _handleTopic(_createTopic(identity));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String)
     */
    public final void value(String value) throws MIOException {
        assert _state() == NAME;
        ((Name) _peekConstruct()).setValue(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String, java.lang.String)
     */
    public final void value(String value, String datatype) throws MIOException {
        ((ILiteralAware) _peekConstruct()).setLiteral(Literal.create(value, datatype));
    }

    /**
     * Enters a state.
     *
     * @param state The state to push ontop of the state stack.
     */
    private void _enterState(int state) {
        if (_stateSize >= _stateStack.length) {
            int[] states = new int[_stateStack.length*2];
            System.arraycopy(_stateStack, 0, states, 0, _stateStack.length);
            _stateStack = states;
        }
        _stateStack[_stateSize++] = state;
    }

    /**
     * Enters a state and pushes the Topic Maps construct ontop of the construct
     * stack.
     *
     * @param state The state to enter.
     * @param tmo The Topic Maps construct which should be pushed to the stack.
     */
    private void _enterState(int state, IConstruct tmo) {
        _enterState(state);
        if (_constructSize >= _constructStack.length) {
            IConstruct[] constructs = new IConstruct[_constructStack.length*2];
            System.arraycopy(_constructStack, 0, constructs, 0, _constructStack.length);
            _constructStack = constructs;
        }
        _constructStack[_constructSize++] = tmo;
    }

    /**
     * Leaves a state.
     *
     * @param state The state to leave.
     * @throws MIOException If the state is not equals to the current state.
     */
    private void _leaveState(int state) throws MIOException {
        if (state != _state()) {
            _reportError("Unexpected state: " + _state() + ", expected: " + state);
        }
        _stateSize--;
    }

    /**
     * Leaves a state and removed the Topic Maps construct from the top of the
     * construct stack.
     *
     * @param state The state to leave.
     * @throws MIOException If the state is not equals to the current state.
     */
    private IConstruct _leaveStatePopConstruct(int state) throws MIOException {
        _leaveState(state);
        final IConstruct construct = _peekConstruct();
        _constructStack[_constructSize] = null;
        _constructSize--;
        return construct;
    }

    /**
     * Returns the Topic Maps construct on top of the stack.
     *
     * @return The Topic Maps construct.
     */
    private IConstruct _peekConstruct() {
        return _constructStack[_constructSize-1];
    }

    /**
     * Returns the topic on top of the stack.
     *
     * @return The topic.
     */
    private TopicImpl _peekTopic() {
        return (TopicImpl) _peekConstruct();
    }

    /**
     * Returns the current state.
     *
     * @return The current state.
     */
    private int _state() {
        return _stateStack[_stateSize-1];
    }

    /**
     * Handles the topic dependent on the current state.
     *
     * @param topic The topic to handle.
     */
    private void _handleTopic(Topic topic) {
        switch (_state()) {
            case ISA: _peekTopic().addType(topic); break;
            case TYPE: ((Typed) _peekConstruct()).setType(topic); break;
            case PLAYER: ((Role) _peekConstruct()).setPlayer(topic); break;
            case THEME: _scope.add(topic); break;
            case REIFIER: ((Reifiable) _peekConstruct()).setReifier(topic); break;
        }
    }

    /**
     * Merges the <tt>source</tt> topic with the <tt>target</tt>.
     * 
     * Further, this method ensures that the construct stack stays valid: If
     * the <tt>source</tt> is part of the stack, it is replaced with 
     * <tt>target</tt>.
     *
     * @param source The source topic (will be removed).
     * @param target The target topic.
     */
    private void _merge(Topic source, TopicImpl target) {
        for (int i=0; i <_constructSize; i++) {
            if (_constructStack[i] == source) {
                _constructStack[i] = target;
            }
        }
        target.mergeIn(source);
    }

    /**
     * Returns either an existing topic with the specified identity or creates
     * a topic with the given identity.
     *
     * @param ref The identity of the topic.
     * @return A topic instance.
     * @throws MIOException 
     */
    private TopicImpl _createTopic(IRef ref) throws MIOException {
        Locator loc = _tm.createLocator(ref.getIRI());
        switch (ref.getType()) {
            case IRef.ITEM_IDENTIFIER: return (TopicImpl) _tm.createTopicByItemIdentifier(loc);
            case IRef.SUBJECT_IDENTIFIER: return (TopicImpl) _tm.createTopicBySubjectIdentifier(loc);
            case IRef.SUBJECT_LOCATOR: return (TopicImpl) _tm.createTopicBySubjectLocator(loc);
            default: _reportError("Unknown reference type " + ref.getType());
        }
        // Never returned, an exception was thrown
        return null;
    }

    /**
     * Reports an error.
     *
     * @param msg The error message.
     * @throws MIOException Thrown in any case.
     */
    private static void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

}
