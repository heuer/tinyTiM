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

import org.tmapi.core.Construct;

/**
 * Enhancement of the {@link org.tmapi.core.Construct} interface.
 * <p>
 * Avoids <tt>foo instanceof Bar</tt> checks. Each construct knows its type.
 * </p>
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IConstruct extends Construct {

    /**
     * Returns <tt>true</tt> if this is a topic map.
     *
     * @return <tt>true</tt> if this is a topic map, <tt>false</tt> otherwise.
     */
    public boolean isTopicMap();

    /**
     * Returns <tt>true</tt> if this is a topic.
     *
     * @return <tt>true</tt> if this is a topic, <tt>false</tt> otherwise.
     */
    public boolean isTopic();

    /**
     * Returns <tt>true</tt> if this is an association.
     *
     * @return <tt>true</tt> if this is an association, <tt>false</tt> otherwise.
     */
    public boolean isAssociation();

    /**
     * Returns <tt>true</tt> if this is a role.
     *
     * @return <tt>true</tt> if this is a role, <tt>false</tt> otherwise.
     */
    public boolean isRole();

    /**
     * Returns <tt>true</tt> if this is an occurrence.
     *
     * @return <tt>true</tt> if this is an occurrence, <tt>false</tt> otherwise.
     */
    public boolean isOccurrence();

    /**
     * Returns <tt>true</tt> if this is a name.
     *
     * @return <tt>true</tt> if this is a name, <tt>false</tt> otherwise.
     */
    public boolean isName();

    /**
     * Returns <tt>true</tt> if this is a variant.
     *
     * @return <tt>true</tt> if this is a variant, <tt>false</tt> otherwise.
     */
    public boolean isVariant();

}
