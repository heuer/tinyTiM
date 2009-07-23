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
package org.tinytim.core;

import org.tinytim.internal.api.IConstructFactory;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.IVariant;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * Tests against {@link IConstructFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestIConstructFactory extends TinyTimTestCase {

    private IConstructFactory _factory;

    /* (non-Javadoc)
     * @see org.tinytim.core.TinyTimTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _factory = _tm.getConstructFactory();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.TinyTimTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _factory = null;
    }

    /**
     * Tests topic creatation.
     */
    public void testTopicCreation() {
        final Topic topic = _factory.createTopic();
        assertNotNull(topic.getId());
        assertTrue(topic.getItemIdentifiers().isEmpty());
        assertTrue(topic.getSubjectIdentifiers().isEmpty());
        assertTrue(topic.getSubjectLocators().isEmpty());
        assertTrue(topic.getTypes().isEmpty());
        assertTrue(topic.getOccurrences().isEmpty());
        assertTrue(topic.getNames().isEmpty());
        assertTrue(topic.getRolesPlayed().isEmpty());
        assertEquals(topic, _tm.getConstructById(topic.getId()));
        assertEquals(_tm, topic.getParent());
        assertEquals(_tm, topic.getTopicMap());
    }

    /**
     * Tests association creatation.
     */
    public void testAssociationCreation() {
        final Association assoc = _factory.createAssociation();
        assertNotNull(assoc.getId());
        assertNull(assoc.getType());
        assertTrue(assoc.getItemIdentifiers().isEmpty());
        assertTrue(assoc.getRoles().isEmpty());
        assertTrue(assoc.getScope().isEmpty());
        assertEquals(assoc, _tm.getConstructById(assoc.getId()));
        assertEquals(_tm, assoc.getParent());
        assertEquals(_tm, assoc.getTopicMap());
    }

    /**
     * Tests role creation.
     */
    public void testRoleCreation() {
        final Association assoc = createAssociation();
        final Role role = _factory.createRole(assoc);
        assertNotNull(role.getId());
        assertNull(role.getType());
        assertNull(role.getPlayer());
        assertTrue(role.getItemIdentifiers().isEmpty());
        assertEquals(role, _tm.getConstructById(role.getId()));
        assertEquals(assoc, role.getParent());
        assertEquals(_tm, assoc.getTopicMap());
    }

    /**
     * Tests occurrence creation.
     */
    public void testOccurrenceCreation() {
        final ITopic topic = (ITopic) createTopic();
        final IOccurrence occ = _factory.createOccurrence(topic);
        assertNotNull(occ.getId());
        assertNull(occ.getType());
        assertTrue(occ.getItemIdentifiers().isEmpty());
        assertNull(occ.getLiteral());
        assertTrue(occ.getScope().isEmpty());
        assertEquals(occ, _tm.getConstructById(occ.getId()));
        assertEquals(topic, occ.getParent());
        assertEquals(_tm, occ.getTopicMap());
    }

    /**
     * Tests name creation.
     */
    public void testNameCreation() {
        final ITopic topic = (ITopic) createTopic();
        final IName name = _factory.createName(topic);
        assertNotNull(name.getId());
        assertNull(name.getType());
        assertTrue(name.getItemIdentifiers().isEmpty());
        assertNull(name.getLiteral());
        assertTrue(name.getScope().isEmpty());
        assertEquals(name, _tm.getConstructById(name.getId()));
        assertEquals(topic, name.getParent());
        assertEquals(_tm, name.getTopicMap());
    }

    /**
     * Tests variant creation.
     */
    public void testVariantCreation() {
        final IName name = (IName) createName();
        final IVariant variant = _factory.createVariant(name);
        assertNotNull(variant.getId());
        assertTrue(variant.getItemIdentifiers().isEmpty());
        assertNull(variant.getLiteral());
        assertTrue(variant.getScope().isEmpty());
        assertEquals(variant, _tm.getConstructById(variant.getId()));
        assertEquals(name, variant.getParent());
        assertEquals(_tm, variant.getTopicMap());
    }

    /**
     * Tests variant creation with a scoped name.
     */
    public void testVariantCreationScope() {
        final IName name = (IName) createName();
        final Topic theme1 = createTopic();
        final Topic theme2 = createTopic();
        name.addTheme(theme1);
        name.addTheme(theme2);
        final IVariant variant = _factory.createVariant(name);
        assertNotNull(variant.getId());
        assertTrue(variant.getItemIdentifiers().isEmpty());
        assertNull(variant.getLiteral());
        assertEquals(2, variant.getScope().size());
        assertTrue(variant.getScope().contains(theme1));
        assertTrue(variant.getScope().contains(theme2));
        assertEquals(variant, _tm.getConstructById(variant.getId()));
        assertEquals(name, variant.getParent());
        assertEquals(_tm, variant.getTopicMap());
    }

}
