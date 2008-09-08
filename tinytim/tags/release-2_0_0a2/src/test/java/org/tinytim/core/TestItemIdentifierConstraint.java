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
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * Tests if the TMDM item identifier constraint is respected.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestItemIdentifierConstraint extends TinyTimTestCase {

    /**
     * The item identifier constraint test.
     *
     * @param tmo The Topic Maps construct to test.
     */
    private void _testConstraint(final Construct tmo) {
        assertTrue(tmo.getItemIdentifiers().isEmpty());
        final Locator iid = createLocator("http://sf.net/projects/tinytim");
        final Locator iid2 = createLocator("http://sf.net/projects/tinytim2");
        final Association assoc = createAssociation();
        assoc.addItemIdentifier(iid);
        assertFalse(tmo.getItemIdentifiers().contains(iid));
        try {
            tmo.addItemIdentifier(iid);
            fail("Topic Maps constructs with the same item identifier are not allowed");
        }
        catch (IdentityConstraintException ex) {
            // noop
        }
        tmo.addItemIdentifier(iid2);
        assertTrue(tmo.getItemIdentifiers().contains(iid2));
        tmo.removeItemIdentifier(iid2);
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

    /**
     * Tests against a topic map.
     */
    public void testTopicMap() {
        _testConstraint(_tm);
    }

    /**
     * Tests againts a topic.
     */
    public void testTopic() {
        Topic topic = createTopic();
        Locator iid = createLocator("http://sf.net/projects/tinytim");
        topic.addItemIdentifier(iid);
        assertTrue(topic.getItemIdentifiers().contains(iid));
        Topic topic2 = createTopic();
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
    public void testAssociation() {
        _testConstraint(createAssociation());
    }

    /**
     * Tests against a role.
     */
    public void testRole() {
        _testConstraint(createRole());
    }

    /**
     * Tests against an occurrence.
     */
    public void testOccurrence() {
        _testConstraint(createOccurrence());
    }

    /**
     * Tests against a name.
     */
    public void testName() {
        _testConstraint(createName());
    }

    /**
     * Tests against a variant.
     */
    public void testVariant() {
        _testConstraint(createVariant());
    }

}
