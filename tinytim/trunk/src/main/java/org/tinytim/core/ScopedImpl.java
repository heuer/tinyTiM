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

import java.util.Set;

import org.tmapi.core.Topic;

/**
 * Class that provides a "scope" property and sends events if that property 
 * changes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class ScopedImpl extends TypedImpl implements IScoped {

    //NOTE: This class does NOT implement IScoped by intention!

    private IScope _scope;

    ScopedImpl(TopicMapImpl tm) {
        super(tm);
        _scope = Scope.UCS;
    }

    ScopedImpl(TopicMapImpl topicMap, Topic type, IScope scope) {
        super(topicMap, type);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScoped#getScopeObject()
     */
    public IScope getScopeObject() {
        return _scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScoped#setScopeObject(org.tinytim.core.IScope)
     */
    public void setScopeObject(IScope scope) {
        if (_scope == scope) {
            return;
        }
        _fireEvent(Event.SET_SCOPE, _scope, scope);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.ScopedObject#getScope()
     */
    public Set<Topic> getScope() {
        return _scope.asSet();
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void addTheme(Topic theme) {
        if (theme == null) {
            throw new IllegalArgumentException("The theme must not be null");
        }
        setScopeObject(_scope.add(theme));
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void removeTheme(Topic theme) {
        setScopeObject(_scope.remove(theme));
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.TypedImpl#dispose()
     */
    @Override
    protected void dispose() {
        _scope = null;
        super.dispose();
    }

}
