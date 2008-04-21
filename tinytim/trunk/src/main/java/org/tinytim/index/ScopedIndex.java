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
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
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
     * @see org.tinytim.index.IScopedIndex#getOccurrencesByTheme(org.tmapi.core.Topic)
     */
    public Collection<OccurrenceImpl> getOccurrencesByTheme(Topic theme) {
        List<OccurrenceImpl> occs = _theme2Occs.get(theme);
        return occs == null ? Collections.<OccurrenceImpl>emptySet()
                            : Collections.unmodifiableCollection(occs);
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
     * @see org.tinytim.index.IScopedIndex#getVariantsByTheme(org.tmapi.core.Topic)
     */
    public Collection<VariantImpl> getVariantsByTheme(Topic theme) {
        List<VariantImpl> vars = _theme2Variants.get(theme);
        return vars == null ? Collections.<VariantImpl>emptySet()
                            : Collections.unmodifiableCollection(vars);
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

    private final class AddThemeHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Map<Topic, ?> theme2Scoped = null;
            if (sender instanceof AssociationImpl) {
                theme2Scoped = _theme2Assocs;
            }
            else if (sender instanceof OccurrenceImpl) {
                theme2Scoped = _theme2Occs;
            }
            else if (sender instanceof TopicNameImpl) {
                theme2Scoped = _theme2Names;
            }
            else if (sender instanceof VariantImpl) {
                theme2Scoped = _theme2Variants;
            }
            _index((Map<Topic, List<ScopedObject>>) theme2Scoped, (Topic) newValue, (ScopedObject) sender);
        }

        private void _index(Map<Topic, List<ScopedObject>> theme2Scoped, 
                Topic newValue, ScopedObject sender) {
            List<ScopedObject> scopedConstructs = theme2Scoped.get(newValue);
            if (scopedConstructs == null) {
                scopedConstructs = new ArrayList<ScopedObject>();
                theme2Scoped.put(newValue, scopedConstructs);
            }
            scopedConstructs.add(sender);
        }
    }

    private final class RemoveThemeHandler implements IEventHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            List<?> scoped = null;
            if (sender instanceof AssociationImpl) {
                scoped = _theme2Assocs.get(oldValue);
            }
            else if (sender instanceof OccurrenceImpl) {
                scoped = _theme2Occs.get(oldValue);
            }
            else if (sender instanceof TopicNameImpl) {
                scoped = _theme2Names.get(oldValue);
            }
            else if (sender instanceof VariantImpl) {
                scoped = _theme2Variants.get(oldValue);
            }
            if (scoped != null) {
                scoped.remove(sender);
            }
        }
    }

}
