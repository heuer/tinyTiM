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
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against the {@link ReificationUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestReificationUtils extends TinyTimTestCase {

    /**
     * Tests reification of a topic map
     */
    public void testTopicMap() throws Exception {
        _testReification(_tm);
    }

    /**
     * Tests reification of a topic (which is not possible)
     */
    public void testTopic() {
        try {
            ReificationUtils.getReifier(_tm.createTopic());
            fail("Topic cannot be reified");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    /**
     * Tests reification of an association.
     */
    public void testAssociation() throws Exception {
        _testReification(_tm.createAssociation());
    }

    /**
     * Tests reification of a role.
     */
    public void testRole() throws Exception {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        _testReification(role);
    }

    /**
     * Tests reification of an occurrence.
     */
    public void testOccurrence() throws Exception {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testReification(occ);
    }

    /**
     * Tests reification of a name.
     */
    public void testName() throws Exception {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testReification(name);
    }

    /**
     * Tests reification of a variant.
     */
    public void testVariant() throws Exception {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        Variant variant = name.createVariant("tinyTiM", null);
        _testReification(variant);
    }

    /**
     * The reification test.
     *
     * @param tmo The Topic Maps construct to test.
     * @throws Exception
     */
    private void _testReification(TopicMapObject tmo) throws Exception {
        assertTrue(tmo.getSourceLocators().isEmpty());
        assertNull(ReificationUtils.getReifier(tmo));
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim/#example");
        Topic reifier = _tm.createTopic();
        assertTrue(reifier.getSubjectIdentifiers().isEmpty());
        assertEquals(0, ReificationUtils.getReified(reifier).size());
        reifier.addSubjectIdentifier(loc);
        assertEquals(1, reifier.getSubjectIdentifiers().size());
        assertEquals(0, ReificationUtils.getReified(reifier).size());
        tmo.addSourceLocator(loc);
        assertEquals(1, ReificationUtils.getReified(reifier).size());
        assertTrue(ReificationUtils.getReified(reifier).contains(tmo));
        assertEquals(reifier, ReificationUtils.getReifier(tmo));
        tmo.removeSourceLocator(loc);
        assertEquals(0, ReificationUtils.getReified(reifier).size());
        assertFalse(ReificationUtils.getReified(reifier).contains(tmo));
        assertNull(ReificationUtils.getReifier(tmo));
        if (!(tmo instanceof TopicMap)) {
            tmo.addSourceLocator(loc);
            assertEquals(1, ReificationUtils.getReified(reifier).size());
            assertTrue(ReificationUtils.getReified(reifier).contains(tmo));
            assertEquals(reifier, ReificationUtils.getReifier(tmo));
            tmo.remove();
            assertEquals(0, ReificationUtils.getReified(reifier).size());
        }
    }
}
