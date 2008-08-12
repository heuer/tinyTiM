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

import gnu.trove.THashMap;
import gnu.trove.THashSet;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIdentityHashingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link ICollectionFactory} which uses the
 * <a href="http://sourceforge.net/projects/trove4j/">Trove library </a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class TroveCollectionFactory implements ICollectionFactory {

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap()
     */
    public <E> IIntObjectMap<E> createIntObjectMap() {
        return new TroveIntObjectMap<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap(int)
     */
    public <E> IIntObjectMap<E> createIntObjectMap(int size) {
        return new TroveIntObjectMap<E>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap(int)
     */
    public <K, V> Map<K, V> createMap(int size) {
        return new THashMap<K,V>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap()
     */
    public <K, V> Map<K, V> createMap() {
        return new THashMap<K, V>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createMap(java.util.Map)
     */
    public <K, V> Map<K, V> createMap(Map<? extends K, ? extends V> map) {
        Map<K, V> result = createMap(map.size());
        result.putAll(map);
        return result;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap()
     */
    public <K, V> Map<K, V> createIdentityMap() {
        return new THashMap<K, V>(new TObjectIdentityHashingStrategy<K>());
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap(int)
     */
    public <K, V> Map<K, V> createIdentityMap(int size) {
        return new THashMap<K, V>(size, new TObjectIdentityHashingStrategy<K>());
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet(int)
     */
    public <E> Set<E> createSet(int size) {
        return new THashSet<E>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet()
     */
    public <E> Set<E> createSet() {
        return new THashSet<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createSet(java.util.Set)
     */
    public <E> Set<E> createSet(Set<? extends E> elements) {
        return new THashSet<E>(elements);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet()
     */
    public <E> Set<E> createIdentitySet() {
        return new THashSet<E>(new TObjectIdentityHashingStrategy<E>());
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet(int)
     */
    public <E> Set<E> createIdentitySet(int size) {
        return new THashSet<E>(size, new TObjectIdentityHashingStrategy<E>());
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentitySet(java.util.Set)
     */
    public <E> Set<E> createIdentitySet(Set<? extends E> elements) {
        return new THashSet<E>(elements, new TObjectIdentityHashingStrategy<E>());
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList()
     */
    public <E> List<E> createList() {
        return new ArrayList<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList(java.util.Collection)
     */
    public <E> List<E> createList(Collection<? extends E> values) {
        return new ArrayList<E>(values);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList(int)
     */
    public <E> List<E> createList(int size) {
        return new ArrayList<E>(size);
    }

    private static final class TroveIntObjectMap<V> implements IIntObjectMap<V> {

        private final TIntObjectHashMap<V> _map;

        TroveIntObjectMap() {
            _map = new TIntObjectHashMap<V>();
        }

        TroveIntObjectMap(int size) {
            _map = new TIntObjectHashMap<V>(size);
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
            return _map.put(key, value);
        }

        public void clear() {
            _map.clear();
        }

    }
}
