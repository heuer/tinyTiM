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

import java.util.Arrays;

import org.tmapi.core.Topic;

/**
 * Tests against {@link Scope}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestScope extends TinyTimTestCase {

    public void testUnconstrained() {
        assertTrue(Scope.UCS.isUnconstrained());
        assertEquals(0, Scope.UCS.size());
    }

    public void testEquals() {
        final Topic theme1 = createTopic();
        final Topic theme2 = createTopic();
        final IScope scope1 = Scope.create(Arrays.asList(theme1, theme2));
        assertEquals(2, scope1.size());
        final IScope scope2 = Scope.create(Arrays.asList(theme2, theme1));
        assertSame(scope1, scope2);
    }
}
