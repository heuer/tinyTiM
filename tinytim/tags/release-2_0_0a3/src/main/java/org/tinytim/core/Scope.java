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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.WeakObjectRegistry;

import org.tmapi.core.Topic;

/**
 * {@link IScope} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Scope implements IScope {

    public static final IScope UCS = new Scope();

    private static final WeakObjectRegistry<IScope> _SCOPES = new WeakObjectRegistry<IScope>(IConstant.SCOPE_SCOPES_SIZE);

    private final Set<Topic> _set; 

    private Scope() {
        _set = Collections.emptySet();
    }

    private Scope(Collection<Topic> themes) {
        Set<Topic> set = CollectionFactory.createIdentitySet(themes.size());
        set.addAll(themes);
        _set = Collections.unmodifiableSet(set);
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
     * @see org.tinytim.internal.api.IScope#asSet()
     */
    public Set<Topic> asSet() {
        // _set is immutable
        return _set;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScope#contains(org.tmapi.core.Topic)
     */
    public boolean contains(Topic theme) {
        return _set.contains(theme);
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScope#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<Topic> scope) {
        return _set.containsAll(scope);
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScope#add(org.tmapi.core.Topic)
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
     * @see org.tinytim.internal.api.IScope#remove(org.tmapi.core.Topic)
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
     * @see org.tinytim.internal.api.IScope#isUnconstrained()
     */
    public boolean isUnconstrained() {
        return this == UCS;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScope#size()
     */
    public int size() {
        return _set.size();
    }

}
