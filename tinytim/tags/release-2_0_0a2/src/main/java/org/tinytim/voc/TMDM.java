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
package org.tinytim.voc;

import org.tmapi.core.Locator;

/**
 * Constants for TMDM 1.0 (model) PSIs.
 * 
 * Copied with permission from the Semagia MIO project.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TMDM extends Vocabulary {

    private TMDM() {
        // noop.
    }

    private static final String _BASE = "http://psi.topicmaps.org/iso13250/model/";

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

