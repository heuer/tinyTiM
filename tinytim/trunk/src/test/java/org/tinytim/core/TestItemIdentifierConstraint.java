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
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

/**
 * Tests if the TMDM item identifier constraint is respected.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
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
        topic.addItemIdentifier(iid);
        assertTrue(topic.getItemIdentifiers().contains(iid));
        Topic topic2 = _tm.createTopic();
        try {
            topic2.addItemIdentifier(iid);
        }
        catch (IdentityConstraintException ex) {
            // noop.
        }
        topic.removeItemIdentifier(iid);
        assertFalse(topic.getItemIdentifiers().contains(iid));
        topic2.addItemIdentifier(iid);
        assertTrue(topic2.getItemIdentifiers().contains(iid));
        topic2.removeItemIdentifier(iid);
        topic.addItemIdentifier(iid);
        assertTrue(topic.getItemIdentifiers().contains(iid));
        assertFalse(topic2.getItemIdentifiers().contains(iid));
        topic.remove();
        topic2.addItemIdentifier(iid);
        assertTrue(topic2.getItemIdentifiers().contains(iid));
    }

    /**
     * Tests against an association.
     */
    public void testAssociation() throws Exception {
        _testConstraint(_tm.createAssociation(_tm.createTopic()));
    }

    /**
     * Tests against a role.
     */
    public void testRole() throws Exception {
        Association assoc = _tm.createAssociation(_tm.createTopic());
        Role role = assoc.createRole(_tm.createTopic(), _tm.createTopic());
        _testConstraint(role);
    }

    /**
     * Tests against an occurrence.
     */
    public void testOccurrence() throws Exception {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence(_tm.createTopic(), "tinyTiM");
        _testConstraint(occ);
    }

    /**
     * Tests against a name.
     */
    public void testName() throws Exception {
        Topic topic = _tm.createTopic();
        Name name = topic.createName("tinyTiM");
        _testConstraint(name);
    }

    /**
     * Tests against a variant.
     */
    public void testVariant() throws Exception {
        Topic topic = _tm.createTopic();
        Name name = topic.createName("tinyTiM");
        Variant variant = name.createVariant("tinyTiM", _tm.createTopic());
        _testConstraint(variant);
    }

    /**
     * The item identifier constraint test.
     *
     * @param tmo The Topic Maps construct to test.
     */
    private void _testConstraint(Construct tmo) throws Exception {
        assertTrue(tmo.getItemIdentifiers().isEmpty());
        Locator iid = _tm.createLocator("http://sf.net/projects/tinytim");
        tmo.addItemIdentifier(iid);
        assertTrue(tmo.getItemIdentifiers().contains(iid));
        Association assoc = _tm.createAssociation(_tm.createTopic());
        try {
            assoc.addItemIdentifier(iid);
            fail("Topic Maps constructs with the same item identifier are not allowed");
        }
        catch (IdentityConstraintException ex) {
            // noop
        }
        tmo.removeItemIdentifier(iid);
        assertFalse(tmo.getItemIdentifiers().contains(iid));
        assoc.addItemIdentifier(iid);
        assertTrue(assoc.getItemIdentifiers().contains(iid));
        assoc.removeItemIdentifier(iid);
        assertFalse(assoc.getItemIdentifiers().contains(iid));
        tmo.addItemIdentifier(iid);
        assertTrue(tmo.getItemIdentifiers().contains(iid));
        if (!(tmo instanceof TopicMap)) {
            // Removal should 'free' the item identifier
            tmo.remove();
            assoc.addItemIdentifier(iid);
            assertTrue(assoc.getItemIdentifiers().contains(iid));
        }
    }
}
