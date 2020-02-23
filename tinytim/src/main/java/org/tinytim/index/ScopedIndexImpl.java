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
package org.tinytim.index;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.IEventHandler;
import org.tinytim.internal.api.IEventPublisher;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.IScoped;
import org.tinytim.internal.utils.CollectionFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

/**
 * {@link org.tmapi.index.ScopedIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class ScopedIndexImpl extends AbstractIndex implements ScopedIndex {

    private final Map<Topic, Set<Association>> _theme2Assocs;
    private final Map<Topic, Set<Occurrence>> _theme2Occs;
    private final Map<Topic, Set<Name>> _theme2Names;
    private final Map<Topic, Set<Variant>> _theme2Variants;

    public ScopedIndexImpl() {
        super();
        _theme2Assocs = CollectionFactory.createIdentityMap();
        _theme2Occs = CollectionFactory.createIdentityMap();
        _theme2Names = CollectionFactory.createIdentityMap();
        _theme2Variants = CollectionFactory.createIdentityMap();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IEventPublisherAware#subscribe(org.tinytim.core.IEventPublisher)
     */
    public void subscribe(IEventPublisher publisher) {
        publisher.subscribe(Event.SET_SCOPE, new SetScopeHandler());
        IEventHandler handler = new AddScopedHandler();
        publisher.subscribe(Event.ADD_ASSOCIATION, handler);
        publisher.subscribe(Event.ATTACHED_OCCURRENCE, handler);
        publisher.subscribe(Event.ATTACHED_NAME, handler);
        publisher.subscribe(Event.ADD_VARIANT, handler);
        handler = new RemoveScopedHandler();
        publisher.subscribe(Event.REMOVE_ASSOCIATION, handler);
        publisher.subscribe(Event.DETACHED_OCCURRENCE, handler);
        publisher.subscribe(Event.DETACHED_NAME, handler);
        publisher.subscribe(Event.REMOVE_VARIANT, handler);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic)
     */
    public Collection<Association> getAssociations(Topic theme) {
        Collection<Association> assocs = _theme2Assocs.get(theme);
        return assocs == null ? Collections.<Association>emptySet()
                              : CollectionFactory.createList(assocs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Association> getAssociations(Topic[] themes,
            boolean matchAll) {
        if (themes == null) {
            throw new IllegalArgumentException("The themes must not be null");
        }
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
        List<Topic> themes = CollectionFactory.createList(_theme2Assocs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic)
     */
    public Collection<Occurrence> getOccurrences(Topic theme) {
        Collection<Occurrence> occs = _theme2Occs.get(theme);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : CollectionFactory.createList(occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Occurrence> getOccurrences(Topic[] themes,
            boolean matchAll) {
        if (themes == null) {
            throw new IllegalArgumentException("The themes must not be null");
        }
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
        List<Topic> themes = CollectionFactory.createList(_theme2Occs.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic)
     */
    public Collection<Name> getNames(Topic theme) {
        Collection<Name> names = _theme2Names.get(theme);
        return names == null ? Collections.<Name>emptySet()
                             : CollectionFactory.createList(names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
        if (themes == null) {
            throw new IllegalArgumentException("The themes must not be null");
        }
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
        List<Topic> themes = CollectionFactory.createList(_theme2Names.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic)
     */
    public Collection<Variant> getVariants(Topic theme) {
        if (theme == null) {
            throw new IllegalArgumentException("The theme must not be null");
        }
        Collection<Variant> vars = _theme2Variants.get(theme);
        return vars == null ? Collections.<Variant>emptySet()
                            : CollectionFactory.createList(vars);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
        if (themes == null) {
            throw new IllegalArgumentException("The themes must not be null");
        }
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
        List<Topic> themes = CollectionFactory.createList(_theme2Variants.keySet());
        themes.remove(null);
        return themes;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.AbstractIndex#clear()
     */
    void clear() {
        _theme2Assocs.clear();
        _theme2Occs.clear();
        _theme2Names.clear();
        _theme2Variants.clear();
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
