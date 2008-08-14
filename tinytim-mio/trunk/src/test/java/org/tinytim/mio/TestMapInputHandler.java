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
import org.tinytim.voc.TMDM;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.TopicName;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;

import junit.framework.TestCase;

/**
 * Tests against the {@link org.tinytim.mio.MapInputHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestMapInputHandler extends TestCase {

    private static final String _XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
    private static final String _XSD_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

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

    /**
     * Simple startTopicMap, followed by an endTopicMap event.
     */
    public void testEmpty() throws Exception {
        assertEquals(0, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
        _handler.startTopicMap();
        _handler.endTopicMap();
        assertEquals(0, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
    }

    /**
     * Tests reifying a topic map.
     */
    public void testTMReifier() throws Exception {
        String itemIdent = "http://sf.net/projects/tinytim/test#1";
        assertEquals(0, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
        _handler.startTopicMap();
        _handler.startReifier();
        _handler.startTopic(Ref.createItemIdentifier(itemIdent));
        _handler.endTopic();
        _handler.endReifier();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        assertEquals(0, _tm.getAssociations().size());
        Topic topic = (Topic) _tm.getObjectByItemIdentifier(_tm.createLocator(itemIdent));
        assertNotNull(topic);
        assertNotNull(_tm.getReifier());
        assertEquals(topic, _tm.getReifier());
    }

    /**
     * Tests topic creation with an item identifier.
     */
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

    /**
     * Tests topic creation with a subject identifier.
     */
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

    /**
     * Tests topic creation with a subject locator.
     */
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

    /**
     * Tests transparent merging.
     */
    public void testTopicMerging() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String itemIdent = "http://example.org/1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        // Topic in topic event
        _handler.startTopic(Ref.createItemIdentifier(itemIdent));
        _handler.itemIdentifier(ref);
        _handler.endTopic();
        _handler.startOccurrence();
        _handler.value("tinyTiM", _XSD_STRING);
        _handler.endOccurrence();
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        assertEquals(topic, _tm.getObjectByItemIdentifier(_tm.createLocator(ref)));
        assertEquals(topic, _tm.getObjectByItemIdentifier(_tm.createLocator(itemIdent)));
        assertEquals(1, topic.getOccurrences().size());
        Occurrence occ = (Occurrence) topic.getOccurrences().iterator().next();
        assertEquals("tinyTiM", occ.getValue());
    }

    /**
     * Tests assigning identities to a topic.
     */
    public void testTopicIdentities1() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.itemIdentifier(ref);
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Locator loc = _tm.createLocator(ref);
        Topic topic = _tm.getTopicBySubjectIdentifier(loc);
        assertNotNull(topic);
        assertEquals(topic, _tm.getObjectByItemIdentifier(loc));
    }

    /**
     * Tests assigning identities to a topic.
     */
    public void testTopicIdentities2() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createItemIdentifier(ref));
        _handler.subjectIdentifier(ref);
        _handler.endTopic();
        _handler.endTopicMap();
        assertEquals(1, _tm.getTopics().size());
        Locator loc = _tm.createLocator(ref);
        Topic topic = _tm.getTopicBySubjectIdentifier(loc);
        assertNotNull(topic);
        assertEquals(topic, _tm.getObjectByItemIdentifier(loc));
    }

    /**
     * Tests reifying the topic map.
     */
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

    /**
     * Tests occurrence creation with a value of datatype xsd:string.
     */
    public void testOccurrenceValueString() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startOccurrence();
        _handler.value(val, _XSD_STRING);
        _handler.endOccurrence();
        _handler.endTopic();
        _handler.endTopicMap();
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        Occurrence occ = (Occurrence) topic.getOccurrences().iterator().next();
        assertEquals(val, occ.getValue());
    }

    /**
     * Tests occurrence creation with a value of datatype xsd:anyURI.
     */
    public void testOccurrenceValueURI() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "http://sf.net/projects/tinytim";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startOccurrence();
        _handler.value(val, _XSD_ANY_URI);
        _handler.endOccurrence();
        _handler.endTopic();
        _handler.endTopicMap();
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        Occurrence occ = (Occurrence) topic.getOccurrences().iterator().next();
        assertNull(occ.getValue());
        assertEquals(val, occ.getResource().getReference());
    }

    /**
     * Tests if the name type is automatically set.
     */
    public void testDefaultNameType() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startName();
        _handler.value(val);
        _handler.endName();
        _handler.endTopic();
        _handler.endTopicMap();
        Topic topic = _tm.getTopicBySubjectIdentifier(_tm.createLocator(ref));
        assertNotNull(topic);
        TopicName name = (TopicName) topic.getTopicNames().iterator().next();
        assertEquals(val, name.getValue());
        assertNotNull(name.getType());
        assertTrue(name.getType().getSubjectIdentifiers().contains(TMDM.TOPIC_NAME));
    }

    /**
     * Tests if a variant with no scope is reported as error.
     */
    public void testVariantNoScopeError() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startName();
        _handler.value(val);
        _handler.startVariant();
        _handler.value(val, _XSD_STRING);
        try {
            _handler.endVariant();
            fail("A variant with no scope shouldn't be allowed");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * Tests if a variant with a scope equals to the parent's scope is rejected.
     */
    public void testVariantNoScopeError2() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String theme = "http://sf.net/projects/tinytim/test#theme";
        String val = "tinyTiM";
        _handler.startTopicMap();
        _handler.startTopic(Ref.createSubjectIdentifier(ref));
        _handler.startName();
        _handler.startScope();
        _handler.startTheme();
        _handler.topicRef(Ref.createItemIdentifier(theme));
        _handler.endTheme();
        _handler.endScope();
        _handler.value(val);
        
        _handler.startVariant();
        _handler.value(val, _XSD_STRING);
        _handler.startScope();
        _handler.startTheme();
        _handler.topicRef(Ref.createItemIdentifier(theme));
        _handler.endTheme();
        _handler.endScope();
        try {
            _handler.endVariant();
            fail("A variant with a scope equals to the parent's scope shouldn't be allowed");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

}
