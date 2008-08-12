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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.WeakObjectRegistry;
import org.tmapi.core.Topic;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class Scope implements IScope {

    public static final IScope UCS = new Scope();

    private static final WeakObjectRegistry<IScope> _SCOPES = new WeakObjectRegistry<IScope>(IConstant.SCOPE_SCOPES_SIZE);

    private final Set<Topic> _set; 

    private Scope() {
        _set = Collections.emptySet();
    }

    private Scope(Collection<Topic> themes) {
        _set = CollectionFactory.createIdentitySet(themes.size());
        _set.addAll(themes);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof Scope) && _set.equals(((Scope)obj)._set);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _set.hashCode();
    }

    public static synchronized IScope create(Collection<Topic> themes) {
        if (themes.isEmpty()) {
            return UCS;
        }
        IScope scope = new Scope(themes);
        IScope existing = _SCOPES.get(scope);
        if (existing != null) {
            return existing;
        }
        _SCOPES.add(scope);
        return scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#asSet()
     */
    public Set<Topic> asSet() {
        return Collections.unmodifiableSet(_set);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#contains(org.tmapi.core.Topic)
     */
    public boolean contains(Topic theme) {
        return _set.contains(theme);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#add(org.tmapi.core.Topic)
     */
    public IScope add(Topic theme) {
        if (_set.contains(theme)) {
            return this;
        }
        Collection<Topic> themes = CollectionFactory.createList(_set);
        themes.add(theme);
        return create(themes);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#remove(org.tmapi.core.Topic)
     */
    public IScope remove(Topic theme) {
        if (!_set.contains(theme)) {
            return this;
        }
        Collection<Topic> themes = CollectionFactory.createList(_set);
        themes.remove(theme);
        return create(themes);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Topic> iterator(){
        return _set.iterator();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#isUnconstrained()
     */
    public boolean isUnconstrained() {
        return this == UCS;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScope#size()
     */
    public int size() {
        return _set.size();
    }

}
