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

import java.util.Map;

/**
 * Default implementation of the {@link IIntObjectMap} which wraps a map.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class DefaultIntObjectMap<E> implements IIntObjectMap<E> {

    private final Map<Integer, E> _map;

    public DefaultIntObjectMap(Map<Integer, E> map) {
        _map = map;
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#get(int)
     */
    public E get(int key) {
        return _map.get(key);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#put(int, java.lang.Object)
     */
    public E put(int key, E value) {
        return _map.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.tinytim.utils.IIntObjectMap#clear()
     */
    public void clear() {
        _map.clear();
    }
}
