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

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against the {@link org.tinytim.IScoped} interface.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestScoped extends TinyTimTestCase {

    /**
     * Tests if a Topic Maps construct is an instance of IScoped.
     */
    public void testInstanceOf() {
        assertFalse(((TopicMap)_tm) instanceof IScoped);
        Topic topic = _tm.createTopic();
        assertFalse(topic instanceof IScoped);
        Association assoc = _tm.createAssociation();
        assertTrue(assoc instanceof IScoped);
        AssociationRole role = assoc.createAssociationRole(_tm.createTopic(), _tm.createTopic());
        assertFalse(role instanceof IScoped);
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        assertTrue(occ instanceof IScoped);
        TopicName name = topic.createTopicName("tinyTiM", null);
        assertTrue(name instanceof IScoped);
        Variant variant = name.createVariant("tinyTiM", null);
        assertTrue(variant instanceof IScoped);
    }

    /**
     * Tests against an association. 
     */
    public void testAssociation() {
        Association assoc = _tm.createAssociation();
        _testScoped((IScoped) assoc);
    }

    /**
     * Tests against an occurrence.
     */
    public void testOccurrence() {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence("tinyTiM", null, null);
        _testScoped((IScoped) occ);
    }

    /**
     * Tests against a name.
     */
    public void testName() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        _testScoped((IScoped) name);
    }

    /**
     * Tests against a variant.
     */
    public void testVariant() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null, null);
        Variant variant = name.createVariant("tinyTiM", null);
        _testScoped((IScoped) variant);
    }

    /**
     * Tests adding / removing themes.
     *
     * @param scoped The scoped Topic Maps construct to test.
     */
    private void _testScoped(IScoped scoped) {
        //TODO: This may fail in the future for variants
        assertEquals(0, scoped.getScope().size());
        Topic theme1 = _tm.createTopic();
        scoped.addTheme(theme1);
        assertEquals(1, scoped.getScope().size());
        assertTrue(scoped.getScope().contains(theme1));
        Topic theme2 = _tm.createTopic();
        assertFalse(scoped.getScope().contains(theme2));
        scoped.addTheme(theme2);
        assertEquals(2, scoped.getScope().size());
        assertTrue(scoped.getScope().contains(theme1));
        assertTrue(scoped.getScope().contains(theme2));
        scoped.removeTheme(theme2);
        assertEquals(1, scoped.getScope().size());
        assertTrue(scoped.getScope().contains(theme1));
        assertFalse(scoped.getScope().contains(theme2));
        scoped.removeTheme(theme1);
        assertEquals(0, scoped.getScope().size());
    }
}
