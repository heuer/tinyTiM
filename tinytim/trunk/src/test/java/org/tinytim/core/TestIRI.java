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
package org.tinytim.core;

import org.tmapi.core.Locator;

/**
 * Tests against the locator implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestIRI extends TinyTimTestCase {

    public void testNormalization() {
        Locator loc = _tm.createLocator("http://www.example.org/test+me/");
        assertEquals("http://www.example.org/test me/", loc.getReference());
        assertEquals("http://www.example.org/test%20me/", loc.toExternalForm());
        Locator loc2 = loc.resolve("./too");
        assertEquals("http://www.example.org/test me/too", loc2.getReference());
        assertEquals("http://www.example.org/test%20me/too", loc2.toExternalForm());
        Locator loc3 = _tm.createLocator("http://www.example.org/test me/");
        assertEquals("http://www.example.org/test me/", loc3.getReference());
        assertEquals("http://www.example.org/test%20me/", loc3.toExternalForm());
    }

    //TODO!
//    public void testLowerCaseScheme() {
//        Locator loc = _tm.createLocator("HTTP://www.example.org/test+me/");
//        assertEquals("http://www.example.org/test me/", loc.getReference());
//        assertEquals("http://www.example.org/test%20me/", loc.toExternalForm());
//    }
}
