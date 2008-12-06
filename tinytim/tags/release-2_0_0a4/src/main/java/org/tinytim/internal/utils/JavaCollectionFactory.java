/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinytim.internal.utils;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
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
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap()
     */
    public <E> IIntObjectMap<E> createIntObjectMap() {
        return new DefaultIntObjectMap<E>(this.<Integer, E>createMap());
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap(int)
     */
    public <E> IIntObjectMap<E> createIntObjectMap(int size) {
        return new DefaultIntObjectMap<E>(this.<Integer, E>createMap(size));
    }

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
     * @see org.tinytim.utils.ICollectionFactory#createMap(java.util.Map)
     */
    public <K, V> Map<K, V> createMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
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
     * @see org.tinytim.utils.ICollectionFactory#createSet(java.util.Set)
     */
    public <E> Set<E> createSet(Set<? extends E> elements) {
        return new HashSet<E>(elements);
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

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentitySet(java.util.Set)
     */
    public <E> Set<E> createIdentitySet(Set<? extends E> elements) {
        Set<E> set = createIdentitySet(elements.size());
        set.addAll(elements);
        return set;
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
