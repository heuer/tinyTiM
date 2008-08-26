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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class CollectionFactory {

    private static final String _COLL_FACTORY_TROVE = "org.tinytim.internal.utils.TroveCollectionFactory";

    private static final ICollectionFactory _COLL_FACTORY;

    static {
        ICollectionFactory collFactory;
        try {
            Class.forName("gnu.trove.THashSet");
            collFactory = (ICollectionFactory) Class.forName(_COLL_FACTORY_TROVE).newInstance();
        }
        catch (Exception ex) {
            collFactory = new JavaCollectionFactory();
        }
        _COLL_FACTORY = collFactory;
    }

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
