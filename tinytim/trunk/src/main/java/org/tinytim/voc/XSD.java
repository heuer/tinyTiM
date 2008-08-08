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
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XSD extends Vocabulary {

    private XSD() {
        // noop.
    }
    
    private static final String _BASE = "http://www.w3.org/2001/XMLSchema#";
    
    public final static Locator STRING = _createLocator(_BASE + "string");

    public final static Locator ANY_URI = _createLocator(_BASE + "anyURI");

    public final static Locator DECIMAL = _createLocator(_BASE + "decimal");
    
    public final static Locator INTEGER = _createLocator(_BASE + "integer");

    public final static Locator INT = _createLocator(_BASE + "int");

    public final static Locator FLOAT = _createLocator(_BASE + "float");

    public final static Locator LONG = _createLocator(_BASE + "long");

}
