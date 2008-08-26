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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.core.Event;
import org.tinytim.core.IConstruct;
import org.tinytim.core.IEventHandler;
import org.tinytim.core.IEventPublisher;
import org.tinytim.internal.utils.CollectionFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.index.TypeInstanceIndex;

/**
 * {@link TypeInstanceIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TypeInstanceIndexImpl extends AbstractIndex implements TypeInstanceIndex {

    private Map<Topic, Set<Topic>> _type2Topics;
    private Map<Topic, List<Association>> _type2Assocs;
    private Map<Topic, List<Role>> _type2Roles;
    private Map<Topic, List<Occurrence>> _type2Occs;
    private Map<Topic, List<Name>> _type2Names;

    public TypeInstanceIndexImpl(IEventPublisher publisher) {
        super();
        _type2Topics = CollectionFactory.createIdentityMap();
        _type2Assocs = CollectionFactory.createIdentityMap();
        _type2Roles = CollectionFactory.createIdentityMap();
        _type2Occs = CollectionFactory.createIdentityMap();
        _type2Names = CollectionFactory.createIdentityMap();
        IEventHandler handler = new TopicTypeHandler();
        publisher.subscribe(Event.ADD_TYPE, handler);
        publisher.subscribe(Event.REMOVE_TYPE, handler);
        handler = new AddTopicHandler();
        publisher.subscribe(Event.ADD_TOPIC, handler);
        handler = new RemoveTopicHandler();
        publisher.subscribe(Event.REMOVE_TOPIC, handler);
        handler = new TypeHandler();
        publisher.subscribe(Event.SET_TYPE, handler);
        handler = new AddTypedHandler();
        publisher.subscribe(Event.ADD_ASSOCIATION, handler);
        publisher.subscribe(Event.ADD_ROLE, handler);
        publisher.subscribe(Event.ADD_OCCURRENCE, handler);
        publisher.subscribe(Event.ADD_NAME, handler);
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
        List<Topic> topics = CollectionFactory.createList(_type2Assocs.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
     */
    public Collection<Association> getAssociations(Topic type) {
        List<Association> assocs = _type2Assocs.get(type);
        return assocs == null ? Collections.<Association>emptySet()
                              : CollectionFactory.createList(assocs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getRoleTypes()
     */
    public Collection<Topic> getRoleTypes() {
        List<Topic> topics = CollectionFactory.createList(_type2Roles.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getRoles(org.tmapi.core.Topic)
     */
    public Collection<Role> getRoles(Topic type) {
        List<Role> roles = _type2Roles.get(type);
        return roles == null ? Collections.<Role>emptySet()
                             : CollectionFactory.createList(roles);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getOccurrenceTypes()
     */
    public Collection<Topic> getOccurrenceTypes() {
        List<Topic> topics = CollectionFactory.createList(_type2Occs.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
     */
    public Collection<Occurrence> getOccurrences(Topic type) {
        List<Occurrence> occs = _type2Occs.get(type);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : CollectionFactory.createList(occs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getNameTypes()
     */
    public Collection<Topic> getNameTypes() {
        List<Topic> topics = CollectionFactory.createList(_type2Names.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getNames(org.tmapi.core.Topic)
     */
    public Collection<Name> getNames(Topic type) {
        List<Name> names = _type2Names.get(type);
        return names == null ? Collections.<Name>emptySet()
                             : CollectionFactory.createList(names);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopicTypes()
     */
    public Collection<Topic> getTopicTypes() {
        List<Topic> topics = CollectionFactory.createList(_type2Topics.keySet());
        topics.remove(null);
        return topics;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[])
     */
    public Collection<Topic> getTopics(Topic type) {
        Set<Topic> topics = _type2Topics.get(type);
        return topics == null ? Collections.<Topic>emptySet()
                              : CollectionFactory.createList(topics);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
        if (types.length == 1) {
            return getTopics(types[0]);
        }
        Set<Topic> result =  CollectionFactory.createIdentitySet();
        if (!matchAll) {
            for (Topic type: types) {
                Set<Topic> matches = _type2Topics.get(type);
                if (matches != null) {
                    result.addAll(matches);
                }
            }
        }
        else {
            result.addAll(getTopics(types[0]));
            for (int i=1; i < types.length; i++) {
                result.retainAll(getTopics(types[i]));
            }
        }
        return result;
    }

    private void _index(Map<Topic, List<Typed>> type2Typed, Topic type, Typed typed) {
        List<Typed> list = type2Typed.get(type);
        if (list == null) {
            list = CollectionFactory.createList();
            type2Typed.put(type, list);
        }
        list.add(typed);
    }

    private void _unindex(Map<Topic, List<Typed>> type2Typed, Topic type, Typed typed) {
        List<Typed> list = type2Typed.get(type);
        if (list == null) {
            return;
        }
        list.remove(typed);
        if (list.isEmpty()) {
            type2Typed.remove(type);
        }
    }

    private final class AddTopicHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Topic topic = (Topic) newValue;
            Collection<Topic> types = topic.getTypes();
            if (types.isEmpty()) {
               Set<Topic> topics = _type2Topics.get(null);
                if (topics == null) {
                    topics = CollectionFactory.createIdentitySet();
                    _type2Topics.put(null, topics);
                }
                topics.add(topic);
            }
            else {
                for (Topic type: types) {
                    Set<Topic> topics = _type2Topics.get(type);
                    if (topics == null) {
                        topics = CollectionFactory.createIdentitySet();
                        _type2Topics.put(type, topics);
                    }
                    topics.add(topic);
                }
            }
        }
    }

    private final class RemoveTopicHandler implements IEventHandler {
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
                    topics = CollectionFactory.createIdentitySet();
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
                        topics = CollectionFactory.createIdentitySet();
                        _type2Topics.put(null, topics);
                    }
                    topics.add(topic);
                }
            }
        }
    }

    private abstract class _EvtHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        Map<Topic, List<Typed>> getMap(Typed typed) {
            Map<Topic, ?> type2Typed = null;
            if (typed instanceof Association) {
                type2Typed = _type2Assocs;
            }
            else if (typed instanceof Role) {
                type2Typed = _type2Roles;
            }
            else if (typed instanceof Occurrence) {
                type2Typed = _type2Occs;
            }
            else if (typed instanceof Name) {
                type2Typed = _type2Names;
            }
            return (Map<Topic, List<Typed>>) type2Typed;
        }
    }

    private final class TypeHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Typed typed = (Typed) sender;
            Map<Topic, List<Typed>> map = getMap(typed);
            _unindex(map, (Topic) oldValue, typed);
            _index(map, (Topic) newValue, typed);
        }
    }

    private final class AddTypedHandler extends _EvtHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Typed typed = (Typed) newValue;
            Map<Topic, List<Typed>> map = getMap(typed);
            _index(map, typed.getType(), typed);
        }
    }

    private final class RemoveTypedHandler extends _EvtHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Typed typed = (Typed) oldValue;
            Map<Topic, List<Typed>> map = getMap(typed);
            _unindex(map, typed.getType(), typed);
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
