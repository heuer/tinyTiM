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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.utils.Feature;
import org.tinytim.utils.Property;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * {@link org.tmapi.core.TopicMapSystemFactory} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TopicMapSystemFactoryImpl extends TopicMapSystemFactory {

    private static final FeatureInfo[] _FEATURES = new FeatureInfo[] {
                    // Feature IRI, default value, fixed?
        new FeatureInfo(Feature.NOTATION_URI, true, true),
        new FeatureInfo(Feature.XTM_1_0, false, true),
        new FeatureInfo(Feature.XTM_1_1, true, true),
        new FeatureInfo(Feature.AUTOMERGE, false, true),
        new FeatureInfo(Feature.TNC, false, true),
        new FeatureInfo(Feature.READ_ONLY, false, true)
    };

    private static final TMSystemInfo[] _SYSTEMS = new TMSystemInfo[] {
        new TMSystemInfo(Property.PERSISTENT, "org.tinytim.core.PersistentTopicMapSystem"),
        new TMSystemInfo(Property.TMSHARE, "org.tinytim.core.TMShareTopicMapSystem"),
    };

    private Map<String, Object> _properties;
    private Map<String, Boolean> _features;

    public TopicMapSystemFactoryImpl() {
        _properties = CollectionFactory.createMap();
        _features = CollectionFactory.createMap(_FEATURES.length);
        for (FeatureInfo feature: _FEATURES) {
            _features.put(feature.name, feature.defaultValue);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#newTopicMapSystem()
     */
    @Override
    public TopicMapSystem newTopicMapSystem() throws TMAPIException {
        String system = (String) _properties.get(Property.SYSTEM);
        if (system != null) {
            String klass = null;
            for (TMSystemInfo info: _SYSTEMS) {
                if (info.name.equals(system)) {
                    klass = info.klass;
                    break;
                }
            }
            if (klass == null) {
                throw new TMAPIException("Unknown TopicMapSystem: '" + system + "'");
            }
            return _newNonDefaultTopicMapSystem(klass);
        }
        return new MemoryTopicMapSystem(CollectionFactory.createMap(_features), CollectionFactory.createMap(_properties));
    }

    /**
     * 
     *
     * @param className
     * @return
     * @throws TMAPIException
     */
    private TopicMapSystem _newNonDefaultTopicMapSystem(final String className) throws TMAPIException {
        try {
            @SuppressWarnings("unchecked")
            Class<TopicMapSystem> klass = (Class<TopicMapSystem>) Class.forName(className);
            Constructor<TopicMapSystem> constructor = klass.getConstructor(Map.class, Map.class);
            return constructor.newInstance(CollectionFactory.createMap(_features), CollectionFactory.createMap(_properties));
        }
        catch (Exception ex) {
            throw new TMAPIException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#getFeature(java.lang.String)
     */
    @Override
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
        final Boolean supported = _features.get(featureName);
        if (supported == null) {
            reportFeatureNotRecognized(featureName);
        }
        return supported;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#hasFeature(java.lang.String)
     */
    @Override
    public boolean hasFeature(String featureName) {
        return _features.containsKey(featureName);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#setFeature(java.lang.String, boolean)
     */
    @Override
    public void setFeature(String featureName, boolean enabled)
            throws FeatureNotSupportedException, FeatureNotRecognizedException {
        if (!_features.containsKey(featureName)) {
            reportFeatureNotRecognized(featureName);
        }
        FeatureInfo feature = null;
        for (FeatureInfo feat: _FEATURES) {
            if (feat.name.equals(featureName)) {
                feature = feat;
                break;
            }
        }
        if (feature.fixed && feature.defaultValue != enabled) {
            throw new FeatureNotSupportedException("The feature '" + featureName + "' cannot be changed.");
        }
        _features.put(featureName, enabled);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String propertyName) {
        return _properties.get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(final String propertyName, Object value) {
        if (value != null) {
            if (Property.TMSHARE.equals(propertyName)) {
                if (!(value instanceof Set)) {
                    value = Collections.singleton(value);
                }
            }
            _properties.put(propertyName, value);
        }
        else {
            _properties.remove(propertyName);
        }
    }

    /**
     * Throws a {@link org.tmapi.core.FeatureNotRecognizedException} with a
     * message.
     *
     * @param featureName The name of the feature which is unknown.
     * @throws FeatureNotRecognizedException Thrown in any case.
     */
    static void reportFeatureNotRecognized(String featureName) throws FeatureNotRecognizedException {
        throw new FeatureNotRecognizedException("The feature '" + featureName + "' is unknown");
    }


    /**
     * Simple structure that holds a feature name, the default value and an
     * indication if the feature is changable.
     */
    private static class FeatureInfo {
        final String name;
        final boolean defaultValue;
        final boolean fixed;

        FeatureInfo(String name, boolean defaultValue, boolean fixed) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.fixed = fixed;
        }
    }

    /**
     * Simple structure that holds a property name (an IRI) and a class name of
     * a TopicMapSystemFactory.
     */
    private static class TMSystemInfo {
        final String name;
        final String klass;

        TMSystemInfo(String name, String klass) {
            this.name = name;
            this.klass = klass;
        }
    }

}
