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

/**
 * Simplified interface for indexes.
 * 
 * Copied from the TMAPIX-project.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface IIndex {

    /**
     * Returns if this index is automatically kept in sync. with the 
     * topic map values.
     *
     * @return <code>true</code> if the index synchronizes itself with the 
     *          underlying topic map, otherwise <code>false</code>
     */
    public boolean isAutoUpdated();

    /**
     * Resynchronizes this index with the data in the topic map.
     * 
     * Indexes that are automatically kept in sync should ignore this.
     */
    public void reindex();

    /**
     * Closes the index.
     * 
     * This operation is optional but useful to release resources.
     * After closing the index must not be used further.
     */
    public void close();

}
