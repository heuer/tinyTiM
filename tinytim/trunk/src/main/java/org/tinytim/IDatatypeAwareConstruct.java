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

import org.tmapi.core.Locator;

/**
 * Indicates that a Topic Maps construct has a value and a datatype.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
interface IDatatypeAwareConstruct extends IConstruct {

    static final String _XSD_BASE = "http://www.w3.org/2001/XMLSchema#";
    static final Locator STRING = new IRI(_XSD_BASE + "string");
    static final Locator ANY_URI = new IRI(_XSD_BASE + "anyURI");

    /**
     * The value of this Topic Maps construct.
     * 
     * This method differs from TMAPI: This method MUST return the value OR the
     * locator as string. This method should be removed if we have TMAPI 2.0
     * (maybe the whole interface should be removed).
     * Currently, the {@link SignatureGenerator} needs it.
     *
     * @return The value.
     */
    public String getValue2();

    /**
     * Returns the datatype of this Topic Maps construct.
     *
     * @return The datatype.
     */
    public Locator getDatatype();

}
