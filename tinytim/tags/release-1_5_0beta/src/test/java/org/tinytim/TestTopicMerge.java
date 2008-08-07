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

import java.util.Properties;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests merging of topics.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTopicMerge extends TinyTimTestCase {

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#getAdditionalProperties()
     */
    @Override
    protected Properties getAdditionalProperties() {
        Properties props = new Properties();
        props.setProperty(Property.XTM10_REIFICATION, "false");
        return props;
    }

    /**
     * If topics reify different Topic Maps constructs they cannot be merged.
     */
    public void testReifiedClash() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Association assoc1 = _tm.createAssociation();
        Association assoc2 = _tm.createAssociation();
        Topic type1 = _tm.createTopic();
        Topic type2 = _tm.createTopic();
        assoc1.setType(type1);
        assoc2.setType(type2);
        ((IReifiable) assoc1).setReifier(topic1);
        ((IReifiable) assoc2).setReifier(topic2);
        assertEquals(type1, assoc1.getType());
        assertEquals(type2, assoc2.getType());
        assertEquals(topic1, assoc1.getReifier());
        assertEquals(topic2, assoc2.getReifier());
        try {
            topic1.mergeIn(topic2);
            fail("The topics reify different Topic Maps constructs and cannot be merged");
        }
        catch (ModelConstraintException ex) {
            // noop.
        }
    }

    /**
     * Tests if a topic overtakes all roles played of the other topic.
     */
    public void testRolePlaying() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Association assoc = _tm.createAssociation();
        assoc.setType(_tm.createTopic());
        AssociationRole role = assoc.createAssociationRole(topic2, _tm.createTopic());
        assertEquals(4, _tm.getTopics().size());
        assertFalse(topic1.getRolesPlayed().contains(role));
        assertTrue(topic2.getRolesPlayed().contains(role));
        topic1.mergeIn(topic2);
        assertEquals(3, _tm.getTopics().size());
        assertTrue(topic1.getRolesPlayed().contains(role));
    }

    /**
     * Tests if the subject identifiers are overtaken.
     */
    public void testIdentitySubjectIdentifier() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator sid1 = _tm.createLocator("http://psi.exmaple.org/sid-1");
        Locator sid2 = _tm.createLocator("http://psi.exmaple.org/sid-2");
        topic1.addSubjectIdentifier(sid1);
        topic2.addSubjectIdentifier(sid2);
        assertTrue(topic1.getSubjectIdentifiers().contains(sid1));
        assertFalse(topic1.getSubjectIdentifiers().contains(sid2));
        assertFalse(topic2.getSubjectIdentifiers().contains(sid1));
        assertTrue(topic2.getSubjectIdentifiers().contains(sid2));
        topic1.mergeIn(topic2);
        assertEquals(2, topic1.getSubjectIdentifiers().size());
        assertTrue(topic1.getSubjectIdentifiers().contains(sid1));
        assertTrue(topic1.getSubjectIdentifiers().contains(sid2));
    }

    /**
     * Tests if the subject locator are overtaken.
     */
    public void testIdentitySubjectLocator() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator slo1 = _tm.createLocator("http://tinytim.sf.net");
        Locator slo2 = _tm.createLocator("http://tinytim.sourceforge.net");
        topic1.addSubjectLocator(slo1);
        topic2.addSubjectLocator(slo2);
        assertTrue(topic1.getSubjectLocators().contains(slo1));
        assertFalse(topic1.getSubjectLocators().contains(slo2));
        assertFalse(topic2.getSubjectLocators().contains(slo1));
        assertTrue(topic2.getSubjectLocators().contains(slo2));
        topic1.mergeIn(topic2);
        assertEquals(2, topic1.getSubjectLocators().size());
        assertTrue(topic1.getSubjectLocators().contains(slo1));
        assertTrue(topic1.getSubjectLocators().contains(slo2));
    }

    /**
     * Tests if the item identifiers are overtaken.
     */
    public void testIdentityItemIdentifier() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator iid1 = _tm.createLocator("http://tinytim.sf.net/test#1");
        Locator iid2 = _tm.createLocator("http://tinytim.sf.net/test#2");
        topic1.addSourceLocator(iid1);
        topic2.addSourceLocator(iid2);
        assertTrue(topic1.getSourceLocators().contains(iid1));
        assertFalse(topic1.getSourceLocators().contains(iid2));
        assertFalse(topic2.getSourceLocators().contains(iid1));
        assertTrue(topic2.getSourceLocators().contains(iid2));
        topic1.mergeIn(topic2);
        assertEquals(2, topic1.getSourceLocators().size());
        assertTrue(topic1.getSourceLocators().contains(iid1));
        assertTrue(topic1.getSourceLocators().contains(iid2));
    }

    /**
     * Tests if merging detects duplicates and that the reifier is kept.
     */
    public void testDuplicateDetectionReifier() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        TopicName name1 = topic1.createTopicName("tinyTiM", null, null);
        TopicName name2 = topic2.createTopicName("tinyTiM", null, null);
        assertEquals(3, _tm.getTopics().size());
        ((IReifiable) name1).setReifier(reifier);
        assertEquals(reifier, name1.getReifier());
        assertEquals(1, topic1.getTopicNames().size());
        assertTrue(topic1.getTopicNames().contains(name1));
        assertEquals(1, topic2.getTopicNames().size());
        assertTrue(topic2.getTopicNames().contains(name2));
        topic1.mergeIn(topic2);
        assertEquals(2, _tm.getTopics().size());
        assertEquals(1, topic1.getTopicNames().size());
        TopicName name = (TopicName) topic1.getTopicNames().iterator().next();
        assertEquals(reifier, name.getReifier());
    }

    /**
     * Tests if merging detects duplicates and merges the reifiers of the
     * duplicates.
     */
    public void testDuplicateDetectionReifierMerge() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Topic reifier1 = _tm.createTopic();
        Topic reifier2 = _tm.createTopic();
        TopicName name1 = topic1.createTopicName("tinyTiM", null, null);
        TopicName name2 = topic2.createTopicName("tinyTiM", null, null);
        assertEquals(4, _tm.getTopics().size());
        ((IReifiable) name1).setReifier(reifier1);
        ((IReifiable) name2).setReifier(reifier2);
        assertEquals(reifier1, name1.getReifier());
        assertEquals(reifier2, name2.getReifier());
        assertEquals(1, topic1.getTopicNames().size());
        assertTrue(topic1.getTopicNames().contains(name1));
        assertEquals(1, topic2.getTopicNames().size());
        assertTrue(topic2.getTopicNames().contains(name2));
        topic1.mergeIn(topic2);
        assertEquals(2, _tm.getTopics().size());
        assertEquals(1, topic1.getTopicNames().size());
        TopicName name = (TopicName) topic1.getTopicNames().iterator().next();
        Topic reifier = null;
        for (Topic topic: _tm.getTopics()) {
            if (!topic.equals(topic1)) {
                reifier = topic;
                break;
            }
        }
        assertEquals(reifier, name.getReifier());
    }

    /**
     * Tests if merging detects duplicate associations.
     */
    public void testDuplicateSuppressionAssociation() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Topic roleType = _tm.createTopic();
        Association assoc1 = _tm.createAssociation();
        Association assoc2 = _tm.createAssociation();
        AssociationRole role1 = assoc1.createAssociationRole(topic1, roleType);
        AssociationRole role2 = assoc2.createAssociationRole(topic2, roleType);
        assertEquals(3, _tm.getTopics().size());
        assertEquals(2, _tm.getAssociations().size());
        assertTrue(topic1.getRolesPlayed().contains(role1));
        assertTrue(topic2.getRolesPlayed().contains(role2));
        assertEquals(1, topic1.getRolesPlayed().size());
        assertEquals(1, topic2.getRolesPlayed().size());
        topic1.mergeIn(topic2);
        assertEquals(2, _tm.getTopics().size());
        assertEquals(1, _tm.getAssociations().size());
        AssociationRole role = (AssociationRole) topic1.getRolesPlayed().iterator().next();
        assertEquals(roleType, role.getType());
    }

    /**
     * Tests if merging detects duplicate names.
     */
    public void testDuplicateSuppressionName() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        TopicName name1 = topic1.createTopicName("tinyTiM", null, null);
        TopicName name2 = topic2.createTopicName("tinyTiM", null, null);
        TopicName name3 = topic2.createTopicName("tiny Topic Maps engine", null, null);
        assertEquals(1, topic1.getTopicNames().size());
        assertTrue(topic1.getTopicNames().contains(name1));
        assertEquals(2, topic2.getTopicNames().size());
        assertTrue(topic2.getTopicNames().contains(name2));
        assertTrue(topic2.getTopicNames().contains(name3));
        topic1.mergeIn(topic2);
        assertEquals(2, topic1.getTopicNames().size());
    }

    /**
     * Tests if merging detects duplicate names and moves the variants.
     */
    public void testDuplicateSuppressionName2() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        TopicName name1 = topic1.createTopicName("tinyTiM", null, null);
        TopicName name2 = topic2.createTopicName("tinyTiM", null, null);
        Variant var = name2.createVariant("tiny", null);
        assertEquals(1, topic1.getTopicNames().size());
        assertTrue(topic1.getTopicNames().contains(name1));
        assertEquals(0, name1.getVariants().size()); 
        assertEquals(1, topic2.getTopicNames().size());
        assertTrue(topic2.getTopicNames().contains(name2));
        assertEquals(1, name2.getVariants().size());
        assertTrue(name2.getVariants().contains(var));
        topic1.mergeIn(topic2);
        assertEquals(1, topic1.getTopicNames().size());
        TopicName tmpName = (TopicName) topic1.getTopicNames().iterator().next();
        assertEquals(1, tmpName.getVariants().size());
        Variant tmpVar = (Variant) tmpName.getVariants().iterator().next();
        assertEquals("tiny", tmpVar.getValue());
    }

    /**
     * Tests if merging detects duplicate names and sets the item 
     * identifier to the union of both names.
     */
    public void testDuplicateSuppressionNameMoveItemIdentifiers() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator iid1 = _tm.createLocator("http://example.org/iid-1");
        Locator iid2 = _tm.createLocator("http://example.org/iid-2");
        TopicName name1 = topic1.createTopicName("tinyTiM", null, null);
        TopicName name2 = topic2.createTopicName("tinyTiM", null, null);
        name1.addSourceLocator(iid1);
        name2.addSourceLocator(iid2);
        assertTrue(name1.getSourceLocators().contains(iid1));
        assertTrue(name2.getSourceLocators().contains(iid2));
        assertEquals(1, topic1.getTopicNames().size());
        assertTrue(topic1.getTopicNames().contains(name1));
        assertEquals(1, topic2.getTopicNames().size());
        assertTrue(topic2.getTopicNames().contains(name2));
        topic1.mergeIn(topic2);
        assertEquals(1, topic1.getTopicNames().size());
        TopicName name = (TopicName) topic1.getTopicNames().iterator().next();
        assertEquals(2, name.getSourceLocators().size());
        assertTrue(name.getSourceLocators().contains(iid1));
        assertTrue(name.getSourceLocators().contains(iid2));
        assertEquals("tinyTiM", name.getValue());
    }

    /**
     * Tests if merging detects duplicate occurrences.
     */
    public void testDuplicateSuppressionOccurrence() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Occurrence occ1 = topic1.createOccurrence("tinyTiM", null, null);
        Occurrence occ2 = topic2.createOccurrence("tinyTiM", null, null);
        Occurrence occ3 = topic2.createOccurrence("tiny Topic Maps engine", null, null);
        assertEquals(1, topic1.getOccurrences().size());
        assertTrue(topic1.getOccurrences().contains(occ1));
        assertEquals(2, topic2.getOccurrences().size());
        assertTrue(topic2.getOccurrences().contains(occ2));
        assertTrue(topic2.getOccurrences().contains(occ3));
        topic1.mergeIn(topic2);
        assertEquals(2, topic1.getOccurrences().size());
    }

    /**
     * Tests if merging detects duplicate occurrences and sets the item 
     * identifier to the union of both occurrences.
     */
    public void testDuplicateSuppressionOccurrenceMoveItemIdentifiers() {
        Topic topic1 = _tm.createTopic();
        Topic topic2 = _tm.createTopic();
        Locator iid1 = _tm.createLocator("http://example.org/iid-1");
        Locator iid2 = _tm.createLocator("http://example.org/iid-2");
        Occurrence occ1 = topic1.createOccurrence("tinyTiM", null, null);
        occ1.addSourceLocator(iid1);
        assertTrue(occ1.getSourceLocators().contains(iid1));
        Occurrence occ2 = topic2.createOccurrence("tinyTiM", null, null);
        occ2.addSourceLocator(iid2);
        assertTrue(occ2.getSourceLocators().contains(iid2));
        assertEquals(1, topic1.getOccurrences().size());
        assertTrue(topic1.getOccurrences().contains(occ1));
        assertEquals(1, topic2.getOccurrences().size());
        assertTrue(topic2.getOccurrences().contains(occ2));
        topic1.mergeIn(topic2);
        assertEquals(1, topic1.getOccurrences().size());
        Occurrence occ = (Occurrence) topic1.getOccurrences().iterator().next();
        assertEquals(2, occ.getSourceLocators().size());
        assertTrue(occ.getSourceLocators().contains(iid1));
        assertTrue(occ.getSourceLocators().contains(iid2));
        assertEquals("tinyTiM", occ.getValue());
    }

}
