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
import org.tmapi.core.DuplicateSourceLocatorException;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.TopicsMustMergeException;
import org.tmapi.core.Variant;

/**
 * Tests if the TMDM item identifier constraint is respected.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestItemIdentifierConstraint extends TinyTimTestCase {

    /**
     * Tests against a topic map.
     */
    public void testTopicMap() throws Exception {
        _testConstraint(_tm);
    }

    /**
     * Tests againts a topic.
     */
    public void testTopic() throws Exception {
        Topic topic = _tm.createTopic();
        Locator iid = _tm.createLocator("http://sf.net/projects/tinytim");
        topic.addSourceLocator(iid);
        assertTrue(topic.getSourceLocators().contains(iid));
        Topic topic2 = _tm.createTopic();
        try {
            topic2.addSourceLocator(iid);
        }
        catch (TopicsMustMergeException ex) {
            // noop.
        }
        topic.removeSourceLocator(iid);
        assertFalse(topic.getSourceLocators().contains(iid));
        topic2.addSourceLocator(iid);
        assertTrue(topic2.getSourceLocators().contains(iid));
        topic2.removeSourceLocator(iid);
        topic.addSourceLocator(iid);
        assertTrue(topic.getSourceLocators().contains(iid));
        assertFalse(topic2.getSourceLocators().contains(iid));
        topic.remove();
        topic2.addSourceLocator(iid);
        assertTrue(topic2.getSourceLocators().contains(iid));
    }

    /**
     * Tests against an association.
     */
    public void testAssociation() throws Exception {
        _testConstraint(_tm.createAssociation());
    }

    /**
     * Tests against a role.
     */
    public void testRole() throws Exception {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        _testConstraint(role);
    }

    /**
     * Tests against an occurrence.
     */
    public void testOccurrence() throws Exception {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testConstraint(occ);
    }

    /**
     * Tests against a name.
     */
    public void testName() throws Exception {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testConstraint(name);
    }

    /**
     * Tests against a variant.
     */
    public void testVariant() throws Exception {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        Variant variant = name.createVariant("tinyTiM", null);
        _testConstraint(variant);
    }

    /**
     * The item identifier constraint test.
     *
     * @param tmo The Topic Maps construct to test.
     */
    private void _testConstraint(TopicMapObject tmo) throws Exception {
        assertTrue(tmo.getSourceLocators().isEmpty());
        Locator iid = _tm.createLocator("http://sf.net/projects/tinytim");
        tmo.addSourceLocator(iid);
        assertTrue(tmo.getSourceLocators().contains(iid));
        Association assoc = _tm.createAssociation();
        try {
            assoc.addSourceLocator(iid);
            fail("Topic Maps constructs with the same item identifier are not allowed");
        }
        catch (DuplicateSourceLocatorException ex) {
            // noop
        }
        tmo.removeSourceLocator(iid);
        assertFalse(tmo.getSourceLocators().contains(iid));
        assoc.addSourceLocator(iid);
        assertTrue(assoc.getSourceLocators().contains(iid));
        assoc.removeSourceLocator(iid);
        assertFalse(assoc.getSourceLocators().contains(iid));
        tmo.addSourceLocator(iid);
        assertTrue(tmo.getSourceLocators().contains(iid));
        if (!(tmo instanceof TopicMap)) {
            // Removal should 'free' the item identifier
            tmo.remove();
            assoc.addSourceLocator(iid);
            assertTrue(assoc.getSourceLocators().contains(iid));
        }
    }
}
