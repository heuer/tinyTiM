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
