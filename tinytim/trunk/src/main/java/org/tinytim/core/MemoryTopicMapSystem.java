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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;

/**
 * {@link org.tmapi.core.TopicMapSystem} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class MemoryTopicMapSystem extends AbstractTopicMapSystem {

    private final Map<Locator, TopicMap> _topicMaps;

    MemoryTopicMapSystem(Map<String, Boolean> features, Map<String, Object> properties) throws TMAPIException {
        super(features, properties);
        _topicMaps = CollectionFactory.createIdentityMap(IConstant.SYSTEM_TM_SIZE);
    }

    /**
     * Removes a topic map from this system.
     *
     * @param tm The topic map to remove.
     */
    void removeTopicMap(MemoryTopicMap tm) {
        _topicMaps.remove(tm.getLocator());
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#createTopicMap(org.tmapi.core.Locator)
     */
    public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
        if (_topicMaps.containsKey(locator)) {
            throw new TopicMapExistsException("A topic map with the IRI + '" + locator.getReference() + "' exists in the system");
        }
        TopicMap tm = new MemoryTopicMap(this, locator);
        _topicMaps.put(locator, tm);
        return tm;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getLocators()
     */
    public Set<Locator> getLocators() {
        return Collections.unmodifiableSet(_topicMaps.keySet());
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getTopicMap(org.tmapi.core.Locator)
     */
    public TopicMap getTopicMap(Locator iri) {
        return _topicMaps.get(iri);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#close()
     */
    public void close() {
        super.close();
        _topicMaps.clear();
    }

}
