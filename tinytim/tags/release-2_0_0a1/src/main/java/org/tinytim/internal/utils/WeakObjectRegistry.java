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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Registry which keeps weak references to the contained elements.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class WeakObjectRegistry<E> {

    private final Map<E, WeakReference<E>> _obj2Ref;

    public WeakObjectRegistry() {
        super();
        _obj2Ref = new WeakHashMap<E, WeakReference<E>>();
    }

    public WeakObjectRegistry(int size) {
        super();
        _obj2Ref = new WeakHashMap<E, WeakReference<E>>(size);
    }

    /**
     * Returns the existing value if <tt>obj</tt> is registered.
     *
     * @param key The key.
     * @return The registered value or <tt>null</tt>.
     */
    public E get(Object key) {
        WeakReference<E> weakRef = _obj2Ref.get(key);
        return weakRef != null ? weakRef.get() : null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    public boolean add(E obj) {
        WeakReference<E> ref = new WeakReference<E>(obj);
        ref = _obj2Ref.put(obj, ref);
        return ref != null && ref.get() != null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#clear()
     */
    public void clear() {
        _obj2Ref.clear();
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#size()
     */
    public int size() {
        return _obj2Ref.size();
    }
}
