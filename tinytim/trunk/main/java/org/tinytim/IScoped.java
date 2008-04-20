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
package org.tinytim;

import java.util.Set;

import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;

/**
 * Scoped Topic Maps construct.
 * 
 * Associations, occurrences, names and variants are scoped Topic Maps 
 * constructs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
interface IScoped extends IConstruct, ScopedObject {

    /* (non-Javadoc)
     * @see org.tmapi.core.ScopedObject#getScope()
     */
    public Set<Topic> getScope();

    /**
     * Adds a theme to the scope.
     *
     * @param theme The theme to add.
     */
    public void addTheme(Topic theme);

    /**
     * Removes a theme from the scope.
     *
     * @param theme The theme to remove.
     */
    public void removeTheme(Topic theme);
}
