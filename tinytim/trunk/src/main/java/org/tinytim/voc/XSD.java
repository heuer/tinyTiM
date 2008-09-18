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

import org.tmapi.core.Locator;

/**
 * Provides PSIs for the XML Schema Datatypes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XSD extends Vocabulary {

    private XSD() {
        // noop.
    }
    
    private static final String _BASE = "http://www.w3.org/2001/XMLSchema#";
    
    public final static Locator STRING = _createLocator(_BASE + "string");

    public final static Locator ANY_URI = _createLocator(_BASE + "anyURI");

    public final static Locator DECIMAL = _createLocator(_BASE + "decimal");
    
    public final static Locator INTEGER = _createLocator(_BASE + "integer");

    public final static Locator INT = _createLocator(_BASE + "int");

    public final static Locator FLOAT = _createLocator(_BASE + "float");

    public final static Locator LONG = _createLocator(_BASE + "long");

    public static final Locator BOOLEAN = _createLocator(_BASE + "boolean");

}
