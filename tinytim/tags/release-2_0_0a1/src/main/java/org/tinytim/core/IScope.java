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
import java.util.Set;

import org.tmapi.core.Topic;

/**
 * Represents an immutable set of {@link org.tmapi.core.Topic}s.
 * 
 * This interface is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IScope extends Iterable<Topic> {

    /**
     * Returns the scope as set of topics.
     *
     * @return A set of topics.
     */
    public Set<Topic> asSet();

    /**
     * Returns <tt>true</tt> if the theme is part of this scope.
     *
     * @param theme A topic.
     * @return <tt>true</tt> if the theme is part of this scope, otherwise <tt>false</tt>.
     */
    public boolean contains(Topic theme);

    /**
     * Returns a <tt>IScope</tt> consisting of all themes contained in this
     * scope and the <tt>theme</tt>.
     *
     * @param theme The theme to add.
     * @return A scope instance which is contains all themes of this scope plus
     *          the specified <tt>theme</tt>.
     */
    public IScope add(Topic theme);

    /**
     * Returns a <tt>IScope</tt> where the <tt>theme</tt> is removed from this
     * set of themes..
     *
     * @param theme The theme to remove.
     * @return A scope instance which is contains all themes of this scope minus
     *          the specified <tt>theme</tt>.
     */
    public IScope remove(Topic theme);

    /**
     * Returns if this scope is unconstrained (empty).
     *
     * @return <tt>true</tt> if the scope is unconstrained, <tt>false</tt> otherwise.
     */
    public boolean isUnconstrained();

    /**
     * Returns the number of themes contained in this scope.
     *
     * @return Number of themes.
     */
    public int size();

    public boolean containsAll(Collection<Topic> scope);

}
