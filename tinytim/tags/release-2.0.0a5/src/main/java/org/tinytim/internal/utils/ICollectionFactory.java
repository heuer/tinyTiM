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
 * Factory for collections.
 * <p>
 * Implementations of this interface must provide a default constructor.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
interface ICollectionFactory {

    /**
     * 
     *
     * @param <E>
     * @return
     */
    <E> IIntObjectMap<E> createIntObjectMap();

    /**
     * 
     *
     * @param <E>
     * @param size
     * @return
     */
    <E> IIntObjectMap<E> createIntObjectMap(int size);

    /**
     * Creates a {@link java.util.Set} with the specified initial <code>size</code>.
     *
     * @param <E>
     * @param size The initial capacity.
     * @return
     */
    <E> Set<E> createSet(int size);

    /**
     * Creates a {@link java.util.Set}.
     *
     * @param <E>
     * @return
     */
    <E> Set<E> createSet();

    <E> Set<E> createSet(Set<? extends E> elements);

    /**
     * Creates a {@link java.util.Set} with the specified initial <tt>size</tt>.
     * 
     * This is almost equal to {@link #createSet(int)} but the implementation is
     * allowed (but not required) to compare the elements by identity.
     *
     * @param <E>
     * @param size The initial capacity.
     * @return
     */
    <E> Set<E> createIdentitySet(int size);

    /**
     * Creates a {@link java.util.Set}.
     *
     * This is almost equal to {@link #createSet()} but the implementation is
     * allowed (but not required) to compare the elements by identity.
     *
     * @param <E>
     * @return
     */
    <E> Set<E> createIdentitySet();

    <E> Set<E> createIdentitySet(Set<? extends E> elements);

    /**
     * Creates a {@link java.util.Map}.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    <K, V> Map<K, V> createMap();

    /**
     * Creates a {@link java.util.Map} with the specified initial <tt>size</tt>.
     *
     * @param <K>
     * @param <V>
     * @param size The initial capacity.
     * @return
     */
    <K, V> Map<K, V> createMap(int size);

    <K, V> Map<K, V> createMap(Map<? extends K,? extends V> map);

    /**
     * Creates a {@link java.util.Map}.
     * 
     * This is almost equal to {@link #createMap()} but the implementation is
     * allowed (but not required) to compare the key elements by identity.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    <K, V> Map<K, V> createIdentityMap();

    /**
     * Creates a {@link java.util.Map} with the specified initial <tt>size</tt>.
     * 
     * This is almost equal to {@link #createMap(int)} but the implementation is
     * allowed (but not required) to compare the key elements by identity.
     *
     * @param <K>
     * @param <V>
     * @param size The initial capacity.
     * @return
     */
    <K, V> Map<K, V> createIdentityMap(int size);

    <E> List<E> createList();
    
    <E> List<E> createList(int size);
    
    <E> List<E> createList(Collection<? extends E> values);

}
