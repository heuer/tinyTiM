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
import org.tmapi.core.Topic;

/**
 * Tests against the {@link TopicUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTopicUtils extends TinyTimTestCase {

    /**
     * Tests if a topic is considered as 'removable'.
     */
    public void testRemovable() {
        Topic topic = _tm.createTopic();
        assertTrue(TopicUtils.isRemovable(topic));
        Association assoc = _tm.createAssociation();
        // Type
        assoc.setType(topic);
        assertFalse(TopicUtils.isRemovable(topic));
        assoc.setType(null);
        assertTrue(TopicUtils.isRemovable(topic));
        // Role played
        AssociationRole role = assoc.createAssociationRole(topic, _tm.createTopic());
        assertFalse(TopicUtils.isRemovable(topic));
        role.setPlayer(null);
        assertTrue(TopicUtils.isRemovable(topic));
        // Theme
        assoc.addScopingTopic(topic);
        assertFalse(TopicUtils.isRemovable(topic));
        assoc.removeScopingTopic(topic);
        assertTrue(TopicUtils.isRemovable(topic));
        // Reifier
        ((IReifiable) assoc).setReifier(topic);
        assertTrue(TopicUtils.isRemovable(topic));
        assertFalse(TopicUtils.isRemovable(topic, true));
        ((IReifiable) assoc).setReifier(null);
        assertTrue(TopicUtils.isRemovable(topic));
        assertTrue(TopicUtils.isRemovable(topic, true));
        // Topic type
        Topic topic2 = _tm.createTopic();
        topic2.addType(topic);
        assertFalse(TopicUtils.isRemovable(topic));
        topic2.removeType(topic);
        assertTrue(TopicUtils.isRemovable(topic));
    }

}
