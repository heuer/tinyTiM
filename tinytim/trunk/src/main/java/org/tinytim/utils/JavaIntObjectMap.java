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
package org.tinytim.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class JavaIntObjectMap<V> implements IIntObjectMap<V> {

    private final Map<Integer, V> _map;

    JavaIntObjectMap() {
        _map = new HashMap<Integer, V>();
    }

    JavaIntObjectMap(int size) {
        _map = new HashMap<Integer, V>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#get(int)
     */
    public V get(int key) {
        return _map.get(key);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#put(int, java.lang.Object)
     */
    public V put(int key, V value) {
        return _map.put(Integer.valueOf(key), value);
    }

    public void clear() {
        _map.clear();
    }

}
