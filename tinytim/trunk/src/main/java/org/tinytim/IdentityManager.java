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

import java.util.Map;

import org.tmapi.core.DuplicateSourceLocatorException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicsMustMergeException;

/**
 * The identity manager takes care about the TMDM identity constraints and
 * provides an index to get Topic Maps constructs by their identity.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class IdentityManager {

    private long _nextId;
    private Map<Locator, Topic> _sid2Topic;
    private Map<Locator, Topic> _slo2Topic;
    private Map<Locator, IConstruct> _iid2Construct;
    private Map<String, IConstruct> _id2Construct;

    IdentityManager(TopicMapImpl tm) {
        ICollectionFactory collFactory = tm.getCollectionFactory();
        _id2Construct = collFactory.<String, IConstruct>createMap();
        _sid2Topic = collFactory.<Locator, Topic>createMap();
        _slo2Topic = collFactory.<Locator, Topic>createMap();
        _iid2Construct = collFactory.<Locator, IConstruct>createMap();
        _subscribe(tm);
        _register(tm);
    }

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

    private void _register(IConstruct construct) {
        Construct c = (Construct) construct;
        if (c._id == null) {
            c._id = "" + _nextId++;
        }
        if (!_id2Construct.containsKey(c)) {
            _id2Construct.put(c._id, c);
        }
    }

    private void _unregister(IConstruct construct) {
        _id2Construct.remove(((Construct) construct)._id);
    }

    public IConstruct getConstructById(String id) {
        return _id2Construct.get(id);
    }

    public Topic getTopicBySubjectIdentifier(Locator sid) {
        return _sid2Topic.get(sid);
    }

    public Topic getTopicBySubjectLocator(Locator slo) {
        return _slo2Topic.get(slo);
    }

    public IConstruct getConstructByItemIdentifier(Locator iid) {
        return _iid2Construct.get(iid);
    }

    public void close() {
        _id2Construct = null;
        _iid2Construct = null;
        _sid2Topic = null;
        _slo2Topic = null;
    }

    private class TopicMapsConstructAddHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            _register((IConstruct)newValue);
        }
    }

    private class TopicMapsConstructRemoveHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            _unregister((IConstruct)oldValue);
        }
    }

    /**
     * Checks identity constraints and adds the Topic Maps construct and the 
     * item identifier to the index. 
     */
    private class AddItemIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Locator iid = (Locator) newValue;
            IConstruct existing = _iid2Construct.get(iid);
            if (existing != null) {
                if (existing != sender) {
                    if (sender instanceof Topic && existing instanceof Topic) {
                        throw new TopicsMustMergeException((Topic) sender, (Topic) existing, "A topic with the same item identifier '" + iid.getReference() + "' exists");
                    }
                    throw new DuplicateSourceLocatorException(sender, existing, iid, "A Topic Maps construct with the same item identifier '" + iid.getReference() + "' exists");
                }
            }
            if (sender instanceof Topic) {
                Topic existingTopic = _sid2Topic.get(iid);
                if (existingTopic != null && existingTopic != sender) {
                    throw new TopicsMustMergeException((Topic) sender, existingTopic, "A topic with a subject identifier equals to the item identifier '" + iid.getReference() + "' exists");
                }
            }
            _iid2Construct.put(iid, sender);
        }
    }

    /**
     * Removes an item identifier and its Topic Maps constructs from the index. 
     */
    private class RemoveItemIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            _iid2Construct.remove(oldValue);
        }
    }

    /**
     * Checks identity constraints and adds the topic and the 
     * subject identifier to the index. 
     */
    private class AddSubjectIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) sender;
            Locator sid = (Locator) newValue;
            IConstruct existing = (IConstruct) _sid2Topic.get(sid);
            if (existing != null && existing != topic) {
                throw new TopicsMustMergeException(topic, (Topic) existing, "A topic with the same subject identifier '" + sid.getReference() + "' exists");
            }
            existing = _iid2Construct.get(sid);
            if (existing != null && existing instanceof Topic && existing != topic) {
                throw new TopicsMustMergeException(topic, (Topic) existing, "A topic with an item identifier equals to the subject identifier '" + sid.getReference() + "' exists");
            }
            _sid2Topic.put(sid, topic);
        }
    }

    /**
     * Removes a subject identifier and its topic from the index. 
     */
    private class RemoveSubjectIdentifierHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Locator slo = (Locator) oldValue;
            _sid2Topic.remove(slo);
        }
    }

    /**
     * Checks identity constraints and adds the topic and the 
     * subject locator to the index. 
     */
    private class AddSubjectLocatorHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) sender;
            Locator slo = (Locator) newValue;
            Topic existing = _slo2Topic.get(slo);
            if (existing != null && existing != topic) {
                throw new TopicsMustMergeException(topic, existing, "A topic with the same subject locator '" + slo.getReference() + "' exists");
            }
            _slo2Topic.put(slo, topic);
        }
    }

    /**
     * Removes a subject locator and its topic from the index. 
     */
    private class RemoveSubjectLocatorHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Locator slo = (Locator) oldValue;
            _slo2Topic.remove(slo);
        }
    }

    /**
     * Checks if setting the reifier is allowed.
     */
    private static class ReifierConstraintHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            if (newValue == null) {
                return;
            }
            IReifiable currentReified = ((TopicImpl) newValue)._reified;
            if (currentReified != null && currentReified != sender) {
                    throw new ModelConstraintException(sender, "The topic reifies another Topic Maps construct");
            }
        }
    }
}
