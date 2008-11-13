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
 * Common, abstract superclass for {@link TopicMapWriter} implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapWriter implements TopicMapWriter {

    protected final String _baseIRI;

    protected AbstractTopicMapWriter(final String baseIRI) {
        _baseIRI = baseIRI;
    }

    /**
     * Returns an identifier for the topic.
     * <p>
     * The algorithm tries to avoid to use the internal identifier which may
     * cause yet another item identifier. If the topic has an item identifier
     * which starts with the specified IRI provided in the constructor, the 
     * algorithm tries to use the fragment identifier.
     * </p>
     *
     * @param topic The topic to return an identifier for.
     * @return An identifier, never <tt>null</tt>.
     */
    protected String _getId(final Topic topic) {
        String id = null;
        for (Locator loc: topic.getItemIdentifiers()) {
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
        return id != null ? id : "id-" + topic.getId();
    }

}
