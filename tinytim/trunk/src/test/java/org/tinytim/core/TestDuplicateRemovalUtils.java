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
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;

/**
 * Tests against the {@link DuplicateRemovalUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestDuplicateRemovalUtils extends TinyTimTestCase {

    public void testTopicRemoveNames() {
        Topic topic = _tm.createTopic();
        Topic nameType = _tm.createTopic();
        Name name1 = topic.createName(nameType, "tinyTiM");
        Name name2 = topic.createName(nameType, "tinyTiM");
        assertEquals(nameType, name1.getType());
        assertTrue(name1.getScope().isEmpty());
        assertEquals(nameType, name2.getType());
        assertTrue(name2.getScope().isEmpty());
        assertEquals(2, topic.getNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getNames().size());
        Name name = (Name) topic.getNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertEquals(nameType, name.getType());
        assertTrue(name.getScope().isEmpty());
    }

    public void testTopicRemoveNames2() {
        Topic topic = _tm.createTopic();
        Topic nameType = _tm.createTopic();
        Name name1 = topic.createName(nameType, "tinyTiM");
        Name name2 = topic.createName(nameType, "tinyTiM");
        Locator iid1 = _tm.createLocator("http://example.org/iid-1");
        Locator iid2 = _tm.createLocator("http://example.org/iid-2");
        name1.addItemIdentifier(iid1);
        name2.addItemIdentifier(iid2);
        assertEquals(2, topic.getNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getNames().size());
        Name name = (Name) topic.getNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertEquals(nameType, name.getType());
        assertTrue(name.getScope().isEmpty());
        assertEquals(2, name.getItemIdentifiers().size());
        assertTrue(name.getItemIdentifiers().contains(iid1));
        assertTrue(name.getItemIdentifiers().contains(iid2));
    }

    public void testTopicRemoveNames3() {
        Topic topic = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();
        Topic nameType = _tm.createTopic();
        Name name1 = topic.createName(nameType, "tinyTiM", theme1, theme2);
        Name name2 = topic.createName(nameType, "tinyTiM", theme2, theme1);
        assertEquals(2, name1.getScope().size());
        assertEquals(2, name2.getScope().size());
        assertEquals(2, topic.getNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getNames().size());
        Name name = (Name) topic.getNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertEquals(nameType, name.getType());
        assertEquals(2, name.getScope().size());
    }

    public void testRemoveRoles() {
        Association assoc = _tm.createAssociation(_tm.createTopic());
        Topic type = _tm.createTopic();
        Topic player = _tm.createTopic();
        assoc.createRole(type, player);
        assoc.createRole(type, player);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getRoles().size());
        DuplicateRemovalUtils.removeDuplicates(assoc);
        assertEquals(1, player.getRolesPlayed().size());
        assertEquals(1, assoc.getRoles().size());
    }
}
