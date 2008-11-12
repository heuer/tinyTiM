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
package org.tinytim.mio;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class AbstractXTMWriter extends AbstractTopicMapWriter {

    protected final Attributes _EMPTY_ATTRS = XMLWriter.EMPTY_ATTRS;
    
    protected AttributesImpl _attrs;
    protected XMLWriter _out;
    /**
     * 
     *
     * @param baseIRI
     */
    public AbstractXTMWriter(String baseIRI) {
        super(baseIRI);
    }

}
