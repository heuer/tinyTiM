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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entry point to create various {@link java.util.Collection} implementations.
 * <p>
 * This class acts as a wrapper for a concrete {@link ICollectionFactory}.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class CollectionFactory {

    private static final ICollectionFactory _COLL_FACTORY = new OntopiaCollectionFactory();

    private CollectionFactory() {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentityMap()
     */
    public static <K, V> Map<K, V> createIdentityMap() {
        return _COLL_FACTORY.createIdentityMap();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentityMap(int)
     */
    public static <K, V> Map<K, V> createIdentityMap(int size) {
        return _COLL_FACTORY.createIdentityMap(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentitySet(int)
     */
    public static <E> Set<E> createIdentitySet(int size) {
        return _COLL_FACTORY.createIdentitySet(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createIdentitySet()
     */
    public static <E> Set<E> createIdentitySet() {
        return _COLL_FACTORY.createIdentitySet();
    }

    public static <E> Set<E> createIdentitySet(Set<? extends E> elements) {
        return _COLL_FACTORY.createIdentitySet(elements);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createMap()
     */
    public static <K, V> Map<K, V> createMap() {
        return _COLL_FACTORY.createMap();
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createMap(int)
     */
    public static <K, V> Map<K, V> createMap(int size) {
        return _COLL_FACTORY.createMap(size);
    }

    public static <K, V> Map<K, V> createMap(Map<? extends K,? extends V> map) {
        return _COLL_FACTORY.createMap(map);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createSet(int)
     */
    public static <E> Set<E> createSet(int size) {
        return _COLL_FACTORY.createSet(size);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.ICollectionFactory#createSet()
     */
    public static <E> Set<E> createSet() {
        return _COLL_FACTORY.createSet();
    }

    public static <E> IIntObjectMap<E> createIntObjectMap() {
        return _COLL_FACTORY.createIntObjectMap();
    }

    public static <E> IIntObjectMap<E> createIntObjectMap(int size) {
        return _COLL_FACTORY.createIntObjectMap(size);
    }

    public static <E> List<E> createList() {
        return _COLL_FACTORY.createList();
    }

    public static <E> List<E> createList(int size) {
        return _COLL_FACTORY.createList(size);
    }

    public static <E> List<E> createList(Collection<? extends E> values) {
        return _COLL_FACTORY.createList(values);
    }

}
