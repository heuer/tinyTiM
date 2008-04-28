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
package org.tinytim.mio;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.tinytim.IReifiable;
import org.tinytim.ITyped;
import org.tinytim.TopicMapImpl;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * {@link com.semagia.mio.IMapHandler} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class MapInputHandler implements IMapHandler {

    private enum State {
        INITIAL, TOPIC, ASSOCIATION, ROLE, OCCURRENCE, NAME, VARIANT,
        SCOPE, THEME, REIFIER, PLAYER, ISA, TYPE;
    }

    private static final String _XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
    private static final String _XSD_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

    private static final Logger LOG = Logger.getLogger(MapInputHandler.class.getName());

    private TopicMapImpl _tm;
    private State[] _stateStack;
    private int _stackPointer;
    private List<TopicMapObject> _constructStack;

    /**
     * Sets the topic map instance to operate on.
     *
     * @param tm The topic map.
     */
    public void setTopicMap(TopicMap tm) {
        _tm = (TopicMapImpl) tm;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopicMap()
     */
    public void startTopicMap() throws MIOException {
        _constructStack = new ArrayList<TopicMapObject>();
        _stateStack = new State[15];
        _stackPointer = -1;
        _enterState(State.INITIAL, _tm);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopicMap()
     */
    public void endTopicMap() throws MIOException {
        if (_state() != State.INITIAL) {
            LOG.warning("The topic map import seems to be unfinished due to errors");
        }
        //TODO: Convert type-instance assocs.
        _constructStack = null;
        _stateStack = null;
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
        Topic topic = _peekTopic();
        _leaveStatePopConstruct(State.TOPIC);
        _handleTopic(topic);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startAssociation()
     */
    public void startAssociation() throws MIOException {
        _enterState(State.ASSOCIATION, _tm.createAssociation());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endAssociation()
     */
    public void endAssociation() throws MIOException {
        _leaveStatePopConstruct(State.ASSOCIATION);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startRole()
     */
    public void startRole() throws MIOException {
        assert _state() == State.ASSOCIATION;
        _enterState(State.ROLE, ((Association) _peekConstruct()).createAssociationRole(null, null));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endRole()
     */
    public void endRole() throws MIOException {
        _leaveStatePopConstruct(State.ROLE);
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
        _enterState(State.OCCURRENCE, _peekTopic().createOccurrence((Locator) null, null, null));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endOccurrence()
     */
    public void endOccurrence() throws MIOException {
        _leaveStatePopConstruct(State.OCCURRENCE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startName()
     */
    public void startName() throws MIOException {
        _enterState(State.NAME, _peekTopic().createTopicName(null, null));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endName()
     */
    public void endName() throws MIOException {
        _leaveStatePopConstruct(State.NAME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startVariant()
     */
    public void startVariant() throws MIOException {
        assert _state() == State.NAME;
        _enterState(State.VARIANT, ((TopicName) _peekConstruct()).createVariant((Locator) null, null));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endVariant()
     */
    public void endVariant() throws MIOException {
        _leaveStatePopConstruct(State.VARIANT);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startType()
     */
    public void startType() throws MIOException {
        assert _peekConstruct() instanceof ITyped;
        _enterState(State.TYPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endType()
     */
    public void endType() throws MIOException {
        _leaveState(State.TYPE);
        assert _peekConstruct() instanceof ITyped;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startScope()
     */
    public void startScope() throws MIOException {
        assert _peekConstruct() instanceof ScopedObject;
        _enterState(State.SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endScope()
     */
    public void endScope() throws MIOException {
        _leaveState(State.SCOPE);
        assert _peekConstruct() instanceof ScopedObject;
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
        if (existing != null && !existing.equals(topic)) {
            _merge(existing, topic);
        }
        else {
            TopicMapObject tmo = _tm.getObjectByItemIdentifier(sid);
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
        if (existing != null && !existing.equals(topic)) {
            _merge(existing, topic);
        }
        topic.addSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#itemIdentifier(java.lang.String)
     */
    public void itemIdentifier(String itemIdentifier) throws MIOException {
        Locator iid = _tm.createLocator(itemIdentifier);
        TopicMapObject tmo = _peekConstruct();
        if (_state() == State.TOPIC) {
            TopicMapObject existing = _tm.getObjectByItemIdentifier(iid);
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
        tmo.addSourceLocator(iid);
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
        assert _peekConstruct() instanceof IReifiable;
        _enterState(State.REIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endReifier()
     */
    public void endReifier() throws MIOException {
        _leaveState(State.REIFIER);
        assert _peekConstruct() instanceof IReifiable;
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
        ((TopicName) _peekConstruct()).setValue(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String, java.lang.String)
     */
    public void value(String value, String datatype) throws MIOException {
        boolean isLocator = _XSD_ANY_URI.equals(datatype);
        if (!isLocator && _XSD_STRING.equals(datatype)) {
            LOG.warning("The datatype '" + datatype + "' was converted into xsd:string");
        }
        if (_state() == State.OCCURRENCE) {
            Occurrence occ = (Occurrence) _peekConstruct();
            if (isLocator) {
                occ.setResource(_tm.createLocator(value));
            }
            else {
                occ.setValue(value);
            }
        }
        else {
            assert _state() == State.VARIANT;
            Variant variant = (Variant) _peekConstruct();
            if (isLocator) {
                variant.setResource(_tm.createLocator(value));
            }
            else {
                variant.setValue(value);
            }
        }
    }

    /**
     * Enters a state.
     *
     * @param state The state to push ontop of the state stack.
     */
    private void _enterState(State state) {
        _stateStack[++_stackPointer] = state;
    }

    /**
     * Enters a state and pushes the Topic Maps construct ontop of the construct
     * stack.
     *
     * @param state The state to enter.
     * @param tmo The Topic Maps construct which should be pushed to the stack.
     */
    private void _enterState(State state, TopicMapObject tmo) {
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
        if (!(state == _stateStack[_stackPointer])) {
            _reportError("Unexpected state: " + _stateStack[_stackPointer] + ", expected: " + state);
        }
        --_stackPointer;
    }

    /**
     * Leaves a state and removed the Topic Maps construct from the top of the
     * construct stack.
     *
     * @param state The state to leave.
     * @throws MIOException If the state is not equals to the current state.
     */
    private void _leaveStatePopConstruct(State state) throws MIOException {
        _leaveState(state);
        _constructStack.remove(_constructStack.size()-1);
    }

    /**
     * Returns the Topic Maps construct on top of the stack.
     *
     * @return The Topic Maps construct.
     */
    private TopicMapObject _peekConstruct() {
        return _constructStack.get(_constructStack.size()-1);
    }

    /**
     * Returns the topic on top of the stack.
     *
     * @return The topic.
     */
    private Topic _peekTopic() {
        return (Topic) _peekConstruct();
    }

    /**
     * Returns the current state.
     *
     * @return The current state.
     */
    private State _state() {
        return _stateStack[_stackPointer];
    }

    /**
     * Handles the topic dependent on the current state.
     *
     * @param topic The topic to handle.
     */
    private void _handleTopic(Topic topic) {
        switch (_state()) {
            case ISA: _peekTopic().addType(topic); break;
            case TYPE: ((ITyped) _peekConstruct()).setType(topic); break;
            case PLAYER: ((AssociationRole) _peekConstruct()).setPlayer(topic); break;
            case THEME: ((ScopedObject) _peekConstruct()).addScopingTopic(topic); break;
            case REIFIER: ((IReifiable) _peekConstruct()).setReifier(topic); break;
        }
    }

    /**
     * Merges the <code>source</code> topic with the <code>target</code>.
     * 
     * Further, this method ensures that the construct stack stays valid: If
     * the <code>source</code> is part of the stack, it is replaced with
     * <code>target</code>.
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
            case IRef.ITEM_IDENTIFIER: return _topicByItemIdentifier(loc);
            case IRef.SUBJECT_IDENTIFIER: return _topicBySubjectIdentifier(loc);
            case IRef.SUBJECT_LOCATOR: return _topicBySubjectLocator(loc);
            default: _reportError("Unknown reference type " + ref.getType());
        }
        // Never returned, an exception was thrown
        return null;
    }

    /**
     * Returns either an existing topic with the specified item identfier,
     * or creates a topic with the given item identifier.
     *
     * @param iid The item identifier of the topic.
     * @return A topic instance.
     */
    private Topic _topicByItemIdentifier(Locator iid) {
        TopicMapObject tmo = _tm.getObjectByItemIdentifier(iid);
        Topic topic = (tmo instanceof Topic) ? (Topic) tmo : null;
        if (topic == null) {
            topic = _tm.getTopicBySubjectIdentifier(iid);
        }
        if (topic == null) {
            topic = _tm.createTopic();
            topic.addSourceLocator(iid);
        }
        return topic;
    }

    /**
     * Returns either an existing topic with the specified subject identfier,
     * or creates a topic with the given subject identifier.
     *
     * @param sid The subject identifier of the topic.
     * @return A topic instance.
     */
    private Topic _topicBySubjectIdentifier(Locator sid) {
        Topic topic = _tm.getTopicBySubjectIdentifier(sid);
        if (topic == null) {
            TopicMapObject tmo = _tm.getObjectByItemIdentifier(sid);
            if (tmo instanceof Topic) {
                topic = (Topic) tmo;
            }
        }
        if (topic == null) {
            topic = _tm.createTopic();
            topic.addSubjectIdentifier(sid);
        }
        return topic;
    }

    /**
     * Returns either an existing topic with the specified subject locator,
     * or creates a topic with the given subject locator.
     *
     * @param slo The subject locator of the topic.
     * @return A topic instance.
     */
    private Topic _topicBySubjectLocator(Locator slo) {
        Topic topic = _tm.getTopicBySubjectLocator(slo);
        if (topic == null) {
            topic = _tm.createTopic();
            topic.addSubjectLocator(slo);
        }
        return topic;
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
