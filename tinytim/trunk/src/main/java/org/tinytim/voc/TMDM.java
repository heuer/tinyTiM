/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package org.tinytim.voc;

import org.tmapi.core.Locator;

/**
 * Constants for TMDM 1.0 (model) PSIs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TMDM extends Vocabulary {

    private TMDM() {
        // noop.
    }

    private static final String _BASE = Namespace.TMDM_MODEL;

    /**
     * Core concept of a subject.
     */
    public static final Locator SUBJECT = _createLocator(_BASE + "subject");

    /**
     * Core concept of type-instance relationships. 
     * Used as association type.
     */
    public static final Locator TYPE_INSTANCE = _createLocator(_BASE + "type-instance");

    /**
     * Core concept of type within a type-instance relationship. 
     * Used as role type.
     */
    public static final Locator TYPE = _createLocator(_BASE + "type");

    /**
     * Core concept of instance within a type-instance relationship. 
     * Used as role type.
     */
    public static final Locator INSTANCE = _createLocator(_BASE + "instance");

    /**
     * Core concept of supertype-subtype relationship.
     * Used as association type.
     */
    public static final Locator SUPERTYPE_SUBTYPE = _createLocator(_BASE + "supertype-subtype");

    /**
     * Core concept of supertype within a supertype-subtype relationship.
     * Used as role type.
     */
    public static final Locator SUPERTYPE = _createLocator(_BASE + "supertype");

    /**
     * Core concept of subtype within a supertype-subtype relationship.
     * Used as role type.
     */
    public static final Locator SUBTYPE = _createLocator(_BASE + "subtype");

    /**
     * Core concept of a topic name.
     * Used as topic name type.
     */
    public static final Locator TOPIC_NAME = _createLocator(_BASE + "topic-name");

    /**
     * Used to indicate that a variant can be used for sorting purposes.
     * Used as variant theme.
     */
    public static final Locator SORT = _createLocator(_BASE + "sort");

}

