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

import java.util.Map;
import java.util.Set;

/**
 * Factory for collections.
 * 
 * Implementations of this interface must provide a default constructor.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface ICollectionFactory {

    /**
     * Creates a Set with the specified initial <code>size</code>.
     *
     * @param <E>
     * @param size The initial capacity.
     * @return
     */
    <E> Set<E> createSet(int size);

    /**
     * Creates a Set.
     *
     * @param <E>
     * @return
     */
    <E> Set<E> createSet();

    /**
     * Creates a Map.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    <K, V> Map<K, V> createMap();

    /**
     * Creates a Map with the specified initial <code>size</code>.
     *
     * @param <K>
     * @param <V>
     * @param size The initial capacity.
     * @return
     */
    <K, V> Map<K, V> createMap(int size);

}
