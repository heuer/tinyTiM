/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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

import java.util.Collection;

import org.tinytim.core.TinyTimTestCase;
import org.tinytim.voc.TMDM;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;

/**
 * Tests against the {@link TypeInstanceUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTypeInstanceUtils extends TinyTimTestCase {

    private void _testTypedInstanceOf(final Typed typed) {
        assertNotNull(typed.getType());
        final Topic origType = typed.getType();
        final Topic newType = createTopic();
        assertTrue(TypeInstanceUtils.isInstanceOf(typed, origType));
        assertFalse(TypeInstanceUtils.isInstanceOf(typed, newType));
        typed.setType(newType);
        assertFalse(TypeInstanceUtils.isInstanceOf(typed, origType));
        assertTrue(TypeInstanceUtils.isInstanceOf(typed, newType));
        final Topic supertype = createTopic();
        assertFalse(TypeInstanceUtils.isInstanceOf(typed, supertype));
        _makeSupertypeSubtype(newType, supertype);
        assertTrue(TypeInstanceUtils.isInstanceOf(typed, supertype));
        final Topic supersupertype = createTopic();
        assertFalse(TypeInstanceUtils.isInstanceOf(typed, supersupertype));
        _makeSupertypeSubtype(supertype, supersupertype);
        assertTrue(TypeInstanceUtils.isInstanceOf(typed, supersupertype));
    }

    private void _makeSupertypeSubtype(Topic subtype, Topic supertype) {
        Association assoc = _tm.createAssociation(_tm.createTopicBySubjectIdentifier(TMDM.SUPERTYPE_SUBTYPE));
        assoc.createRole(_tm.createTopicBySubjectIdentifier(TMDM.SUPERTYPE), supertype);
        assoc.createRole(_tm.createTopicBySubjectIdentifier(TMDM.SUBTYPE), subtype);
    }

    public void testAssociation() {
        _testTypedInstanceOf(createAssociation());
    }

    public void testRole() {
        _testTypedInstanceOf(createRole());
    }

    public void testOccurrence() {
        _testTypedInstanceOf(createOccurrence());
    }

    public void testName() {
        _testTypedInstanceOf(createName());
    }

    /**
     * Tests if a topic is an instance of itself.
     */
    public void testIsInstanceOfSelf() {
        final Topic topic = createTopic();
        assertFalse(TypeInstanceUtils.isInstanceOf(topic, topic));
    }

    /**
     * Tests if a topic is an instance of the types returned by 
     * {@link Topic#getTypes()}.
     */
    public void testIsInstanceOfTypes() {
        final Topic topic = createTopic();
        final Topic type = createTopic();
        topic.addType(type);
        assertTrue(topic.getTypes().contains(type));
        assertTrue(TypeInstanceUtils.isInstanceOf(topic, type));
    }

    public void testTopicIsInstanceOf() {
        final Topic topic = createTopic();
        final Topic type1 = createTopic();
        final Topic type2 = createTopic();
        topic.addType(type1);
        assertTrue(topic.getTypes().contains(type1));
        assertFalse(topic.getTypes().contains(type2));
        assertTrue(TypeInstanceUtils.isInstanceOf(topic, type1));
        assertFalse(TypeInstanceUtils.isInstanceOf(topic, type2));
        _makeSupertypeSubtype(type1, type2);
        assertTrue(TypeInstanceUtils.isInstanceOf(topic, type2));
        final Topic type3 = createTopic();
        assertFalse(TypeInstanceUtils.isInstanceOf(topic, type3));
        _makeSupertypeSubtype(type2, type3);
        assertTrue(TypeInstanceUtils.isInstanceOf(topic, type3));
    }

    public void testGetSupertypesSubtypes() {
        final Topic sub = createTopic();
        final Topic super1 = createTopic();
        final Topic super2 = createTopic();
        Collection<Topic> supertypes = TypeInstanceUtils.getSupertypes(sub);
        Collection<Topic> subtypes1 = TypeInstanceUtils.getSubtypes(super1);
        Collection<Topic> subtypes2 = TypeInstanceUtils.getSubtypes(super2);
        assertEquals(0, supertypes.size());
        assertEquals(0, subtypes1.size());
        assertEquals(0, subtypes2.size());
        // sub ako super1
        _makeSupertypeSubtype(sub, super1);
        supertypes = TypeInstanceUtils.getSupertypes(sub);
        subtypes1 = TypeInstanceUtils.getSubtypes(super1);
        subtypes2 = TypeInstanceUtils.getSubtypes(super2);
        assertEquals(1, supertypes.size());
        assertTrue(supertypes.contains(super1));
        assertEquals(1, subtypes1.size());
        assertTrue(subtypes1.contains(sub));
        assertEquals(0, subtypes2.size());
        // super1 ako super2
        _makeSupertypeSubtype(super1, super2);
        supertypes = TypeInstanceUtils.getSupertypes(sub);
        subtypes1 = TypeInstanceUtils.getSubtypes(super1);
        subtypes2 = TypeInstanceUtils.getSubtypes(super2);
        assertEquals(2, supertypes.size());
        assertTrue(supertypes.contains(super1));
        assertTrue(supertypes.contains(super2));
        assertEquals(1, subtypes1.size());
        assertTrue(subtypes1.contains(sub));
        assertEquals(2, subtypes2.size());
        assertTrue(subtypes2.contains(sub));
        assertTrue(subtypes2.contains(super1));
    }

}
