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

import java.util.Arrays;
import java.util.Collections;

import org.tinytim.internal.api.IScope;
import org.tmapi.core.Topic;

/**
 * Tests against {@link Scope}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestScope extends AbstractTinyTimTestCase {

    public void testUnconstrained() {
        assertTrue(Scope.UCS.isUnconstrained());
        assertEquals(0, Scope.UCS.size());
    }

    public void testEquals() {
        final Topic theme1 = createTopic();
        final Topic theme2 = createTopic();
        final IScope scope1 = Scope.create(Arrays.asList(theme1, theme2));
        assertEquals(2, scope1.size());
        final IScope scope2 = Scope.create(Arrays.asList(theme2, theme1));
        assertSame(scope1, scope2);
    }

    public void testCreationUCS() {
        final IScope scope = Scope.create(Collections.<Topic>emptyList());
        assertTrue(scope.isUnconstrained());
        assertSame(Scope.UCS, scope);
    }

}
