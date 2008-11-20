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

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMapObject;

/**
 * The Topic Maps construct.
 * 
 * This interface is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IConstruct extends TopicMapObject {

    /**
     * Returns the parent of the Topic Maps construct.
     *
     * @return The parent of a Topic Maps construct or <code>null</code>.
     */
    public IConstruct getParent();

    /**
     * Returns the item identifiers of the Topic Maps construct.
     *
     * @return A (maybe empty) immutable Set of item identifiers.
     */
    public Set<Locator> getItemIdentifiers();

    /**
     * Adds an item identifier to the Topic Maps construct.
     *
     * @param itemIdentifier The item identifier to add.
     */
    public void addItemIdentifier(Locator itemIdentifier);

    /**
     * Removes an item identifier from the Topic Maps construct.
     *
     * @param itemIdentifier The item identifier to remove.
     */
    public void removeItemIdentifier(Locator itemIdentifier);

}
