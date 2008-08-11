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
import java.util.Set;

import org.tinytim.core.Event;
import org.tinytim.core.IConstruct;
import org.tinytim.core.IEventHandler;
import org.tinytim.core.IEventPublisher;
import org.tinytim.core.IScope;
import org.tinytim.core.IScoped;
import org.tinytim.utils.CollectionFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

/**
 * {@link ScopedIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class ScopedIndexImpl extends AbstractIndex implements ScopedIndex {

    private Map<Topic, Set<Association>> _theme2Assocs;
    private Map<Topic, Set<Occurrence>> _theme2Occs;
    private Map<Topic, Set<Name>> _theme2Names;
    private Map<Topic, Set<Variant>> _theme2Variants;

    public ScopedIndexImpl(IEventPublisher publisher) {
        super();
        _theme2Assocs = CollectionFactory.createIdentityMap();
        _theme2Occs = CollectionFactory.createIdentityMap();
        _theme2Names = CollectionFactory.createIdentityMap();
        _theme2Variants = CollectionFactory.createIdentityMap();
        publisher.subscribe(Event.SET_SCOPE, new SetScopeHandler());
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
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic)
     */
    public Collection<Association> getAssociations(Topic theme) {
        Collection<Association> assocs = _theme2Assocs.get(theme);
        return assocs == null ? Collections.<Association>emptySet()
                              : new ArrayList<Association>(assocs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Association> getAssociations(Topic[] themes,
            boolean matchAll) {
        Set<Association> result = CollectionFactory.createIdentitySet();
        if (!matchAll) {
            for (Topic theme: themes) {
                result.addAll(getAssociations(theme));
            }
        }
        else {
            result.addAll(getAssociations(themes[0]));
            for (int i=1; i < themes.length; i++) {
                result.retainAll(getAssociations(themes[i]));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociationThemes()
     */
    public Collection<Topic> getAssociationThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Assocs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic)
     */
    public Collection<Occurrence> getOccurrences(Topic theme) {
        Collection<Occurrence> occs = _theme2Occs.get(theme);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : new ArrayList<Occurrence>(occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Occurrence> getOccurrences(Topic[] themes,
            boolean matchAll) {
        Set<Occurrence> result = CollectionFactory.createIdentitySet();
        if (!matchAll) {
            for (Topic theme: themes) {
                result.addAll(getOccurrences(theme));
            }
        }
        else {
            result.addAll(getOccurrences(themes[0]));
            for (int i=1; i < themes.length; i++) {
                result.retainAll(getOccurrences(themes[i]));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrenceThemes()
     */
    public Collection<Topic> getOccurrenceThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Occs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic)
     */
    public Collection<Name> getNames(Topic theme) {
        Collection<Name> names = _theme2Names.get(theme);
        return names == null ? Collections.<Name>emptySet()
                             : new ArrayList<Name>(names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
        Set<Name> result = CollectionFactory.createIdentitySet();
        if (!matchAll) {
            for (Topic theme: themes) {
                result.addAll(getNames(theme));
            }
        }
        else {
            result.addAll(getNames(themes[0]));
            for (int i=1; i < themes.length; i++) {
                result.retainAll(getNames(themes[i]));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNameThemes()
     */
    public Collection<Topic> getNameThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Names.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic)
     */
    public Collection<Variant> getVariants(Topic theme) {
        Collection<Variant> vars = _theme2Variants.get(theme);
        return vars == null ? Collections.<Variant>emptySet()
                            : new ArrayList<Variant>(vars);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
        Set<Variant> result = CollectionFactory.createIdentitySet();
        if (!matchAll) {
            for (Topic theme: themes) {
                result.addAll(getVariants(theme));
            }
        }
        else {
            result.addAll(getVariants(themes[0]));
            for (int i=1; i < themes.length; i++) {
                result.retainAll(getVariants(themes[i]));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariantThemes()
     */
    public Collection<Topic> getVariantThemes() {
        List<Topic> themes = new ArrayList<Topic>(_theme2Variants.keySet());
        themes.remove(null);
        return themes;
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

    private void _unindex(Map<Topic, Set<Scoped>> map, Scoped scoped, IScope scope) {
        if (scope.isUnconstrained()) {
            Set<Scoped> list = map.get(null);
            if (list != null) {
                list.remove(scoped);
            }
        }
        else {
            for (Topic theme: scope) {
                Set<Scoped> list = map.get(theme);
                if (list != null) {
                    list.remove(scoped);
                    if (list.isEmpty()) {
                        map.remove(theme);
                    }
                }
            }
        }
    }

    private void _index(Map<Topic, Set<Scoped>> map, Scoped scoped, IScope scope) {
        if (scope.isUnconstrained()) {
            Set<Scoped> list = map.get(null);
            if (list == null) {
                list = CollectionFactory.createIdentitySet();
                map.put(null, list);
            }
            list.add(scoped);
        }
        else {
            for (Topic theme: scope) {
                Set<Scoped> list = map.get(theme);
                if (list == null) {
                    list = CollectionFactory.createIdentitySet();
                    map.put(theme, list);
                }
                list.add(scoped);
            }
        }
    }

    private abstract class _EvtHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        Map<Topic, Set<Scoped>> getMap(IConstruct scoped) {
            Map<Topic, ?> theme2Scoped = null;
            if (scoped.isAssociation()) {
                theme2Scoped = _theme2Assocs;
            }
            else if (scoped.isOccurrence()) {
                theme2Scoped = _theme2Occs;
            }
            else if (scoped.isName()) {
                theme2Scoped = _theme2Names;
            }
            else if (scoped.isVariant()) {
                theme2Scoped = _theme2Variants;
            }
            return (Map<Topic, Set<Scoped>>) theme2Scoped;
        }
    }

    private final class AddScopedHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            IScoped scoped = (IScoped) newValue;
            _index(getMap(scoped), scoped, scoped.getScopeObject());
        }
    }

    private final class RemoveScopedHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            IScoped scoped = (IScoped) oldValue;
            _unindex(getMap(scoped), scoped, scoped.getScopeObject());
        }
    }

    private final class SetScopeHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            Scoped scoped = (Scoped) sender;
            Map<Topic, Set<Scoped>> map = getMap(sender);
            _unindex(map, scoped, (IScope) oldValue);
            _index(map, scoped, (IScope) newValue);
        }
    }

}
