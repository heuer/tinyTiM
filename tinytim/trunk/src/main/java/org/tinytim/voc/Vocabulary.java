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
package org.tinytim.voc;

import org.tinytim.core.Literal;
import org.tmapi.core.Locator;

/**
 * Base for classes which provide PSIs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class Vocabulary {

    /**
     * Returns a locator with the specified <tt>reference</tt>.
     *
     * @param reference The address of the locator.
     * @return A locator.
     */
    protected final static Locator _createLocator(String reference) {
        return Literal.createIRI(reference);
    }
}
