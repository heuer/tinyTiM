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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

/**
 * Tests against {@link IConstruct}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestIConstruct extends TinyTimTestCase {


    private void _testConstruct(Construct construct) {
        IConstruct c = (IConstruct) construct;
        assertEquals(c.isTopicMap(), c instanceof TopicMap);
        assertEquals(c.isTopic(), c instanceof Topic);
        assertEquals(c.isAssociation(), c instanceof Association);
        assertEquals(c.isRole(), c instanceof Role);
        assertEquals(c.isOccurrence(), c instanceof Occurrence);
        assertEquals(c.isName(), c instanceof Name);
        assertEquals(c.isVariant(), c instanceof Variant);
    }

    public void testTopicMap() {
        _testConstruct(_tm);
    }

    public void testTopic() {
        _testConstruct(createTopic());
    }

    public void testAssociation() {
        _testConstruct(createAssociation());
    }

    public void testRole() {
        _testConstruct(createRole());
    }

    public void testOccurrence() {
        _testConstruct(createOccurrence());
    }

    public void testName() {
        _testConstruct(createName());
    }

    public void testVariant() {
        _testConstruct(createVariant());
    }
}
