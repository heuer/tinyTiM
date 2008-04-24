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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tinytim.AssociationImpl;
import org.tinytim.Event;
import org.tinytim.ICollectionFactory;
import org.tinytim.IConstruct;
import org.tinytim.IEventHandler;
import org.tinytim.IEventPublisher;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicNameImpl;
import org.tinytim.VariantImpl;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;

/**
 * {@link IScopedIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class ScopedIndex implements IScopedIndex {

    private Map<Topic, List<AssociationImpl>> _theme2Assocs;
    private Map<Topic, List<OccurrenceImpl>> _theme2Occs;
    private Map<Topic, List<TopicNameImpl>> _theme2Names;
    private Map<Topic, List<VariantImpl>> _theme2Variants;

    public ScopedIndex(IEventPublisher publisher, ICollectionFactory collFactory) {
        _theme2Assocs = collFactory.createMap();
        _theme2Occs = collFactory.createMap();
        _theme2Names = collFactory.createMap();
        _theme2Variants = collFactory.createMap();
        publisher.subscribe(Event.ADD_THEME, new AddThemeHandler());
        publisher.subscribe(Event.REMOVE_THEME, new RemoveThemeHandler());
        IEventHandler handler = new AddScopedHandler();
        publisher.subscribe(Event.ADD_ASSOCIATION, handler);
        publisher.subscribe(Event.ADD_OCCURRENCE, handler);
        publisher.subscribe(Event.ADD_NAME, handler);
        publisher.subscribe(Event.ADD_VARIANT, handler);
        handler = new RemoveScopedHandler();
        publisher.subscribe(Event.REMOVE_ASSOCIATION, handler);
        publisher.subscribe(Event.REMOVE_OCCURRENCE, handler);
        publisher.subscribe(Event.REMOVE_NAME, handler);
        publisher.subscribe(Event.REMOVE_VARIANT, handler);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getAssociationsByTheme(org.tmapi.core.Topic)
     */
    public Collection<AssociationImpl> getAssociationsByTheme(Topic theme) {
        List<AssociationImpl> assocs = _theme2Assocs.get(theme);
        return assocs == null ? Collections.<AssociationImpl>emptySet()
                              : Collections.unmodifiableCollection(assocs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getAssociationThemes()
     */
    public Collection<Topic> getAssociationThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Assocs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getOccurrencesByTheme(org.tmapi.core.Topic)
     */
    public Collection<OccurrenceImpl> getOccurrencesByTheme(Topic theme) {
        List<OccurrenceImpl> occs = _theme2Occs.get(theme);
        return occs == null ? Collections.<OccurrenceImpl>emptySet()
                            : Collections.unmodifiableCollection(occs);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getOccurrenceThemes()
     */
    public Collection<Topic> getOccurrenceThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Occs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getNamesByTheme(org.tmapi.core.Topic)
     */
    public Collection<TopicNameImpl> getNamesByTheme(Topic theme) {
        List<TopicNameImpl> names = _theme2Names.get(theme);
        return names == null ? Collections.<TopicNameImpl>emptySet()
                             : Collections.unmodifiableCollection(names);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getNameThemes()
     */
    public Collection<Topic> getNameThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Names.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getVariantsByTheme(org.tmapi.core.Topic)
     */
    public Collection<VariantImpl> getVariantsByTheme(Topic theme) {
        List<VariantImpl> vars = _theme2Variants.get(theme);
        return vars == null ? Collections.<VariantImpl>emptySet()
                            : Collections.unmodifiableCollection(vars);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IScopedIndex#getVariantThemes()
     */
    public Collection<Topic> getVariantThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Variants.keySet());
        themes.remove(null);
        return themes;
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

    void clear() {
        _theme2Assocs.clear();
        _theme2Assocs = null;
        _theme2Occs.clear();
        _theme2Occs = null;
        _theme2Names.clear();
        _theme2Names = null;
        _theme2Variants.clear();
        _theme2Variants = null;
    }

    private abstract class _EvtHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        Map<Topic, List<ScopedObject>> getMap(ScopedObject scoped) {
            Map<Topic, ?> theme2Scoped = null;
            if (scoped instanceof AssociationImpl) {
                theme2Scoped = _theme2Assocs;
            }
            else if (scoped instanceof OccurrenceImpl) {
                theme2Scoped = _theme2Occs;
            }
            else if (scoped instanceof TopicNameImpl) {
                theme2Scoped = _theme2Names;
            }
            else if (scoped instanceof VariantImpl) {
                theme2Scoped = _theme2Variants;
            }
            return (Map<Topic, List<ScopedObject>>) theme2Scoped;
        }
    }

    private final class AddScopedHandler extends _EvtHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ScopedObject scoped = (ScopedObject) newValue;
            Map<Topic, List<ScopedObject>> map = getMap(scoped);
            List<ScopedObject> list = null;
            if (scoped.getScope().isEmpty()) {
                list = map.get(null);
                if (list == null) {
                    list = new ArrayList<ScopedObject>();
                    map.put(null, list);
                }
                list.add(scoped);
            }
            else {
                for (Iterator<Topic> iter = scoped.getScope().iterator(); iter.hasNext();) {
                    Topic theme = iter.next();
                    list = map.get(theme);
                    if (list == null) {
                        list = new ArrayList<ScopedObject>();
                        map.put(theme, list);
                    }
                    list.add(scoped);
                }
            }
        }
        
    }

    private final class RemoveScopedHandler extends _EvtHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ScopedObject scoped = (ScopedObject) oldValue;
            Map<Topic, List<ScopedObject>> map = getMap(scoped); 
            List<ScopedObject> list = null;
            if (scoped.getScope().isEmpty()) {
                list = map.get(null);
                if (list != null) {
                    list.remove(scoped);
                }
            }
            else {
                for (Iterator<Topic> iter = scoped.getScope().iterator(); iter.hasNext();) {
                    Topic theme = iter.next();
                    list = map.get(theme);
                    if (list != null) {
                        list.remove(scoped);
                        if (list.isEmpty()) {
                            map.remove(theme);
                        }
                    }
                }
            }
        }
        
    }

    private final class AddThemeHandler extends _EvtHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ScopedObject scoped = (ScopedObject) sender;
            Map<Topic, List<ScopedObject>> map = getMap(scoped); 
            List<ScopedObject> list = map.get(newValue);
            if (list == null) {
                list = new ArrayList<ScopedObject>();
                map.put((Topic)newValue, list);
            }
            list.add(scoped);
            list = map.get(null);
            if (list != null) {
                list.remove(scoped);
            }
        }
    }

    private final class RemoveThemeHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ScopedObject scoped = (ScopedObject) sender;
            Map<Topic, List<ScopedObject>> map = getMap(scoped); 
            List<ScopedObject> list = map.get(oldValue);
            if (list != null) {
                list.remove(scoped);
                if (list.isEmpty()) {
                    map.remove(oldValue);
                }
            }
            if (scoped.getScope().size() == 1) {
                list = map.get(null);
                if (list == null) {
                    list = new ArrayList<ScopedObject>();
                    map.put(null, list);
                }
                list.add(scoped);
            }
        }
    }

}
