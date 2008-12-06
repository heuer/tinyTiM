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

/**
 * A map which uses <tt>int</tt> as keys.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IIntObjectMap<V> {

    /**
     * Associates the <tt>key</tt> with the <tt>value</tt>.
     *
     * @param key The key
     * @param value The value.
     * @return The previous value associated with <tt>key</tt> or <tt>null</tt>.
     */
    public V put(int key, V value);

    /**
     * Returns the value associated with <tt>key</tt>.
     *
     * @param key The key.
     * @return The value associated with <tt>key</tt> or <tt>null</tt>.
     */
    public V get(int key);

    /**
     * Clears the map.
     */
    public void clear();
}
