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

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapWriter implements TopicMapWriter {

    protected final String _baseIRI;

    protected AbstractTopicMapWriter(final String baseIRI) {
        _baseIRI = baseIRI;
    }

    protected String _getId(Topic tmo) {
        String id = null;
        for (Locator loc: tmo.getItemIdentifiers()) {
            String reference = loc.getReference();
            if (!reference.startsWith(_baseIRI)) {
                continue;
            }
            int fragIdx =  reference.indexOf('#');
            if (fragIdx < 0) {
                continue;
            }
            id = reference.substring(fragIdx+1);
            if (id.startsWith("id")) {
                id = null;
            }
            if (id != null) {
                break;
            }
        }
        return id != null ? id : "id-" + tmo.getId();
    }
}
