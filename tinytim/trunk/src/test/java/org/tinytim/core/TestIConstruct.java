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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

/**
 * Tests against {@link IConstruct}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestIConstruct extends TinyTimTestCase {


    private void _testConstruct(Construct construct) {
        IConstruct c = (IConstruct) construct;
        assertEquals(c.isTopicMap(), c instanceof TopicMap);
        assertEquals(c.isTopic(), c instanceof Topic);
        assertEquals(c.isAssociation(), c instanceof Association);
        assertEquals(c.isRole(), c instanceof Role);
        assertEquals(c.isOccurrence(), c instanceof Occurrence);
        assertEquals(c.isName(), c instanceof Name);
        assertEquals(c.isVariant(), c instanceof Variant);
    }

    public void testTopicMap() {
        _testConstruct(_tm);
    }

    public void testTopic() {
        _testConstruct(createTopic());
    }

    public void testAssociation() {
        _testConstruct(createAssociation());
    }

    public void testRole() {
        _testConstruct(createRole());
    }

    public void testOccurrence() {
        _testConstruct(createOccurrence());
    }

    public void testName() {
        _testConstruct(createName());
    }

    public void testVariant() {
        _testConstruct(createVariant());
    }
}
