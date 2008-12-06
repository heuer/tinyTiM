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
package org.tinytim.mio;

import org.tinytim.core.TinyTimTestCase;
import org.tinytim.voc.TMDM;
import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;

/**
 * Tests against the {@link org.tinytim.mio.TinyTimMapInputHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTinyTimMapInputHandler extends TinyTimTestCase {

    private static final String _XSD_STRING = XSD.STRING.getReference();
    private static final String _XSD_ANY_URI = XSD.ANY_URI.getReference();

    private TinyTimMapInputHandler _handler;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _handler = new TinyTimMapInputHandler(_tm);
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
        Topic topic = (Topic) _tm.getConstructByItemIdentifier(_tm.createLocator(itemIdent));
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
        Topic topic = (Topic) _tm.getConstructByItemIdentifier(_tm.createLocator(itemIdent));
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
        assertEquals(topic, _tm.getConstructByItemIdentifier(_tm.createLocator(ref)));
        assertEquals(topic, _tm.getConstructByItemIdentifier(_tm.createLocator(itemIdent)));
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
        assertEquals(topic, _tm.getConstructByItemIdentifier(loc));
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
        assertEquals(topic, _tm.getConstructByItemIdentifier(loc));
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
        assertEquals(XSD.STRING, occ.getDatatype());
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
        assertEquals(val, occ.getValue());
        assertEquals(XSD.ANY_URI, occ.getDatatype());
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
        Name name = topic.getNames().iterator().next();
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

    /**
     * Tests nested startTopic/endTopic events.
     */
    public void testNestedTopics() throws Exception {
        String base = "http://tinytim.sourceforge.net/test-nesting#";
        final int MAX = 10000;
        String[] iids = new String[MAX];
        _handler.startTopicMap();
        for (int i=0; i<MAX; i++) {
            iids[i] = base + i;
            _handler.startTopic(Ref.createItemIdentifier(iids[i]));
        }
        for (int i=0; i<MAX; i++) {
            _handler.endTopic();
        }
        _handler.endTopicMap();
        for (String iid: iids) {
            assertNotNull(_tm.getConstructByItemIdentifier(createLocator(iid)));
        }
    }

}
