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
package org.tinytim.core;

import java.util.ArrayList;
import java.util.List;

import org.tinytim.utils.TypeInstanceConverter;
import org.tinytim.voc.TMDM;
import org.tmapi.core.Construct;
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
 * {@link com.semagia.mio.IMapHandler} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TinyTimMapInputHandler implements IMapHandler {

    private enum State {
        INITIAL, TOPIC, ASSOCIATION, ROLE, OCCURRENCE, NAME, VARIANT,
        SCOPE, THEME, REIFIER, PLAYER, ISA, TYPE;
    }

    private TopicMapImpl _tm;
    private List<State> _stateStack;
    private List<Construct> _constructStack;
    private List<Topic> _scope;

    public TinyTimMapInputHandler() {
        // noop.
    }

    public TinyTimMapInputHandler(TopicMap topicMap) {
        this();
        setTopicMap(topicMap);
    }

    /**
     * Sets the topic map instance to operate on.
     *
     * @param topicMap The topic map.
     */
    public void setTopicMap(TopicMap topicMap) {
        if (topicMap == null) {
            throw new IllegalArgumentException("The topic map must not be null");
        }
        _tm = (TopicMapImpl) topicMap;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopicMap()
     */
    public void startTopicMap() throws MIOException {
        _constructStack = new ArrayList<Construct>();
        _stateStack = new ArrayList<State>();
        _scope = new ArrayList<Topic>();
        _enterState(State.INITIAL, _tm);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopicMap()
     */
    public void endTopicMap() throws MIOException {
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
        _enterState(State.TOPIC, _createTopic(identity));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopic()
     */
    public void endTopic() throws MIOException {
        Topic topic = (Topic) _leaveStatePopConstruct(State.TOPIC);
        _handleTopic(topic);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startAssociation()
     */
    public void startAssociation() throws MIOException {
        _enterState(State.ASSOCIATION, new AssociationImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endAssociation()
     */
    public void endAssociation() throws MIOException {
        AssociationImpl assoc = (AssociationImpl) _leaveStatePopConstruct(State.ASSOCIATION);
        _tm.addAssociation(assoc);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startRole()
     */
    public void startRole() throws MIOException {
        assert _state() == State.ASSOCIATION;
        _enterState(State.ROLE, new RoleImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endRole()
     */
    public void endRole() throws MIOException {
        Role role = (Role) _leaveStatePopConstruct(State.ROLE);
        ((AssociationImpl) _peekConstruct()).addRole(role);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startPlayer()
     */
    public void startPlayer() throws MIOException {
        assert _state() == State.ROLE;
        _enterState(State.PLAYER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endPlayer()
     */
    public void endPlayer() throws MIOException {
        _leaveState(State.PLAYER);
        assert _state() == State.ROLE;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startOccurrence()
     */
    public void startOccurrence() throws MIOException {
        _enterState(State.OCCURRENCE, new OccurrenceImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endOccurrence()
     */
    public void endOccurrence() throws MIOException {
        Occurrence occ = (Occurrence) _leaveStatePopConstruct(State.OCCURRENCE);
        _peekTopic().addOccurrence(occ);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startName()
     */
    public void startName() throws MIOException {
        _enterState(State.NAME, new NameImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endName()
     */
    public void endName() throws MIOException {
        Name name = (Name) _leaveStatePopConstruct(State.NAME);
        if (name.getType() == null) {
            name.setType(_tm.createTopicBySubjectIdentifier(TMDM.TOPIC_NAME));
        }
        _peekTopic().addName(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startVariant()
     */
    public void startVariant() throws MIOException {
        assert _state() == State.NAME;
        _enterState(State.VARIANT, new VariantImpl(_tm));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endVariant()
     */
    public void endVariant() throws MIOException {
        VariantImpl variant = (VariantImpl) _leaveStatePopConstruct(State.VARIANT);
        NameImpl name = (NameImpl) _peekConstruct();
        IScope scope = variant.getScopeObject();
        if (scope.isUnconstrained() || name.getScopeObject() == scope) {
            throw new MIOException("The variant has no scope");
        }
        name.addVariant(variant);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startType()
     */
    public void startType() throws MIOException {
        assert _peekConstruct() instanceof Typed;
        _enterState(State.TYPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endType()
     */
    public void endType() throws MIOException {
        _leaveState(State.TYPE);
        assert _peekConstruct() instanceof Typed;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startScope()
     */
    public void startScope() throws MIOException {
        assert _peekConstruct() instanceof Scoped;
        _enterState(State.SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endScope()
     */
    public void endScope() throws MIOException {
        _leaveState(State.SCOPE);
        ((IScoped) _peekConstruct()).setScopeObject(Scope.create(_scope));
        _scope.clear();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTheme()
     */
    public void startTheme() throws MIOException {
        assert _state() == State.SCOPE;
        _enterState(State.THEME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTheme()
     */
    public void endTheme() throws MIOException {
        _leaveState(State.THEME);
        assert _state() == State.SCOPE;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectIdentifier(java.lang.String)
     */
    public void subjectIdentifier(String subjectIdentifier) throws MIOException {
        Locator sid = _tm.createLocator(subjectIdentifier);
        Topic topic = _peekTopic();
        Topic existing = _tm.getTopicBySubjectIdentifier(sid);
        if (existing != null && !(existing == topic)) {
            _merge(existing, topic);
        }
        else {
            Construct tmo = _tm.getConstructByItemIdentifier(sid);
            if (tmo != null && tmo instanceof Topic && !tmo.equals(topic)) {
                _merge((Topic) tmo, topic);
            }
        }
        topic.addSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectLocator(java.lang.String)
     */
    public void subjectLocator(String subjectLocator) throws MIOException {
        Locator slo = _tm.createLocator(subjectLocator);
        Topic topic = _peekTopic();
        Topic existing = _tm.getTopicBySubjectLocator(slo);
        if (existing != null && !(existing == topic)) {
            _merge(existing, topic);
        }
        topic.addSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#itemIdentifier(java.lang.String)
     */
    public void itemIdentifier(String itemIdentifier) throws MIOException {
        Locator iid = _tm.createLocator(itemIdentifier);
        Construct tmo = _peekConstruct();
        if (_state() == State.TOPIC) {
            Construct existing = _tm.getConstructByItemIdentifier(iid);
            if (existing != null && existing instanceof Topic && !existing.equals(tmo)) {
                _merge((Topic) existing, (Topic) tmo);
            }
            else {
                Topic topic = _tm.getTopicBySubjectIdentifier(iid);
                if (topic != null && !topic.equals(tmo)) {
                    _merge(topic, (Topic) tmo);
                }
            }
        }
        tmo.addItemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startIsa()
     */
    public void startIsa() throws MIOException {
        assert _state() == State.TOPIC;
        _enterState(State.ISA);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endIsa()
     */
    public void endIsa() throws MIOException {
        _leaveState(State.ISA);
        assert _state() == State.TOPIC;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startReifier()
     */
    public void startReifier() throws MIOException {
        assert _peekConstruct() instanceof Reifiable;
        _enterState(State.REIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endReifier()
     */
    public void endReifier() throws MIOException {
        _leaveState(State.REIFIER);
        assert _peekConstruct() instanceof Reifiable;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#topicRef(com.semagia.mio.IRef)
     */
    public void topicRef(IRef identity) throws MIOException {
        _handleTopic(_createTopic(identity));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String)
     */
    public void value(String value) throws MIOException {
        assert _state() == State.NAME;
        ((Name) _peekConstruct()).setValue(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String, java.lang.String)
     */
    public void value(String value, String datatype) throws MIOException {
        ((ILiteralAware) _peekConstruct()).setLiteral(Literal.create(value, datatype));
    }

    /**
     * Enters a state.
     *
     * @param state The state to push ontop of the state stack.
     */
    private void _enterState(State state) {
        _stateStack.add(state);
    }

    /**
     * Enters a state and pushes the Topic Maps construct ontop of the construct
     * stack.
     *
     * @param state The state to enter.
     * @param tmo The Topic Maps construct which should be pushed to the stack.
     */
    private void _enterState(State state, Construct tmo) {
        _enterState(state);
        _constructStack.add(tmo);
    }

    /**
     * Leaves a state.
     *
     * @param state The state to leave.
     * @throws MIOException If the state is not equals to the current state.
     */
    private void _leaveState(State state) throws MIOException {
        State current = _stateStack.remove(_stateStack.size()-1);
        if (state != current) {
            _reportError("Unexpected state: " + current + ", expected: " + state);
        }
    }

    /**
     * Leaves a state and removed the Topic Maps construct from the top of the
     * construct stack.
     *
     * @param state The state to leave.
     * @throws MIOException If the state is not equals to the current state.
     */
    private Construct _leaveStatePopConstruct(State state) throws MIOException {
        _leaveState(state);
        return _constructStack.remove(_constructStack.size()-1);
    }

    /**
     * Returns the Topic Maps construct on top of the stack.
     *
     * @return The Topic Maps construct.
     */
    private Construct _peekConstruct() {
        return _constructStack.get(_constructStack.size()-1);
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
    private State _state() {
        return _stateStack.get(_stateStack.size()-1);
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
    private void _merge(Topic source, Topic target) {
        int i = _constructStack.indexOf(source);
        while (i > -1) {
            _constructStack.set(i, target);
            i = _constructStack.indexOf(source);
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
    private Topic _createTopic(IRef ref) throws MIOException {
        Locator loc = _tm.createLocator(ref.getIRI());
        switch (ref.getType()) {
            case IRef.ITEM_IDENTIFIER: return _tm.createTopicByItemIdentifier(loc);
            case IRef.SUBJECT_IDENTIFIER: return _tm.createTopicBySubjectIdentifier(loc);
            case IRef.SUBJECT_LOCATOR: return _tm.createTopicBySubjectLocator(loc);
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
