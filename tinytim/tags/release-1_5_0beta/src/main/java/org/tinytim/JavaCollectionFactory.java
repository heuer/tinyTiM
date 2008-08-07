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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link ICollectionFactory} which uses the standard Java collections.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class JavaCollectionFactory implements ICollectionFactory {

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap(int)
     */
    public <K, V> Map<K, V> createMap(int size) {
        return new HashMap<K, V>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap()
     */
    public <K, V> Map<K, V> createMap() {
        return new HashMap<K, V>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet(int)
     */
    public <E> Set<E> createSet(int size) {
        return new HashSet<E>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet()
     */
    public <E> Set<E> createSet() {
        return new HashSet<E>();
    }

}
