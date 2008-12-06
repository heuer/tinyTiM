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
package org.tinytim.internal.api;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Topic;

/**
 * Represents an immutable set of {@link org.tmapi.core.Topic}s.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IScope extends Iterable<Topic> {

    /**
     * Returns the scope as set of topics.
     *
     * @return A set of topics.
     */
    public Set<Topic> asSet();

    /**
     * Returns <tt>true</tt> if the theme is part of this scope.
     *
     * @param theme A topic.
     * @return <tt>true</tt> if the theme is part of this scope, otherwise <tt>false</tt>.
     */
    public boolean contains(Topic theme);

    /**
     * Returns <tt>true</tt> if all themes of the other <tt>scope</tt> are part 
     * of this scope.
     *
     * @param scope A collection of themes.
     * @return <tt>true</tt> if all themes are part of this scope, otherwise <tt>false</tt>.
     */
    public boolean containsAll(Collection<Topic> scope);

    /**
     * Returns a <tt>IScope</tt> consisting of all themes contained in this
     * scope and the <tt>theme</tt>.
     *
     * @param theme The theme to add.
     * @return A scope instance which is contains all themes of this scope plus
     *          the specified <tt>theme</tt>.
     */
    public IScope add(Topic theme);

    /**
     * Returns a <tt>IScope</tt> where the <tt>theme</tt> is removed from this
     * set of themes..
     *
     * @param theme The theme to remove.
     * @return A scope instance which is contains all themes of this scope minus
     *          the specified <tt>theme</tt>.
     */
    public IScope remove(Topic theme);

    /**
     * Returns if this scope is unconstrained (empty).
     *
     * @return <tt>true</tt> if the scope is unconstrained, <tt>false</tt> otherwise.
     */
    public boolean isUnconstrained();

    /**
     * Returns the number of themes contained in this scope.
     *
     * @return Number of themes.
     */
    public int size();

}
