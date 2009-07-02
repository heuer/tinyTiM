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

import java.util.Arrays;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;

/**
 * Tests against the {@link DuplicateRemovalUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestDuplicateRemovalUtils extends TinyTimTestCase {

    public void testTopicRemoveNames() {
        Topic topic = _tm.createTopic();
        TopicName name1 = topic.createTopicName("tinyTiM", null);
        TopicName name2 = topic.createTopicName("tinyTiM", null);
        assertNull(name1.getType());
        assertTrue(name1.getScope().isEmpty());
        assertNull(name2.getType());
        assertTrue(name2.getScope().isEmpty());
        assertEquals(2, topic.getTopicNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getTopicNames().size());
        TopicName name = (TopicName) topic.getTopicNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertNull(name.getType());
        assertTrue(name.getScope().isEmpty());
    }

    public void testTopicRemoveNames2() {
        Topic topic = _tm.createTopic();
        TopicName name1 = topic.createTopicName("tinyTiM", null);
        TopicName name2 = topic.createTopicName("tinyTiM", null);
        Locator iid1 = _tm.createLocator("http://example.org/iid-1");
        Locator iid2 = _tm.createLocator("http://example.org/iid-2");
        name1.addSourceLocator(iid1);
        name2.addSourceLocator(iid2);
        assertEquals(2, topic.getTopicNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getTopicNames().size());
        TopicName name = (TopicName) topic.getTopicNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertNull(name.getType());
        assertTrue(name.getScope().isEmpty());
        assertEquals(2, name.getSourceLocators().size());
        assertTrue(name.getSourceLocators().contains(iid1));
        assertTrue(name.getSourceLocators().contains(iid2));
    }

    public void testTopicRemoveNames3() {
        Topic topic = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();
        Collection<Topic> scope1 = Arrays.asList(new Topic[] {theme1, theme2});
        Collection<Topic> scope2 = Arrays.asList(new Topic[] {theme2, theme1});
        TopicName name1 = topic.createTopicName("tinyTiM", scope1);
        TopicName name2 = topic.createTopicName("tinyTiM", scope2);
        assertEquals(2, name1.getScope().size());
        assertEquals(2, name2.getScope().size());
        assertEquals(2, topic.getTopicNames().size());
        DuplicateRemovalUtils.removeDuplicates(topic);
        assertEquals(1, topic.getTopicNames().size());
        TopicName name = (TopicName) topic.getTopicNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
        assertNull(name.getType());
        assertEquals(2, name.getScope().size());
    }

    public void testRemoveRoles() {
        Association assoc = _tm.createAssociation();
        Topic type = _tm.createTopic();
        Topic player = _tm.createTopic();
        assoc.createAssociationRole(player, type);
        assoc.createAssociationRole(player, type);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getAssociationRoles().size());
        DuplicateRemovalUtils.removeDuplicates(assoc);
        assertEquals(1, player.getRolesPlayed().size());
        assertEquals(1, assoc.getAssociationRoles().size());
    }
}
