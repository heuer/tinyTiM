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
import java.util.Set;

import org.tmapi.core.Topic;

/**
 * Class that provides a "scope" property and sends events if that property 
 * changes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class ScopedImpl extends TypedImpl {

    //NOTE: This class does NOT implement IScoped by intention!

    private Set<Topic> _scope;

    ScopedImpl(TopicMapImpl topicMap, Topic type, Collection<Topic> scope) {
        super(topicMap, type);
        if (scope != null && !scope.isEmpty()) {
            _scope = _makeSet(scope.size());
            for (Topic theme: scope) {
                _scope.add(theme);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.ScopedObject#getScope()
     */
    public Set<Topic> getScope() {
        return _scope == null ? Collections.<Topic>emptySet()
                              : Collections.unmodifiableSet(_scope); 
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void addTheme(Topic theme) {
        if (theme == null) {
            throw new IllegalArgumentException("The theme must not be null");
        }
        if (_scope != null && _scope.contains(theme)) {
            return;
        }
        _fireEvent(Event.ADD_THEME, null, theme);
        if (_scope == null) {
            _scope = _makeSet();
        }
        _scope.add(theme);
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void removeTheme(Topic theme) {
        if (_scope == null || _scope.isEmpty()) {
            return;
        }
        _fireEvent(Event.REMOVE_THEME, theme, null);
        _scope.remove(theme);
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
