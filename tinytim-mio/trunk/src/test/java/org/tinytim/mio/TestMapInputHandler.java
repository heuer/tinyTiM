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
package org.tinytim.mio;

import org.tinytim.Property;
import org.tinytim.TopicMapImpl;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.TopicName;

import com.semagia.mio.helpers.Ref;

import junit.framework.TestCase;

/**
 * Tests against the {@link org.tinytim.mio.MapInputHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestMapInputHandler extends TestCase {

    private TopicMapImpl _tm;
    private MapInputHandler _handler;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        TopicMapSystemFactory tmSysFactory = TopicMapSystemFactory.newInstance();
        tmSysFactory.setProperty(Property.XTM10_REIFICATION, "false");
        TopicMapSystem tmSys = tmSysFactory.newTopicMapSystem();
        _tm = (TopicMapImpl) tmSys.createTopicMap("http://sf.net/projects/tinytim/test");
        _handler = new MapInputHandler();
        _handler.setTopicMap(_tm);
    }

    public void testEmpty() throws Exception {
        assertEquals(0, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
        _handler.startTopicMap();
        _handler.endTopicMap();
        assertEquals(0, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
    }

    public void testTopicIdentityItemIdentifier() throws Exception {
        String itemIdent = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createItemIdentifier(itemIdent));
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Topic topic = (Topic) _tm.getObjectByItemIdentifier(_tm.createLocator(itemIdent));
        assertNotNull(topic);
    }

    public void testTopicIdentitySubjectIdentifier() throws Exception {
        String subjIdent = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(subjIdent));
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(subjIdent));
        assertNotNull(topic);
    }

    public void testTopicIdentitySubjectLocator() throws Exception {
        String subjLoc = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectLocator(subjLoc));
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Topic topic = _tm.getTopicBySubjectLocator(_tm.createLocator(subjLoc));
        assertNotNull(topic);
    }

    public void testTopicMerging1() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String itemIdent = "http://example.org/1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startTopic(Ref.createItemIdentifier(itemIdent));
        _handler.itemIdentifier(ref);
        _handler.endTopic();
        _handler.startName();
        _handler.value("tinyTiM");
        _handler.endName();
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        assertEquals(topic, _tm.getObjectByItemIdentifier(_tm.createLocator(ref)));
        assertEquals(topic, _tm.getObjectByItemIdentifier(_tm.createLocator(itemIdent)));
        assertEquals(1, topic.getTopicNames().size());
        TopicName name = (TopicName)topic.getTopicNames().iterator().next();
        assertEquals("tinyTiM", name.getValue());
    }

    public void testTopicMapReifier() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startReifier();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.endTopic();
        _handler.endReifier();
        _handler.endTopicMap();
        assertNotNull(_tm.getReifier());
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        assertEquals(topic, _tm.getReifier());
    }

}
