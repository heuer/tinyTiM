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
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against {@link IConstruct}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestConstruct extends TinyTimTestCase {

    /**
     * Tests against the topic map.
     */
    public void testTopicMap() {
        _testConstruct((IConstruct) _tm);
    }

    /**
     * Tests against the topic.
     */
    public void testTopic() {
        _testConstruct((IConstruct) _tm.createTopic());
    }

    /**
     * Tests against the association.
     */
    public void testAssociation() {
        _testConstruct((IConstruct) _tm.createAssociation());
    }

    /**
     * Tests against the role.
     */
    public void testRole() {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(null, null);
        _testConstruct((IConstruct) role);
    }

    /**
     * Tests against an occurrence.
     */
    public void testOccurrence() {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testConstruct((IConstruct) occ);
    }

    /**
     * Tests against a name.
     */
    public void testName() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testConstruct((IConstruct) name);
    }

    /**
     * Tests against a variant.
     */
    public void testVariant() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        Variant variant = name.createVariant("tinyTiM", null);
        _testConstruct((IConstruct) variant);
    }

    /**
     * Tests adding / removing item identifiers, retrieval by item identifier.
     *
     * @param construct The Topic Maps construct to test.
     */
    private void _testConstruct(IConstruct construct) {
        assertEquals(0, construct.getItemIdentifiers().size());
        Locator iid = _tm.createLocator("http://sf.net/projects/tinytim/#test");
        construct.addItemIdentifier(iid);
        assertEquals(1, construct.getItemIdentifiers().size());
        assertTrue(construct.getItemIdentifiers().contains(iid));
        assertEquals(construct, _tm.getObjectByItemIdentifier(iid));
        construct.removeItemIdentifier(iid);
        assertEquals(0, construct.getItemIdentifiers().size());
        assertFalse(construct.getItemIdentifiers().contains(iid));
        assertNull(_tm.getObjectByItemIdentifier(iid));

        String id = construct.getObjectId();
        assertEquals(construct, _tm.getObjectById(id));
    }
}
