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
package org.tinytim;

/**
 * Provides constants for all tinyTiM-specific TMAPI properties.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Property {

    private Property() {
        // noop.
    }

    /**
     * Property which indicates the {@link org.tinytim.ICollectionFactory} to use.
     * 
     * The default value of this property depends on the environment: If
     * the <a href="http://trove4j.sourceforge.net/">Trove4J</a> lib is found, 
     * that lib used, otherwise a collection factory which depends on the 
     * default Java collections.
     */
    public static final String COLLECTION_FACTORY = "org.tinytim.CollectionFactory";

    /**
     * Property which indicates if the "old" XTM 1.0 reification mechanism 
     * should be used.
     * 
     * For backwards compatibilty and to support TMAPI 1.0 this property is
     * set to "true" by default.
     * 
     * Note, that this property is likely to be removed in a future version and
     * that only the TMDM way of reification will be supported.
     * Maybe you'll be able to use {@link org.tinytim.ReificationUtils} to 
     * support the XTM 1.0 reification mechanism.
     */
    public static final String XTM10_REIFICATION = "org.tinytim.XTM10Reification";

}
