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

import java.util.Collection;
import java.util.Collections;

import org.tinytim.TinyTimTestCase;
import org.tmapi.core.Association;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;

/**
 * Tests against {@link IScopedIndex}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestScopedIndex extends TinyTimTestCase {

    private IScopedIndex _scopedIdx;

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _scopedIdx = _tm.getIndexManager().getScopedIndex();
    }

    /* (non-Javadoc)
     * @see org.tinytim.TinyTimTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _scopedIdx = null;
    }

    private void _updateIndex() {
        if (!_scopedIdx.isAutoUpdated()) {
            _scopedIdx.reindex();
        }
    }

    public void testAssociation() throws Exception {
        Topic theme = _tm.createTopic();
        _updateIndex();
        assertTrue(_scopedIdx.getAssociationsByTheme(null).isEmpty());
        assertTrue(_scopedIdx.getAssociationsByTheme(theme).isEmpty());
        assertTrue(_scopedIdx.getAssociationThemes().isEmpty());
        Association scoped = _tm.createAssociation();
        assertEquals(0, scoped.getScope().size());
        _updateIndex();
        assertEquals(1, _scopedIdx.getAssociationsByTheme(null).size());
        assertTrue(_scopedIdx.getAssociationsByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getAssociationThemes().contains(theme));
        scoped.addScopingTopic(theme);
        _updateIndex();
        assertEquals(0, _scopedIdx.getAssociationsByTheme(null).size());
        assertFalse(_scopedIdx.getAssociationsByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getAssociationThemes().isEmpty());
        assertEquals(1, _scopedIdx.getAssociationThemes().size());
        assertTrue(_scopedIdx.getAssociationsByTheme(theme).contains(scoped));
        assertTrue(_scopedIdx.getAssociationThemes().contains(theme));
        scoped.remove();
        _updateIndex();
        assertEquals(0, _scopedIdx.getAssociationsByTheme(null).size());
        assertFalse(_scopedIdx.getAssociationsByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getAssociationThemes().contains(theme));
    }

    public void testOccurrence() throws Exception {
        Topic theme = _tm.createTopic();
        _updateIndex();
        assertTrue(_scopedIdx.getOccurrencesByTheme(null).isEmpty());
        assertTrue(_scopedIdx.getOccurrencesByTheme(theme).isEmpty());
        assertTrue(_scopedIdx.getOccurrenceThemes().isEmpty());
        Occurrence scoped = _tm.createTopic().createOccurrence("tinyTiM", null, null);
        assertEquals(0, scoped.getScope().size());
        _updateIndex();
        assertEquals(1, _scopedIdx.getOccurrencesByTheme(null).size());
        assertTrue(_scopedIdx.getOccurrencesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getOccurrenceThemes().contains(theme));
        scoped.addScopingTopic(theme);
        _updateIndex();
        assertEquals(0, _scopedIdx.getOccurrencesByTheme(null).size());
        assertFalse(_scopedIdx.getOccurrencesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getOccurrenceThemes().isEmpty());
        assertEquals(1, _scopedIdx.getOccurrenceThemes().size());
        assertTrue(_scopedIdx.getOccurrencesByTheme(theme).contains(scoped));
        assertTrue(_scopedIdx.getOccurrenceThemes().contains(theme));
        scoped.remove();
        _updateIndex();
        assertEquals(0, _scopedIdx.getOccurrencesByTheme(null).size());
        assertFalse(_scopedIdx.getOccurrencesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getOccurrenceThemes().contains(theme));
    }

    public void testName() throws Exception {
        Topic theme = _tm.createTopic();
        _updateIndex();
        assertTrue(_scopedIdx.getNamesByTheme(null).isEmpty());
        assertTrue(_scopedIdx.getNamesByTheme(theme).isEmpty());
        assertTrue(_scopedIdx.getNameThemes().isEmpty());
        TopicName scoped = _tm.createTopic().createTopicName("tinyTiM", null, null);
        assertEquals(0, scoped.getScope().size());
        _updateIndex();
        assertEquals(1, _scopedIdx.getNamesByTheme(null).size());
        assertTrue(_scopedIdx.getNamesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getNameThemes().contains(theme));
        scoped.addScopingTopic(theme);
        _updateIndex();
        assertEquals(0, _scopedIdx.getNamesByTheme(null).size());
        assertFalse(_scopedIdx.getNamesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getNameThemes().isEmpty());
        assertEquals(1, _scopedIdx.getNameThemes().size());
        assertTrue(_scopedIdx.getNamesByTheme(theme).contains(scoped));
        assertTrue(_scopedIdx.getNameThemes().contains(theme));
        scoped.remove();
        _updateIndex();
        assertEquals(0, _scopedIdx.getNamesByTheme(null).size());
        assertFalse(_scopedIdx.getNamesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getNameThemes().contains(theme));
    }

    public void testName2() throws Exception {
        Topic theme = _tm.createTopic();
        _updateIndex();
        assertTrue(_scopedIdx.getNamesByTheme(null).isEmpty());
        assertTrue(_scopedIdx.getNamesByTheme(theme).isEmpty());
        assertTrue(_scopedIdx.getNameThemes().isEmpty());
        Collection<Topic> scope = Collections.singleton(theme);
        TopicName scoped = _tm.createTopic().createTopicName("tinyTiM", null, scope);
        assertEquals(1, scoped.getScope().size());
        _updateIndex();
        assertEquals(0, _scopedIdx.getNamesByTheme(null).size());
        assertFalse(_scopedIdx.getNamesByTheme(null).contains(scoped));
        assertFalse(_scopedIdx.getNameThemes().isEmpty());
        assertEquals(1, _scopedIdx.getNameThemes().size());
        assertTrue(_scopedIdx.getNamesByTheme(theme).contains(scoped));
        assertTrue(_scopedIdx.getNameThemes().contains(theme));
        scoped.remove();
        _updateIndex();
        assertEquals(0, _scopedIdx.getNamesByTheme(null).size());
        assertFalse(_scopedIdx.getNamesByTheme(null).contains(scoped));
        assertEquals(0, _scopedIdx.getNamesByTheme(theme).size());
        assertFalse(_scopedIdx.getNameThemes().contains(theme));
    }

}
