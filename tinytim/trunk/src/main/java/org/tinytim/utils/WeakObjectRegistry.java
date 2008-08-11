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

import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class WeakObjectRegistry<E> extends AbstractSet<E> {

    private final Map<E, WeakReference<E>> _obj2Ref;

    public WeakObjectRegistry() {
        super();
        _obj2Ref = new WeakHashMap<E, WeakReference<E>>();
    }

    /**
     * 
     *
     * @param key
     * @return
     */
    public E get(Object key) {
        WeakReference<E> weakRef = _obj2Ref.get(key);
        return weakRef != null ? weakRef.get() : null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#add(java.lang.Object)
     */
    @Override
    public boolean add(E obj) {
        WeakReference<E> ref = new WeakReference<E>(obj);
        ref = _obj2Ref.put(obj, ref);
        return ref != null && ref.get() != null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object obj) {
        WeakReference<E> ref = _obj2Ref.remove(obj);
        return ref != null && ref.get() != null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        _obj2Ref.clear();
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object obj) {
        return get(obj) != null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return _obj2Ref.keySet().iterator();
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return _obj2Ref.size();
    }
}
