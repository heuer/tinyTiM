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
import org.tmapi.core.TopicMap;

/**
 * Tests merging of topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTopicMapMerge extends TinyTimTestCase {

    private static final String _TM2_BASE = "http://www.sf.net/projects/tinytim/tm-2";

    private TopicMap _tm2;

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _tm2 = _sys.createTopicMap(_TM2_BASE);
    }

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _tm2 = null;
    }

    /**
     * Tests merging of topics by equal item identifiers.
     */
    public void testMergeSimple1() {
        final String ref = "http://sf.net/projects/tinytim/loc";
        Topic topicA = _tm.createTopic();
        Locator iidA = _tm.createLocator(ref);
        topicA.addSourceLocator(iidA);
        Topic topicB = _tm2.createTopic();
        Locator iidB = _tm2.createLocator(ref);
        topicB.addSourceLocator(iidB);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(1, _tm2.getTopics().size());

        _tm.mergeIn(_tm2);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(topicA, _tm.getObjectByItemIdentifier(iidA));
    }

    /**
     * Tests merging of topics by equal subject identifiers.
     */
    public void testMergeSimple2() {
        final String ref = "http://sf.net/projects/tinytim/loc";
        Topic topicA = _tm.createTopic();
        Locator sidA = _tm.createLocator(ref);
        topicA.addSubjectIdentifier(sidA);
        Topic topicB = _tm2.createTopic();
        Locator sidB = _tm2.createLocator(ref);
        topicB.addSubjectIdentifier(sidB);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(1, _tm2.getTopics().size());

        _tm.mergeIn(_tm2);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(topicA, _tm.getTopicBySubjectIdentifier(sidA));
    }

    /**
     * Tests merging of topics by equal subject locators.
     */
    public void testMergeSimple3() {
        final String ref = "http://sf.net/projects/tinytim/loc";
        Topic topicA = _tm.createTopic();
        Locator sloA = _tm.createLocator(ref);
        topicA.addSubjectLocator(sloA);
        Topic topicB = _tm2.createTopic();
        Locator sloB = _tm2.createLocator(ref);
        topicB.addSubjectLocator(sloB);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(1, _tm2.getTopics().size());

        _tm.mergeIn(_tm2);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(topicA, _tm.getTopicBySubjectLocator(sloA));
    }

    /**
     * Tests merging of topics by existing topic with item identifier equals
     * to a topic's subject identifier from the other map.
     */
    public void testMergeSimple4() {
        final String ref = "http://sf.net/projects/tinytim/loc";
        Topic topicA = _tm.createTopic();
        Locator loc = _tm.createLocator(ref);
        topicA.addSourceLocator(loc);
        Topic topicB = _tm2.createTopic();
        Locator locB = _tm2.createLocator(ref);
        topicB.addSubjectIdentifier(locB);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(1, _tm2.getTopics().size());
        assertEquals(topicA, _tm.getObjectByItemIdentifier(loc));
        assertNull(_tm.getTopicBySubjectIdentifier(loc));
        _tm.mergeIn(_tm2);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(topicA, _tm.getObjectByItemIdentifier(loc));
        assertEquals(topicA, _tm.getTopicBySubjectIdentifier(loc));
    }

    /**
     * Tests merging of topics by existing topic with subject identifier equals
     * to a topic's item identifier from the other map.
     */
    public void testMergeSimple5() {
        final String ref = "http://sf.net/projects/tinytim/loc";
        Topic topicA = _tm.createTopic();
        Locator loc = _tm.createLocator(ref);
        topicA.addSubjectIdentifier(loc);
        Topic topicB = _tm2.createTopic();
        Locator locB = _tm2.createLocator(ref);
        topicB.addSourceLocator(locB);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(1, _tm2.getTopics().size());
        assertNull(_tm.getObjectByItemIdentifier(loc));
        assertEquals(topicA, _tm.getTopicBySubjectIdentifier(loc));
        _tm.mergeIn(_tm2);
        assertEquals(1, _tm.getTopics().size());
        assertEquals(topicA, _tm.getObjectByItemIdentifier(loc));
        assertEquals(topicA, _tm.getTopicBySubjectIdentifier(loc));
    }
}
