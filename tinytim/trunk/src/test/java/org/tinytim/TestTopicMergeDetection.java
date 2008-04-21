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

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicsMustMergeException;

/**
 * Tests if merging situations are detected.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTopicMergeDetection extends TinyTimTestCase {

    /**
     * Tests if adding a duplicate subject identifier is detected.
     */
    public void testExistingSubjectIdentifier() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectIdentifier(loc);
        assertTrue(topic1.getSubjectIdentifiers().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectIdentifier(loc));
        try {
            topic2.addSubjectIdentifier(loc);
            fail("The duplicate subject identifier '" + loc + "' is not detected");
        }
        catch (TopicsMustMergeException ex) {
            // noop.
        }
    }

    /**
     * Tests if adding a duplicate subject identifier on the SAME topic is ignored.
     */
    public void testExistingSubjectIdentifierLegal() {
        Topic topic1 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectIdentifier(loc);
        assertEquals(1, topic1.getSubjectIdentifiers().size());
        assertTrue(topic1.getSubjectIdentifiers().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectIdentifier(loc));
        topic1.addSubjectIdentifier(loc);
        assertEquals(1, topic1.getSubjectIdentifiers().size());
    }

    /**
     * Tests if adding a duplicate subject locator is detected.
     */
    public void testExistingSubjectLocator() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectLocator(loc);
        assertTrue(topic1.getSubjectLocators().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectLocator(loc));
        try {
            topic2.addSubjectLocator(loc);
            fail("The duplicate subject locator '" + loc + "' is not detected");
        }
        catch (TopicsMustMergeException ex) {
            // noop.
        }
    }

    /**
     * Tests if adding a duplicate subject locator at the SAME topic is ignored.
     */
    public void testExistingSubjectLocatorLegal() {
        Topic topic1 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectLocator(loc);
        assertEquals(1, topic1.getSubjectLocators().size());
        assertTrue(topic1.getSubjectLocators().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectLocator(loc));
        topic1.addSubjectLocator(loc);
        assertEquals(1, topic1.getSubjectLocators().size());
    }

    /**
     * Tests if adding a subject identifier equals to an item identifier is detected.
     */
    public void testExistingSubjectIdentifierItemIdentifier() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectIdentifier(loc);
        assertTrue(topic1.getSubjectIdentifiers().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectIdentifier(loc));
        try {
            topic2.addSourceLocator(loc);
            fail("A topic with a subject identifier equals to the item identifier '" + loc + "' exists.");
        }
        catch (TopicsMustMergeException ex) {
            // noop.
        }
    }

    /**
     * Tests if adding a subject identifier equals to an item identifier 
     * on the SAME topic is accepted
     */
    public void testExistingSubjectIdentifierItemIdentifierLegal() {
        Topic topic1 = _tm.createTopic();
        Locator loc = _tm.createLocator("http://sf.net/projects/tinytim");
        topic1.addSubjectIdentifier(loc);
        assertEquals(1, topic1.getSubjectIdentifiers().size());
        assertEquals(0, topic1.getSourceLocators().size());
        assertTrue(topic1.getSubjectIdentifiers().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectIdentifier(loc));
        assertNull(_tm.getObjectByItemIdentifier(loc));
        topic1.addSourceLocator(loc);
        assertEquals(1, topic1.getSubjectIdentifiers().size());
        assertEquals(1, topic1.getSourceLocators().size());
        assertTrue(topic1.getSubjectIdentifiers().contains(loc));
        assertTrue(topic1.getSourceLocators().contains(loc));
        assertEquals(topic1, _tm.getTopicBySubjectIdentifier(loc));
        assertEquals(topic1, _tm.getObjectByItemIdentifier(loc));
    }
}
