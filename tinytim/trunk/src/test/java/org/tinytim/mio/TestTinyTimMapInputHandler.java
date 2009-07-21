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
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;

/**
 * Tests against the {@link org.tinytim.mio.TinyTimMapInputHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 209 $ - $Date: 2008-11-19 14:45:23 +0100 (Mi, 19 Nov 2008) $
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
     * <a href="http://code.google.com/p/mappa/issues/detail?id=23">http://code.google.com/p/mappa/issues/detail?id=23</a>
     */
    public void testMappaIssue23() throws Exception {
        String iid = "http://mappa.semagia.com/issue-23";
        String iid2 = "http://mappa.semagia.com/issue-23_";
        final IRef TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME.getReference());
        TinyTimMapInputHandler handler = _handler;
        handler.startTopicMap();
        handler.startTopic(Ref.createItemIdentifier(iid));
        handler.startName();
        handler.value("test");
        handler.startType();
        handler.topicRef(TOPIC_NAME);
        handler.endType();
        handler.endName();
        handler.endTopic();
        handler.startTopic(Ref.createItemIdentifier(iid2));
        handler.startName();
        handler.value("a test");
        handler.startType();
        handler.topicRef(TOPIC_NAME);
        handler.endType();
        handler.endName();
        handler.subjectIdentifier(TOPIC_NAME.getIRI());
        handler.endTopic();
        handler.endTopicMap();
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, _tm.getAssociations().size());
        final Association assoc = _tm.getAssociations().iterator().next();
        assertNotNull(assoc.getReifier());
        final Construct tmc = _tm.getConstructByItemIdentifier(createLocator(roleIID));
        assertNotNull(tmc);
        assertEquals(assoc, tmc.getParent());
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84_2() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, _tm.getAssociations().size());
        final Association assoc = _tm.getAssociations().iterator().next();
        assertNotNull(assoc.getReifier());
        final Construct tmc = _tm.getConstructByItemIdentifier(createLocator(roleIID));
        assertNotNull(tmc);
        assertEquals(assoc, tmc.getParent());
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84RoleReifier() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, _tm.getAssociations().size());
        final Association assoc = _tm.getAssociations().iterator().next();
        assertNull(assoc.getReifier());
        final Construct tmc = _tm.getConstructByItemIdentifier(createLocator(roleIID));
        assertNotNull(tmc);
        assertEquals(assoc, tmc.getParent());
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84RoleReifier2() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef assocType2 = Ref.createItemIdentifier("http://test.semagia.com/assoc-type2");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final String reifierIID = "http://test.semagia.com/reifier";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        try {
            handler.startAssociation();
            handler.startType();
            handler.topicRef(assocType2);
            handler.endType();
            handler.startRole();
            handler.startReifier();
            handler.topicRef(reifier);
            handler.endReifier();
            handler.startType();
            handler.topicRef(roleType);
            handler.endType();
            handler.startPlayer();
            handler.topicRef(rolePlayer);
            handler.endPlayer();
            handler.endRole();
            handler.endAssociation();
            handler.endTopicMap();
            fail("The topic " + reifierIID + " reifies another role");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(variantIID);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();
        handler.endTopic();
        handler.endTopicMap();
        Topic reifying = (Topic) _tm.getConstructByItemIdentifier(createLocator(reifierIID));
        assertNotNull(reifying);
        assertNotNull(reifying.getReified());
        assertEquals(reifying.getReified(), _tm.getConstructByItemIdentifier(createLocator(variantIID)));
    }
    
    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier2() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(variantIID);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();
        handler.endTopic();
        handler.endTopicMap();
        Topic reifying = (Topic) _tm.getConstructByItemIdentifier(createLocator(reifierIID));
        assertNotNull(reifying);
        assertNotNull(reifying.getReified());
        assertEquals(reifying.getReified(), _tm.getConstructByItemIdentifier(createLocator(variantIID)));
    }
    
    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier3() throws Exception {
        TinyTimMapInputHandler handler = _handler;
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        try {
            handler.startName();
            handler.value("Not Semagia");
            handler.startVariant();
            handler.startReifier();
            handler.topicRef(reifier);
            handler.endReifier();
            handler.itemIdentifier(variantIID);
            handler.startScope();
            handler.startTheme();
            handler.topicRef(theme);
            handler.endTheme();
            handler.endScope();
            handler.endVariant();
            handler.endName();
            handler.endTopic();
            handler.endTopicMap();
            fail("The topic " + reifierIID + " reifies a variant of another name which is not equal");
        }
        catch (MIOException ex) {
            // noop.
        }
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
