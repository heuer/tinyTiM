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
package org.tinytim.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.AssociationImpl;
import org.tinytim.AssociationRoleImpl;
import org.tinytim.Event;
import org.tinytim.ICollectionFactory;
import org.tinytim.IConstruct;
import org.tinytim.IEventHandler;
import org.tinytim.IEventPublisher;
import org.tinytim.ITyped;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicNameImpl;
import org.tmapi.core.Topic;

/**
 * {@link ITypeInstanceIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TypeInstanceIndex implements ITypeInstanceIndex {

    private Map<Topic, Set<Topic>> _type2Topics;
    private Map<Topic, List<AssociationImpl>> _type2Assocs;
    private Map<Topic, List<AssociationRoleImpl>> _type2Roles;
    private Map<Topic, List<OccurrenceImpl>> _type2Occs;
    private Map<Topic, List<TopicNameImpl>> _type2Names;

    public TypeInstanceIndex(IEventPublisher publisher, ICollectionFactory collFactory) {
        _type2Topics = collFactory.createMap();
        _type2Assocs = collFactory.createMap();
        _type2Roles = collFactory.createMap();
        _type2Occs = collFactory.createMap();
        _type2Names = collFactory.createMap();
        IEventHandler handler = new TopicTypeHandler();
        publisher.subscribe(Event.ADD_TYPE, handler);
        publisher.subscribe(Event.REMOVE_TYPE, handler);
        handler = new AddTopicHandler();
        publisher.subscribe(Event.ADD_TOPIC, handler);
        handler = new RemoveTopicHandler();
        publisher.subscribe(Event.REMOVE_TOPIC, handler);
        handler = new TypeHandler();
        publisher.subscribe(Event.SET_TYPE, handler);
        handler = new RemoveTypedHandler();
        publisher.subscribe(Event.REMOVE_ASSOCIATION, handler);
        publisher.subscribe(Event.REMOVE_ROLE, handler);
        publisher.subscribe(Event.REMOVE_OCCURRENCE, handler);
        publisher.subscribe(Event.REMOVE_NAME, handler);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getAssociationTypes()
     */
    public Collection<Topic> getAssociationTypes() {
        List<Topic> topics = new ArrayList<Topic>(_type2Assocs.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
     */
    public Collection<AssociationImpl> getAssociations(Topic type) {
        List<AssociationImpl> assocs = _type2Assocs.get(type);
        return assocs == null ? Collections.<AssociationImpl>emptySet()
                              : new ArrayList<AssociationImpl>(assocs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getRoleTypes()
     */
    public Collection<Topic> getRoleTypes() {
        List<Topic> topics = new ArrayList<Topic>(_type2Roles.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getRoles(org.tmapi.core.Topic)
     */
    public Collection<AssociationRoleImpl> getRoles(Topic type) {
        List<AssociationRoleImpl> roles = _type2Roles.get(type);
        return roles == null ? Collections.<AssociationRoleImpl>emptySet()
                             : new ArrayList<AssociationRoleImpl>(roles);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getOccurrenceTypes()
     */
    public Collection<Topic> getOccurrenceTypes() {
        List<Topic> topics = new ArrayList<Topic>(_type2Occs.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
     */
    public Collection<OccurrenceImpl> getOccurrences(Topic type) {
        List<OccurrenceImpl> occs = _type2Occs.get(type);
        return occs == null ? Collections.<OccurrenceImpl>emptySet()
                            : new ArrayList<OccurrenceImpl>(occs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getNameTypes()
     */
    public Collection<Topic> getNameTypes() {
        List<Topic> topics = new ArrayList<Topic>(_type2Names.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getNames(org.tmapi.core.Topic)
     */
    public Collection<TopicNameImpl> getNames(Topic type) {
        List<TopicNameImpl> names = _type2Names.get(type);
        return names == null ? Collections.<TopicNameImpl>emptySet()
                             : new ArrayList<TopicNameImpl>(names);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopicTypes()
     */
    public Collection<Topic> getTopicTypes() {
        List<Topic> topics = new ArrayList<Topic>(_type2Topics.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[])
     */
    public Collection<Topic> getTopics(Topic type) {
        Set<Topic> topics = _type2Topics.get(type);
        return topics == null ? Collections.<Topic>emptySet()
                              : new ArrayList<Topic>(topics);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
        if (types.length == 1) {
            return getTopics(types[0]);
        }
        if (!matchAll) {
            Set<Topic> topics = new HashSet<Topic>();
            for (Topic type: types) {
                Set<Topic> matches = _type2Topics.get(type);
                if (matches != null) {
                    topics.addAll(matches);
                }
            }
            return topics;
        }
        else {
            Set<Topic> topics = new HashSet<Topic>(getTopics(types[0]));
            for (int i=1; i < types.length; i++) {
                topics.retainAll(getTopics(types[i]));
            }
            return topics;
        }
    }

    private boolean _unindex(List<ITyped> objects, Object obj) {
        if (objects == null) {
            return false;
        }
        objects.remove(obj);
        return objects.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndex#close()
     */
    public void close() {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndex#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndex#reindex()
     */
    public void reindex() {
        // noop.
    }

    private final class AddTopicHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) newValue;
            Collection<Topic> types = topic.getTypes();
            if (types.isEmpty()) {
               Set<Topic> topics = _type2Topics.get(null);
                if (topics == null) {
                    topics = new HashSet<Topic>();
                    _type2Topics.put(null, topics);
                }
                topics.add(topic);
            }
            else {
                for (Topic type: types) {
                    Set<Topic> topics = _type2Topics.get(type);
                    if (topics == null) {
                        topics = new HashSet<Topic>();
                        _type2Topics.put(type, topics);
                    }
                    topics.add(topic);
                }
            }
        }
    }

    private final class RemoveTopicHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) oldValue;
            Set<Topic> topics = _type2Topics.get(null);
            if (topics != null) {
                topics.remove(topic);
            }
            Collection<Topic> types = topic.getTypes();
            for (Topic type: types) {
                topics = _type2Topics.get(type);
                if (topics != null) {
                    topics.remove(topic);
                }
            }
        }
    }

    /**
     * Handler that (un-)indexes topics by their type.
     */
    private final class TopicTypeHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) sender;
            if (oldValue == null) {
                // Adding a type
                Set<Topic> topics = _type2Topics.get(newValue);
                if (topics == null) {
                    topics = new HashSet<Topic>();
                    _type2Topics.put((Topic) newValue, topics);
                }
                topics.add(topic);
                topics = _type2Topics.get(null);
                if (topics != null) {
                    topics.remove(topic);
                }
            }
            else {
                Set<Topic> topics = _type2Topics.get(oldValue);
                if (topics == null) {
                    return;
                }
                topics.remove(topic);
                if (topics.isEmpty()) {
                    _type2Topics.remove(oldValue);
                }
                if (topic.getTypes().size() == 1) {
                    topics = _type2Topics.get(null);
                    if (topics == null) {
                        topics = new HashSet<Topic>();
                        _type2Topics.put(null, topics);
                    }
                    topics.add(topic);
                }
            }
        }
    }

    private final class TypeHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Map<Topic, ?> type2Typed = null;
            if (sender instanceof AssociationImpl) {
                type2Typed = _type2Assocs;
            }
            else if (sender instanceof AssociationRoleImpl) {
                type2Typed = _type2Roles;
            }
            else if (sender instanceof OccurrenceImpl) {
                type2Typed = _type2Occs;
            }
            else if (sender instanceof TopicNameImpl) {
                type2Typed = _type2Names;
            }
            if (_unindex((List<ITyped>)type2Typed.get((Topic)oldValue), sender)) {
                type2Typed.remove(oldValue);
            }
            _index((Map<Topic, List<ITyped>>) type2Typed, (Topic) newValue, (ITyped) sender);
        }

        private void _index(Map<Topic, List<ITyped>> type2Typed,
                Topic newValue, ITyped sender) {
            List<ITyped> typedConstructs = type2Typed.get(newValue);
            if (typedConstructs == null) {
                typedConstructs = new ArrayList<ITyped>();
                type2Typed.put(newValue, typedConstructs);
            }
            typedConstructs.add(sender);
        }
    }

    private final class RemoveTypedHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Map<Topic, ?> type2Typed = null;
            if (oldValue instanceof AssociationImpl) {
                type2Typed = _type2Assocs;
            }
            else if (oldValue instanceof AssociationRoleImpl) {
                type2Typed = _type2Roles;
            }
            else if (oldValue instanceof OccurrenceImpl) {
                type2Typed = _type2Occs;
            }
            else if (oldValue instanceof TopicNameImpl) {
                type2Typed = _type2Names;
            }
            Topic type = ((ITyped) oldValue).getType();
            if (_unindex((List<ITyped>)type2Typed.get(type), oldValue)) {
                type2Typed.remove(type);
            }
        }
    }

    void clear() {
        _type2Topics.clear();
        _type2Topics = null;
        _type2Assocs.clear();
        _type2Assocs = null;
        _type2Roles.clear();
        _type2Roles = null;
        _type2Occs.clear();
        _type2Occs = null;
        _type2Names.clear();
        _type2Names = null;
    }
}
