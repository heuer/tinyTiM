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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link ICollectionFactory} which uses the Ontopia collections.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class OntopiaCollectionFactory implements ICollectionFactory {

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap()
     */
    @Override
    public <E> IIntObjectMap<E> createIntObjectMap() {
        return new DefaultIntObjectMap<E>(this.<Integer, E>createMap());
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIntObjectMap(int)
     */
    @Override
    public <E> IIntObjectMap<E> createIntObjectMap(int size) {
        return new DefaultIntObjectMap<E>(this.<Integer, E>createMap(size));
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap(int)
     */
    @Override
    public <K, V> Map<K, V> createMap(int size) {
        return new HashMap<K, V>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createMap()
     */
    @Override
    public <K, V> Map<K, V> createMap() {
        return new HashMap<K, V>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createMap(java.util.Map)
     */
    @Override
    public <K, V> Map<K, V> createMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap()
     */
    @Override
    public <K, V> Map<K, V> createIdentityMap() {
        return new IdentityHashMap<K,V>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentityMap(int)
     */
    @Override
    public <K, V> Map<K, V> createIdentityMap(int size) {
        return new IdentityHashMap<K,V>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet(int)
     */
    @Override
    public <E> Set<E> createSet(int size) {
        return new CompactHashSet<E>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.ICollectionFactory#createSet()
     */
    @Override
    public <E> Set<E> createSet() {
        return new CompactHashSet<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createSet(java.util.Set)
     */
    @Override
    public <E> Set<E> createSet(Set<? extends E> elements) {
        Set<E> set = createSet(elements.size());
        set.addAll(elements);
        return set;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet()
     */
    @Override
    public <E> Set<E> createIdentitySet() {
        return new CompactIdentityHashSet<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ICollectionFactory#createIdentitySet(int)
     */
    @Override
    public <E> Set<E> createIdentitySet(int size) {
        return new CompactIdentityHashSet<E>(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentitySet(java.util.Set)
     */
    @Override
    public <E> Set<E> createIdentitySet(Set<? extends E> elements) {
        Set<E> set = createIdentitySet(elements.size());
        set.addAll(elements);
        return set;
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList()
     */
    @Override
    public <E> List<E> createList() {
        return new ArrayList<E>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList(java.util.Collection)
     */
    @Override
    public <E> List<E> createList(Collection<? extends E> values) {
        return new ArrayList<E>(values);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createList(int)
     */
    @Override
    public <E> List<E> createList(int size) {
        return new ArrayList<E>(size);
    }

}
