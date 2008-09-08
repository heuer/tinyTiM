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
package org.tinytim.utils;

import org.tinytim.core.TinyTimTestCase;
import org.tinytim.voc.TMDM;
import org.tinytim.voc.XTM10;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

/**
 * Tests against the {@link TypeInstanceConverter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTypeInstanceConverter extends TinyTimTestCase {


    public void testTMDMTypeInstanceAssociation() {
        final Topic type = createTopic();
        final Topic instance = createTopic();
        final Association assoc = _tm.createAssociation(_createTMDMTypeInstanceTopic());
        assoc.createRole(_createTMDMTypeTopic(), type);
        assoc.createRole(_createTMDMInstanceTopic(), instance);
        assertEquals(1, _tm.getAssociations().size());
        assertEquals(2, _tm.getAssociations().iterator().next().getRoles().size());
        assertEquals(0, instance.getTypes().size());
        // XTM 1.0 class-instance assocs cannot be found
        TypeInstanceConverter.convertXTMAssociationsToTypes(_tm);
        assertEquals(1, _tm.getAssociations().size());
        assertEquals(2, _tm.getAssociations().iterator().next().getRoles().size());
        assertEquals(0, instance.getTypes().size());
        // Now remove the type-instance assocs.
        TypeInstanceConverter.convertTMDMAssociationsToTypes(_tm);
        assertEquals(0, _tm.getAssociations().size());
        assertEquals(1, instance.getTypes().size());
        assertTrue(instance.getTypes().contains(type));
    }

    public void testXTM10TypeInstanceAssociation() {
        final Topic type = createTopic();
        final Topic instance = createTopic();
        final Association assoc = _tm.createAssociation(_createXTM10TypeInstanceTopic());
        assoc.createRole(_createXTM10TypeTopic(), type);
        assoc.createRole(_createXTM10InstanceTopic(), instance);
        assertEquals(1, _tm.getAssociations().size());
        assertEquals(2, _tm.getAssociations().iterator().next().getRoles().size());
        assertEquals(0, instance.getTypes().size());
        // TMDM type-instance assocs cannot be found
        TypeInstanceConverter.convertTMDMAssociationsToTypes(_tm);
        assertEquals(1, _tm.getAssociations().size());
        assertEquals(2, _tm.getAssociations().iterator().next().getRoles().size());
        assertEquals(0, instance.getTypes().size());
        // Now remove the class-instance assocs.
        TypeInstanceConverter.convertXTMAssociationsToTypes(_tm);
        assertEquals(0, _tm.getAssociations().size());
        assertEquals(1, instance.getTypes().size());
        assertTrue(instance.getTypes().contains(type));
    }

    private Topic _createTMDMTypeInstanceTopic() {
        return _createTopic(TMDM.TYPE_INSTANCE);
    }

    private Topic _createTMDMTypeTopic() {
        return _createTopic(TMDM.TYPE);
    }

    private Topic _createTMDMInstanceTopic() {
        return _createTopic(TMDM.INSTANCE);
    }
    
    private Topic _createXTM10TypeInstanceTopic() {
        return _createTopic(XTM10.CLASS_INSTANCE);
    }

    private Topic _createXTM10TypeTopic() {
        return _createTopic(XTM10.CLASS);
    }

    private Topic _createXTM10InstanceTopic() {
        return _createTopic(XTM10.INSTANCE);
    }

    private Topic _createTopic(final Locator loc) {
        return _tm.createTopicBySubjectIdentifier(loc);
    }
}
