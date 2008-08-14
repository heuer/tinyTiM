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
package org.tinytim.internal.utils;

import java.util.Map;

/**
 * Default implementation of the {@link IIntObjectMap} which wraps a map.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class DefaultIntObjectMap<E> implements IIntObjectMap<E> {

    private final Map<Integer, E> _map;

    public DefaultIntObjectMap(Map<Integer, E> map) {
        _map = map;
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#get(int)
     */
    public E get(int key) {
        return _map.get(key);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#put(int, java.lang.Object)
     */
    public E put(int key, E value) {
        return _map.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#clear()
     */
    public void clear() {
        _map.clear();
    }
}
