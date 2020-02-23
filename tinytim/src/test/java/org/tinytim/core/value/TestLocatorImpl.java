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
package org.tinytim.core.value;

import org.tinytim.core.AbstractTinyTimTestCase;
import org.tmapi.core.Locator;

/**
 * Tests against the locator implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestLocatorImpl extends AbstractTinyTimTestCase {

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

    public void testResolve() {
        Locator loc = _tm.createLocator("http://www.example.org/");
        assertEquals("http://www.example.org/#uta%20schulze", loc.resolve("#uta schulze").toExternalForm());
    }

//    public void testLowerCaseScheme() {
//        Locator loc = _tm.createLocator("HTTP://www.example.org/test+me/");
//        assertEquals("http://www.example.org/test me/", loc.getReference());
//        assertEquals("http://www.example.org/test%20me/", loc.toExternalForm());
//    }
}
