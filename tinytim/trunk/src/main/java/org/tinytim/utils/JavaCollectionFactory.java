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

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
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
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap()
     */
    public <K, V> Map<K, V> createIdentityMap() {
        return new IdentityHashMap<K,V>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap(int)
     */
    public <K, V> Map<K, V> createIdentityMap(int size) {
        return new IdentityHashMap<K,V>(size);
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

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet()
     */
    public <E> Set<E> createIdentitySet() {
        return new IdentityHashSet<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet(int)
     */
    public <E> Set<E> createIdentitySet(int size) {
        return new IdentityHashSet<E>(size);
    }

    /**
     * {@link java.util.Set} implementation that compares its elements by 
     * identity.
     */
    private static class IdentityHashSet<E> extends AbstractSet<E> {

        private final Map<E, Boolean> _map;

        public IdentityHashSet() {
            _map = new IdentityHashMap<E, Boolean>();
        }

        public IdentityHashSet(int size) {
            _map = new IdentityHashMap<E, Boolean>(size);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#add(java.lang.Object)
         */
        @Override
        public boolean add(E obj) {
            return _map.put(obj, Boolean.TRUE) == null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object obj) {
            return _map.remove(obj) != null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object obj) {
            return _map.containsKey(obj);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<E> iterator() {
            return _map.keySet().iterator();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return _map.size();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#clear()
         */
        @Override
        public void clear() {
            _map.clear();
        }
    }
}
