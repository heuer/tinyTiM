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
package org.tinytim.index;

import org.tinytim.TinyTimTestCase;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;

/**
 * Tests against {@link ITypeInstanceIndex}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTypeInstanceIndex extends TinyTimTestCase {

    private ITypeInstanceIndex _typeInstanceIdx;

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _typeInstanceIdx = _tm.getIndexManager().getTypeInstanceIndex();
    }

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _typeInstanceIdx = null;
    }

    private void _updateIndex() {
        if (!_typeInstanceIdx.isAutoUpdated()) {
            _typeInstanceIdx.reindex();
        }
    }

    public void testTopic() throws Exception {
        _updateIndex();
        assertTrue(_typeInstanceIdx.getTopics(null).isEmpty());
        assertTrue(_typeInstanceIdx.getTopicTypes().isEmpty());
        Topic topic = _tm.createTopic();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getTopicTypes().isEmpty());
        assertEquals(1, _typeInstanceIdx.getTopics(null).size());
        assertTrue(_typeInstanceIdx.getTopics(null).contains(topic));
        Topic type1 = _tm.createTopic();
        Topic type2 = _tm.createTopic();
        assertTrue(_typeInstanceIdx.getTopicTypes().isEmpty());
        assertEquals(3, _typeInstanceIdx.getTopics(null).size());
        assertTrue(_typeInstanceIdx.getTopics(null).contains(topic));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type1));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type2));
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).isEmpty());
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, true).isEmpty());
        // Topic with one type
        topic.addType(type1);
        assertEquals(1, _typeInstanceIdx.getTopicTypes().size());
        assertTrue(_typeInstanceIdx.getTopicTypes().contains(type1));
        assertEquals(2, _typeInstanceIdx.getTopics(null).size());
        assertFalse(_typeInstanceIdx.getTopics(null).contains(topic));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type1));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type2));
        assertEquals(1, _typeInstanceIdx.getTopics(type1).size());
        assertTrue(_typeInstanceIdx.getTopics(type1).contains(topic));
        assertEquals(1, _typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).size());
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).contains(topic));
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, true).isEmpty());
        // Topic with two types
        topic.addType(type2);
        assertEquals(2, _typeInstanceIdx.getTopicTypes().size());
        assertTrue(_typeInstanceIdx.getTopicTypes().contains(type1));
        assertTrue(_typeInstanceIdx.getTopicTypes().contains(type2));
        assertEquals(2, _typeInstanceIdx.getTopics(null).size());
        assertFalse(_typeInstanceIdx.getTopics(null).contains(topic));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type1));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type2));
        assertEquals(1, _typeInstanceIdx.getTopics(type1).size());
        assertTrue(_typeInstanceIdx.getTopics(type1).contains(topic));
        assertEquals(1, _typeInstanceIdx.getTopics(type2).size());
        assertTrue(_typeInstanceIdx.getTopics(type2).contains(topic));
        assertEquals(1, _typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).size());
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).contains(topic));
        assertEquals(1, _typeInstanceIdx.getTopics(new Topic[] {type1, type2}, true).size());
        assertTrue(_typeInstanceIdx.getTopics(new Topic[] {type1, type2}, true).contains(topic));
        // Topic removal
        topic.remove();
        assertEquals(0, _typeInstanceIdx.getTopicTypes().size());
        assertEquals(2, _typeInstanceIdx.getTopics(null).size());
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type1));
        assertTrue(_typeInstanceIdx.getTopics(null).contains(type2));
        assertTrue(_typeInstanceIdx.getTopics(type1).isEmpty());
        assertTrue(_typeInstanceIdx.getTopics(type2).isEmpty());
        assertEquals(0, _typeInstanceIdx.getTopics(new Topic[] {type1, type2}, false).size());
        assertEquals(0, _typeInstanceIdx.getTopics(new Topic[] {type1, type2}, true).size());
    }

    public void testAssociation() throws Exception {
        Topic type = _tm.createTopic();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getAssociations(null).isEmpty());
        assertTrue(_typeInstanceIdx.getAssociations(type).isEmpty());
        assertTrue(_typeInstanceIdx.getAssociationTypes().isEmpty());
        Association typed = _tm.createAssociation();
        assertNull(typed.getType());
        _updateIndex();
        assertEquals(1, _typeInstanceIdx.getAssociations(null).size());
        assertTrue(_typeInstanceIdx.getAssociations(null).contains(typed));
        assertFalse(_typeInstanceIdx.getAssociationTypes().contains(type));
        assertTrue(_typeInstanceIdx.getAssociationTypes().isEmpty());
        typed.setType(type);
        _updateIndex();
        assertEquals(0, _typeInstanceIdx.getAssociations(null).size());
        assertFalse(_typeInstanceIdx.getAssociations(null).contains(typed));
        assertFalse(_typeInstanceIdx.getAssociationTypes().isEmpty());
        assertEquals(1, _typeInstanceIdx.getAssociations(type).size());
        assertTrue(_typeInstanceIdx.getAssociations(type).contains(typed));
        assertTrue(_typeInstanceIdx.getAssociationTypes().contains(type));
        typed.setType(null);
        assertNull(typed.getType());
        assertEquals(1, _typeInstanceIdx.getAssociations(null).size());
        assertTrue(_typeInstanceIdx.getAssociations(null).contains(typed));
        assertFalse(_typeInstanceIdx.getAssociationTypes().contains(type));
        assertTrue(_typeInstanceIdx.getAssociationTypes().isEmpty());
        typed.setType(type);
        typed.remove();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getAssociations(null).isEmpty());
        assertTrue(_typeInstanceIdx.getAssociations(type).isEmpty());
        assertTrue(_typeInstanceIdx.getAssociationTypes().isEmpty());
    }

    public void testRole() throws Exception {
        Topic type = _tm.createTopic();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getRoles(null).isEmpty());
        assertTrue(_typeInstanceIdx.getRoles(type).isEmpty());
        assertTrue(_typeInstanceIdx.getRoleTypes().isEmpty());
        Association parent = _tm.createAssociation();
        AssociationRole typed = parent.createAssociationRole(null, null);
        assertNull(typed.getType());
        _updateIndex();
        assertEquals(1, _typeInstanceIdx.getRoles(null).size());
        assertTrue(_typeInstanceIdx.getRoles(null).contains(typed));
        assertFalse(_typeInstanceIdx.getRoleTypes().contains(type));
        typed.setType(type);
        _updateIndex();
        assertEquals(0, _typeInstanceIdx.getRoles(null).size());
        assertFalse(_typeInstanceIdx.getRoles(null).contains(typed));
        assertEquals(1, _typeInstanceIdx.getRoles(type).size());
        assertTrue(_typeInstanceIdx.getRoles(type).contains(typed));
        typed.setType(null);
        assertNull(typed.getType());
        assertEquals(1, _typeInstanceIdx.getRoles(null).size());
        assertTrue(_typeInstanceIdx.getRoles(null).contains(typed));
        assertFalse(_typeInstanceIdx.getRoleTypes().contains(type));
        assertTrue(_typeInstanceIdx.getRoleTypes().isEmpty());
        typed.setType(type);
        typed.remove();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getRoles(null).isEmpty());
        assertTrue(_typeInstanceIdx.getRoles(type).isEmpty());
        // The same test, but the parent is removed
        typed = parent.createAssociationRole(null, null);
        assertNull(typed.getType());
        _updateIndex();
        assertEquals(1, _typeInstanceIdx.getRoles(null).size());
        assertTrue(_typeInstanceIdx.getRoles(null).contains(typed));
        typed.setType(type);
        _updateIndex();
        assertEquals(0, _typeInstanceIdx.getRoles(null).size());
        assertFalse(_typeInstanceIdx.getRoles(null).contains(typed));
        assertFalse(_typeInstanceIdx.getRoleTypes().isEmpty());
        assertEquals(1, _typeInstanceIdx.getRoles(type).size());
        assertTrue(_typeInstanceIdx.getRoles(type).contains(typed));
        assertTrue(_typeInstanceIdx.getRoleTypes().contains(type));
        parent.remove();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getRoles(null).isEmpty());
        assertTrue(_typeInstanceIdx.getRoles(type).isEmpty());
        assertTrue(_typeInstanceIdx.getRoleTypes().isEmpty());
    }

    public void testOccurrence() throws Exception {
        Topic type = _tm.createTopic();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getOccurrences(null).isEmpty());
        assertTrue(_typeInstanceIdx.getOccurrences(type).isEmpty());
        assertTrue(_typeInstanceIdx.getOccurrenceTypes().isEmpty());
        Topic parent = _tm.createTopic();
        Occurrence typed = parent.createOccurrence("tinyTiM", null, null);
        assertNull(typed.getType());
        _updateIndex();
        assertEquals(1, _typeInstanceIdx.getOccurrences(null).size());
        assertTrue(_typeInstanceIdx.getOccurrences(null).contains(typed));
        assertFalse(_typeInstanceIdx.getOccurrenceTypes().contains(type));
        typed.setType(type);
        _updateIndex();
        assertEquals(0, _typeInstanceIdx.getOccurrences(null).size());
        assertFalse(_typeInstanceIdx.getOccurrences(null).contains(typed));
        assertFalse(_typeInstanceIdx.getOccurrenceTypes().isEmpty());
        assertEquals(1, _typeInstanceIdx.getOccurrences(type).size());
        assertTrue(_typeInstanceIdx.getOccurrences(type).contains(typed));
        assertTrue(_typeInstanceIdx.getOccurrenceTypes().contains(type));
        typed.setType(null);
        assertNull(typed.getType());
        assertEquals(1, _typeInstanceIdx.getOccurrences(null).size());
        assertTrue(_typeInstanceIdx.getOccurrences(null).contains(typed));
        assertFalse(_typeInstanceIdx.getOccurrenceTypes().contains(type));
        assertTrue(_typeInstanceIdx.getOccurrenceTypes().isEmpty());
        typed.setType(type);
        typed.remove();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getOccurrences(null).isEmpty());
        assertTrue(_typeInstanceIdx.getOccurrences(type).isEmpty());
        assertTrue(_typeInstanceIdx.getOccurrenceTypes().isEmpty());
    }

    public void testName() throws Exception {
        Topic type = _tm.createTopic();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getNames(null).isEmpty());
        assertTrue(_typeInstanceIdx.getNames(type).isEmpty());
        assertTrue(_typeInstanceIdx.getNameTypes().isEmpty());
        Topic parent = _tm.createTopic();
        TopicName typed = parent.createTopicName("tinyTiM", null, null);
        assertNull(typed.getType());
        _updateIndex();
        assertEquals(1, _typeInstanceIdx.getNames(null).size());
        assertTrue(_typeInstanceIdx.getNames(null).contains(typed));
        assertFalse(_typeInstanceIdx.getNameTypes().contains(type));
        typed.setType(type);
        _updateIndex();
        assertEquals(0, _typeInstanceIdx.getNames(null).size());
        assertFalse(_typeInstanceIdx.getNames(null).contains(typed));
        assertFalse(_typeInstanceIdx.getNameTypes().isEmpty());
        assertEquals(1, _typeInstanceIdx.getNames(type).size());
        assertTrue(_typeInstanceIdx.getNames(type).contains(typed));
        assertTrue(_typeInstanceIdx.getNameTypes().contains(type));
        typed.setType(null);
        assertNull(typed.getType());
        assertEquals(1, _typeInstanceIdx.getNames(null).size());
        assertTrue(_typeInstanceIdx.getNames(null).contains(typed));
        assertFalse(_typeInstanceIdx.getNameTypes().contains(type));
        assertTrue(_typeInstanceIdx.getNameTypes().isEmpty());
        typed.setType(type);
        typed.remove();
        _updateIndex();
        assertTrue(_typeInstanceIdx.getNames(null).isEmpty());
        assertTrue(_typeInstanceIdx.getNames(type).isEmpty());
        assertTrue(_typeInstanceIdx.getNameTypes().isEmpty());
    }
}
