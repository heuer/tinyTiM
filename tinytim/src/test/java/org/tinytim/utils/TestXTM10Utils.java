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

import org.tinytim.core.AbstractTinyTimTestCase;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * Tests against the {@link XTM10Utils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestXTM10Utils extends AbstractTinyTimTestCase {

    private void _testConvertReification(Reifiable reifiable) {
        final Locator loc = _tm.createLocator("http://www.semagia.com/test");
        Topic t = _tm.createTopicBySubjectIdentifier(loc);
        reifiable.addItemIdentifier(loc);
        assertNull(reifiable.getReifier());
        assertNull(t.getReified());
        assertEquals(1, t.getSubjectIdentifiers().size());
        assertEquals(loc, t.getSubjectIdentifiers().iterator().next());
        assertEquals(1, reifiable.getItemIdentifiers().size());
        assertEquals(loc, reifiable.getItemIdentifiers().iterator().next());
        XTM10Utils.convertReification(_tm);
        assertEquals(t, reifiable.getReifier());
        assertEquals(reifiable, t.getReified());
        assertEquals(0, t.getSubjectIdentifiers().size());
        assertEquals(0, reifiable.getItemIdentifiers().size());
        reifiable.setReifier(null);
        t.remove();
    }

    public void testConvertReification() {
        _testConvertReification(_tm);
        _testConvertReification(createAssociation());
        _testConvertReification(createRole());
        _testConvertReification(createOccurrence());
        _testConvertReification(createName());
        _testConvertReification(createVariant());
    }

    public void testConvertReificationSkipReified() {
        final Locator loc = _tm.createLocator("http://www.semagia.com/test");
        Topic t = _tm.createTopicBySubjectIdentifier(loc);
        Name reifiedName = createName();
        reifiedName.setReifier(t);
        assertEquals(t, reifiedName.getReifier());
        assertEquals(reifiedName, t.getReified());
        Name name = createName();
        name.addItemIdentifier(loc);
        assertEquals(1, t.getSubjectIdentifiers().size());
        assertEquals(loc, t.getSubjectIdentifiers().iterator().next());
        assertEquals(1, name.getItemIdentifiers().size());
        assertEquals(loc, name.getItemIdentifiers().iterator().next());
        XTM10Utils.convertReification(_tm);
        assertEquals(t, reifiedName.getReifier());
        assertEquals(reifiedName, t.getReified());
        assertEquals(1, t.getSubjectIdentifiers().size());
        assertEquals(loc, t.getSubjectIdentifiers().iterator().next());
        assertEquals(1, name.getItemIdentifiers().size());
        assertEquals(loc, name.getItemIdentifiers().iterator().next());
    }

}
