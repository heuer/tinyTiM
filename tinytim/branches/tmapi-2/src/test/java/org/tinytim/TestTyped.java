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

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against the {@link org.tinytim.ITyped} interface.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTyped extends TinyTimTestCase {

    /**
     * Tests if a Topic Maps construct is an instance of ITyped.
     */
    public void testInstanceOf() {
        assertFalse(((TopicMap)_tm) instanceof ITyped);
        Topic topic = _tm.createTopic();
        assertFalse(topic instanceof ITyped);
        Association assoc = _tm.createAssociation();
        assertTrue(assoc instanceof ITyped);
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        assertTrue(role instanceof ITyped);
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        assertTrue(occ instanceof ITyped);
        TopicName name = topic.createTopicName("tinyTiM", null);
        assertTrue(name instanceof ITyped);
        Variant variant = name.createVariant("tinyTiM", null);
        assertFalse(variant instanceof ITyped);
    }

    /**
     * Tests setting and getting the type of an association.
     */
    public void testAssociation() {
        _testSetGet((ITyped)_tm.createAssociation());
    }

    /**
     * Tests setting and getting the type of a role.
     */
    public void testRole() {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        _testSetGet((ITyped)role);
    }

    /**
     * Tests setting and getting the type of an occurrence.
     */
    public void testOccurrence() {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testSetGet((ITyped)occ);
    }

    /**
     * Tests setting and getting the type of a name.
     */
    public void testName() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testSetGet((ITyped)name);
    }

    /**
     * Tests setting and getting the type of a typed Topic Maps construct.
     *
     * @param typed The Topic Maps construct to test.
     */
    private void _testSetGet(ITyped typed) {
        Topic type = _tm.createTopic();
        Topic type2 = _tm.createTopic();
        assertFalse(type.equals(typed.getType()));
        assertFalse(type2.equals(typed.getType()));
        typed.setType(type);
        assertTrue(type.equals(typed.getType()));
        assertFalse(type2.equals(typed.getType()));
        typed.setType(type2);
        assertFalse(type.equals(typed.getType()));
        assertTrue(type2.equals(typed.getType()));
        typed.setType(null);
        assertNull(typed.getType());
    }
}
