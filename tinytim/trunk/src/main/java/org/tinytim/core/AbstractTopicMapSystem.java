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

import java.util.Map;

import org.tinytim.core.value.Literal;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapSystem implements TopicMapSystem {

    protected final Map<String, Boolean> _features;
    protected final Map<String, Object> _properties;

    protected AbstractTopicMapSystem(Map<String, Boolean> features, Map<String, Object> properties) throws TMAPIException {
        _features = features;
        _properties = properties;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getFeature(java.lang.String)
     */
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
        final Boolean supported = _features.get(featureName);
        if (supported == null) {
            TopicMapSystemFactoryImpl.reportFeatureNotRecognized(featureName);
        }
        return supported.booleanValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName) {
        return _properties.get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#createLocator(java.lang.String)
     */
    public Locator createLocator(String reference) {
        return Literal.createIRI(reference);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String)
     */
    public TopicMap getTopicMap(String reference) {
        return getTopicMap(createLocator(reference));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String)
     */
    public TopicMap createTopicMap(String reference) throws TopicMapExistsException {
        return createTopicMap(createLocator(reference));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#close()
     */
    public void close() {
        _features.clear();
        _properties.clear();
    }

    abstract void removeTopicMap(MemoryTopicMap tm);

}
