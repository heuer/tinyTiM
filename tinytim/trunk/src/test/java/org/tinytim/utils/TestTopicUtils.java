/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinytim.utils;

import org.tinytim.core.TinyTimTestCase;
import org.tmapi.core.Association;
import org.tmapi.core.Role;
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
        Topic topic = createTopic();
        assertTrue(TopicUtils.isRemovable(topic));
        Association assoc = _tm.createAssociation(topic);
        assertFalse(TopicUtils.isRemovable(topic));
        assoc.setType(createTopic());
        assertTrue(TopicUtils.isRemovable(topic));
        // Role played
        Role role = assoc.createRole(createTopic(), topic);
        assertFalse(TopicUtils.isRemovable(topic));
        role.setPlayer(createTopic());
        assertTrue(TopicUtils.isRemovable(topic));
        // Theme
        assoc.addTheme(topic);
        assertFalse(TopicUtils.isRemovable(topic));
        assoc.removeTheme(topic);
        assertTrue(TopicUtils.isRemovable(topic));
        // Reifier
        assoc.setReifier(topic);
        assertTrue(TopicUtils.isRemovable(topic));
        assertFalse(TopicUtils.isRemovable(topic, true));
        assoc.setReifier(null);
        assertTrue(TopicUtils.isRemovable(topic));
        assertTrue(TopicUtils.isRemovable(topic, true));
    }

}
