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

import java.util.Map;

import org.tinytim.utils.ICollectionFactory;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * The identity manager takes care about the TMDM identity constraints and
 * provides an index to get Topic Maps constructs by their identity.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class IdentityManager {

    private Map<Locator, Topic> _sid2Topic;
    private Map<Locator, Topic> _slo2Topic;
    private Map<Locator, Construct> _iid2Construct;
    private Map<String, Construct> _id2Construct;

    IdentityManager(TopicMapImpl tm) {
        ICollectionFactory collFactory = tm.getCollectionFactory();
        _id2Construct = collFactory.createIdentityMap();
        _sid2Topic = collFactory.createIdentityMap();
        _slo2Topic = collFactory.createIdentityMap();
        _iid2Construct = collFactory.createIdentityMap();
        _subscribe(tm);
        _register(tm);
    }

    /**
     * Subscribes itself to the specified event publisher.
     *
     * @param publisher The publisher to subscribe to.
     */
    private void _subscribe(IEventPublisher publisher) {
        IEventHandler handler = new TopicMapsConstructAddHandler();
        publisher.subscribe(Event.ADD_TOPIC, handler);
        publisher.subscribe(Event.ADD_ASSOCIATION, handler);
        publisher.subscribe(Event.ADD_ROLE, handler);
        publisher.subscribe(Event.ADD_OCCURRENCE, handler);
        publisher.subscribe(Event.ADD_NAME, handler);
        publisher.subscribe(Event.ADD_VARIANT, handler);
        handler = new TopicMapsConstructRemoveHandler();
        publisher.subscribe(Event.REMOVE_TOPIC, handler);
        publisher.subscribe(Event.REMOVE_ASSOCIATION, handler);
        publisher.subscribe(Event.REMOVE_ROLE, handler);
        publisher.subscribe(Event.REMOVE_OCCURRENCE, handler);
        publisher.subscribe(Event.REMOVE_NAME, handler);
        publisher.subscribe(Event.REMOVE_VARIANT, handler);
        handler = new AddItemIdentifierHandler();
        publisher.subscribe(Event.ADD_IID, handler);
        handler = new RemoveItemIdentifierHandler();
        publisher.subscribe(Event.REMOVE_IID, handler);
        handler = new AddSubjectIdentifierHandler();
        publisher.subscribe(Event.ADD_SID, handler);
        handler = new RemoveSubjectIdentifierHandler();
        publisher.subscribe(Event.REMOVE_SID, handler);
        handler = new AddSubjectLocatorHandler();
        publisher.subscribe(Event.ADD_SLO, handler);
        handler = new RemoveSubjectLocatorHandler();
        publisher.subscribe(Event.REMOVE_SLO, handler);
        handler = new ReifierConstraintHandler();
        publisher.subscribe(Event.SET_REIFIER, handler);
    }

    /**
     * Registeres a Topic Maps construct and, if necessary, gives it an id.
     *
     * @param construct The construct to register.
     */
    private void _register(Construct construct) {
        ConstructImpl c = (ConstructImpl) construct;
        if (c._id == null) {
            String id = "" + IdGenerator.getInstance().nextId();
            c._id = id.intern();
        }
        if (!_id2Construct.containsKey(c._id)) {
            _id2Construct.put(c._id, c);
        }
    }

    /**
     * Unregisteres the specified <code>construct</code>.
     *
     * @param construct The Topic Maps construct to unregister.
     */
    private void _unregister(Construct construct) {
        _id2Construct.remove(((ConstructImpl) construct)._id);
    }

    /**
     * Returns a Topic Maps construct by its identifier.
     *
     * @param id The identifier.
     * @return A Topic Maps construct with the <code>id</code> or <code>null</code>.
     */
    public Construct getConstructById(String id) {
        return _id2Construct.get(id);
    }

    /**
     * Returns a topic by its subject identifier.
     *
     * @param sid The subject identifier.
     * @return A topic with the <code>sid</code> or <code>null</code>.
     */
    public Topic getTopicBySubjectIdentifier(Locator sid) {
        return _sid2Topic.get(sid);
    }

    /**
     * Returns a topic by its subject locator.
     *
     * @param slo The subject locator.
     * @return A topic with the <code>slo</code> or <code>null</code>.
     */
    public Topic getTopicBySubjectLocator(Locator slo) {
        return _slo2Topic.get(slo);
    }

    /**
     * Returns a Topic Maps construct by its item identifier.
     *
     * @param iid The item identifier.
     * @return A Topic Maps construct with the <code>iid</code> or <code>null</code>.
     */
    public Construct getConstructByItemIdentifier(Locator iid) {
        return _iid2Construct.get(iid);
    }

    public void close() {
        _id2Construct = null;
        _iid2Construct = null;
        _sid2Topic = null;
        _slo2Topic = null;
    }

    private class TopicMapsConstructAddHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            _register((ConstructImpl)newValue);
        }
    }

    private class TopicMapsConstructRemoveHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            _unregister((ConstructImpl)oldValue);
        }
    }

    /**
     * Checks identity constraints and adds the Topic Maps construct and the 
     * item identifier to the index. 
     */
    private class AddItemIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            Locator iid = (Locator) newValue;
            Construct existing = _iid2Construct.get(iid);
            if (existing != null) {
                if (existing != sender) {
                    if (sender instanceof Topic && existing instanceof Topic) {
                        throw new IdentityConstraintException((Topic) sender, (Topic) existing, iid, "A topic with the same item identifier '" + iid.getReference() + "' exists");
                    }
                    throw new IdentityConstraintException(sender, existing, iid, "A Topic Maps construct with the same item identifier '" + iid.getReference() + "' exists");
                }
            }
            if (sender instanceof Topic) {
                Topic existingTopic = _sid2Topic.get(iid);
                if (existingTopic != null && existingTopic != sender) {
                    throw new IdentityConstraintException((Topic) sender, existingTopic, iid, "A topic with a subject identifier equals to the item identifier '" + iid.getReference() + "' exists");
                }
            }
            _iid2Construct.put(iid, sender);
        }
    }

    /**
     * Removes an item identifier and its Topic Maps constructs from the index. 
     */
    private class RemoveItemIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            _iid2Construct.remove(oldValue);
        }
    }

    /**
     * Checks identity constraints and adds the topic and the 
     * subject identifier to the index. 
     */
    private class AddSubjectIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) sender;
            Locator sid = (Locator) newValue;
            Construct existing = _sid2Topic.get(sid);
            if (existing != null && existing != topic) {
                throw new IdentityConstraintException(topic, (Topic) existing, sid, "A topic with the same subject identifier '" + sid.getReference() + "' exists");
            }
            existing = _iid2Construct.get(sid);
            if (existing != null && existing instanceof Topic && existing != topic) {
                throw new IdentityConstraintException(topic, (Topic) existing, sid, "A topic with an item identifier equals to the subject identifier '" + sid.getReference() + "' exists");
            }
            _sid2Topic.put(sid, topic);
        }
    }

    /**
     * Removes a subject identifier and its topic from the index. 
     */
    private class RemoveSubjectIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            _sid2Topic.remove(oldValue);
        }
    }

    /**
     * Checks identity constraints and adds the topic and the 
     * subject locator to the index. 
     */
    private class AddSubjectLocatorHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) sender;
            Locator slo = (Locator) newValue;
            Topic existing = _slo2Topic.get(slo);
            if (existing != null && existing != topic) {
                throw new IdentityConstraintException(topic, existing, slo, "A topic with the same subject locator '" + slo.getReference() + "' exists");
            }
            _slo2Topic.put(slo, topic);
        }
    }

    /**
     * Removes a subject locator and its topic from the index. 
     */
    private class RemoveSubjectLocatorHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            _slo2Topic.remove(oldValue);
        }
    }

    /**
     * Checks if setting the reifier is allowed.
     */
    private static class ReifierConstraintHandler implements IEventHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            if (newValue == null) {
                return;
            }
            Reifiable currentReified = ((Topic) newValue).getReified();
            if (currentReified != null && currentReified != sender) {
                    throw new ModelConstraintException(sender, "The topic reifies another Topic Maps construct");
            }
        }
    }
}
