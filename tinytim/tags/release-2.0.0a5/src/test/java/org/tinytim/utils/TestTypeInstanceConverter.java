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
 * @version $Rev$ - $Date$
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
