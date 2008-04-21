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
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against the {@link org.tinytim.IReifiable} interface.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestReifiable extends TinyTimTestCase {

    /**
     * Tests if a Topic Maps construct is an instance of IReifiable.
     */
    public void testInstanceOf() {
        assertTrue(((TopicMap)_tm) instanceof IReifiable);
        Topic topic = _tm.createTopic();
        assertFalse(topic instanceof IReifiable);
        Association assoc = _tm.createAssociation();
        assertTrue(assoc instanceof IReifiable);
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        assertTrue(role instanceof IReifiable);
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        assertTrue(occ instanceof IReifiable);
        TopicName name = topic.createTopicName("tinyTiM", null);
        assertTrue(name instanceof IReifiable);
        Variant variant = name.createVariant("tinyTiM", null);
        assertTrue(variant instanceof IReifiable);
    }

    /**
     * Tests setting and getting the reifier of a topic map.
     */
    public void testTopicMap() {
        _testSetGet((IReifiable)_tm);
    }

    /**
     * Tests setting and getting the reifier of an association.
     */
    public void testAssociation() {
        _testSetGet((IReifiable)_tm.createAssociation());
    }

    /**
     * Tests setting and getting the reifier of a role.
     */
    public void testRole() {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        _testSetGet((IReifiable)role);
    }

    /**
     * Tests setting and getting the reifier of an occurrence.
     */
    public void testOccurrence() {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testSetGet((IReifiable)occ);
    }

    /**
     * Tests setting and getting the reifier of a name.
     */
    public void testName() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testSetGet((IReifiable)name);
    }

    /**
     * Tests setting and getting the reifier of a variant.
     */
    public void testVariant() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        Variant variant = name.createVariant("tinyTiM", null);
        _testSetGet((IReifiable)variant);
    }

    /**
     * Tests setting and getting the reifier of a reifiable Topic Maps construct.
     *
     * @param reifiable The Topic Maps construct to test.
     */
    private void _testSetGet(IReifiable reifiable) {
        assertNull(reifiable.getReifier());
        TopicImpl reifier = (TopicImpl) _tm.createTopic();
        assertEquals(0, reifier.getReified().size());
        reifiable.setReifier(reifier);
        assertEquals(reifier, reifiable.getReifier());
        assertEquals(reifiable, reifier._reified);
        assertEquals(1, reifier.getReified().size());
        assertTrue(reifier.getReified().contains(reifiable));
        reifiable.setReifier(null);
        assertNull(reifiable.getReifier());
        assertNull(reifier._reified);
        assertEquals(0, reifier.getReified().size());

        TopicImpl reifier2 = (TopicImpl) _tm.createTopic();
        IReifiable assoc = (IReifiable) _tm.createAssociation();
        assoc.setReifier(reifier2);
        assertEquals(reifier2, assoc.getReifier());
        assertEquals(assoc, reifier2._reified);
        try {
            reifiable.setReifier(reifier2);
            fail("Expected an exception. The reifier reifies another Topic Maps construct");
        }
        catch (ModelConstraintException ex) {
            // noop.
        }
        assoc.setReifier(null);
        assertNull(assoc.getReifier());
        assertNull(reifier2._reified);
        reifiable.setReifier(reifier);
        assertEquals(reifier, reifiable.getReifier());
        assertEquals(reifiable, reifier._reified);
        reifiable.setReifier(reifier2);
        assertEquals(reifier2, reifiable.getReifier());
        assertEquals(reifiable, reifier2._reified);
    }
}
