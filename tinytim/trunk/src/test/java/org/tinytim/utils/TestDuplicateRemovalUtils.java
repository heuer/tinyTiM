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
        Topic topic = createTopic();
        Topic nameType = createTopic();
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
        Topic topic = createTopic();
        Topic nameType = createTopic();
        Name name1 = topic.createName(nameType, "tinyTiM");
        Name name2 = topic.createName(nameType, "tinyTiM");
        Locator iid1 = createLocator("http://example.org/iid-1");
        Locator iid2 = createLocator("http://example.org/iid-2");
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
        Topic topic = createTopic();
        Topic theme1 = createTopic();
        Topic theme2 = createTopic();
        Topic nameType = createTopic();
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
        Association assoc = createAssociation();
        Topic type = createTopic();
        Topic player = createTopic();
        assoc.createRole(type, player);
        assoc.createRole(type, player);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getRoles().size());
        DuplicateRemovalUtils.removeDuplicates(assoc);
        assertEquals(1, player.getRolesPlayed().size());
        assertEquals(1, assoc.getRoles().size());
    }
}
