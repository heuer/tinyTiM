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

import java.util.Map;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.IEventHandler;
import org.tinytim.internal.api.IEventPublisher;
import org.tinytim.internal.api.IEventPublisherAware;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * The identity manager takes care about the TMDM identity constraints and
 * provides an index to get Topic Maps constructs by their identity.
 * <p>
 * This class is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class IdentityManager implements IEventPublisherAware {

    private final Map<Locator, Topic> _sid2Topic;
    private final Map<Locator, Topic> _slo2Topic;
    private final Map<Locator, IConstruct> _iid2Construct;
    private final Map<String, IConstruct> _id2Construct;

    IdentityManager(MemoryTopicMap tm) {
        _id2Construct = CollectionFactory.createMap(IConstant.IDENTITY_ID2CONSTRUCT_SIZE);
        _sid2Topic = CollectionFactory.createIdentityMap(IConstant.IDENTITY_SID2TOPIC_SIZE);
        _slo2Topic = CollectionFactory.createIdentityMap(IConstant.IDENTITY_SLO2TOPIC_SIZE);
        _iid2Construct = CollectionFactory.createIdentityMap(IConstant.IDENTITY_IID2CONSTRUCT_SIZE);
        subscribe(tm);
        _register(tm);
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IEventPublisherAware#subscribe(org.tinytim.internal.api.IEventPublisher)
     */
    public void subscribe(IEventPublisher publisher) {
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

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IEventPublisherAware#unsubscribe(org.tinytim.internal.api.IEventPublisher)
     */
    public void unsubscribe(IEventPublisher publisher) {
        // noop.
    }

    /**
     * Registeres a Topic Maps construct and, if necessary, gives it an id.
     *
     * @param construct The construct to register.
     */
    private void _register(IConstruct construct) {
        ConstructImpl c = (ConstructImpl) construct;
        if (c._id == null) {
            c._id = String.valueOf(IdGenerator.nextId());
        }
        if (!_id2Construct.containsKey(c._id)) {
            _id2Construct.put(c._id, c);
        }
    }

    /**
     * Unregisteres the specified <tt>construct</tt>.
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
     * @return A Topic Maps construct with the <tt>id</tt> or <tt>null</tt>.
     */
    public Construct getConstructById(String id) {
        return _id2Construct.get(id);
    }

    /**
     * Returns a topic by its subject identifier.
     *
     * @param sid The subject identifier.
     * @return A topic with the <tt>sid</tt> or <tt>null</tt>.
     */
    public Topic getTopicBySubjectIdentifier(Locator sid) {
        return _sid2Topic.get(sid);
    }

    /**
     * Returns a topic by its subject locator.
     *
     * @param slo The subject locator.
     * @return A topic with the <tt>slo</tt> or <tt>null</tt>.
     */
    public Topic getTopicBySubjectLocator(Locator slo) {
        return _slo2Topic.get(slo);
    }

    /**
     * Returns a Topic Maps construct by its item identifier.
     *
     * @param iid The item identifier.
     * @return A Topic Maps construct with the <tt>iid</tt> or <tt>null</tt>.
     */
    public Construct getConstructByItemIdentifier(Locator iid) {
        return _iid2Construct.get(iid);
    }

    public void clear() {
        _id2Construct.clear();
        _iid2Construct.clear();
        _sid2Topic.clear();
        _slo2Topic.clear();
    }

    public void close() {
        clear();
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
                    if (sender.isTopic() && existing.isTopic()) {
                        throw new IdentityConstraintException((Topic) sender, (Topic) existing, iid, "A topic with the same item identifier '" + iid.getReference() + "' exists");
                    }
                    throw new IdentityConstraintException(sender, existing, iid, "A Topic Maps construct with the same item identifier '" + iid.getReference() + "' exists");
                }
            }
            if (sender.isTopic()) {
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
                throw new IdentityConstraintException(topic, (Topic) existing, sid, "A topic with the same subject identifier '" + sid.getReference() + "' exists");
            }
            existing = _iid2Construct.get(sid);
            if (existing != null && existing.isTopic() && existing != topic) {
                throw new IdentityConstraintException(topic, (Topic) existing, sid, "A topic with an item identifier equals to the subject identifier '" + sid.getReference() + "' exists");
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
            _sid2Topic.remove(oldValue);
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
                throw new IdentityConstraintException(topic, existing, slo, "A topic with the same subject locator '" + slo.getReference() + "' exists");
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
            _slo2Topic.remove(oldValue);
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
            Reifiable currentReified = ((Topic) newValue).getReified();
            if (currentReified != null && currentReified != sender) {
                    throw new ModelConstraintException(sender, "The topic reifies another Topic Maps construct");
            }
        }
    }

}
