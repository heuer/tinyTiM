/*
 * This is tinyTiM, a tiny Topic Maps engine.
 *
 * Copyright (C) 2008 Lars Heuer (heuer[at]semagia.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
package org.tinytim;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * {@link org.tmapi.core.TopicMapSystemFactory} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class TopicMapSystemFactoryImpl extends TopicMapSystemFactory {

    private static final String _COLL_FACTORY_JAVA = "org.tinytim.JavaCollectionFactory";
    private static final String _COLL_FACTORY_TROVE = "org.tinytim.TroveCollectionFactory";
    private static final FeatureInfo[] _FEATURES = new FeatureInfo[] {
                    // Feature IRI, default value, fixed?
        new FeatureInfo(TMAPIFeature.NOTATION_URI, true, true),
        new FeatureInfo(TMAPIFeature.XTM_1_0, false, true),
        new FeatureInfo(TMAPIFeature.XTM_1_1, true, true),
        new FeatureInfo(TMAPIFeature.AUTOMERGE, false, true),
        new FeatureInfo(TMAPIFeature.TNC, false, true),
        new FeatureInfo(TMAPIFeature.READ_ONLY, false, true)
    };

    private Properties _properties;
    private Map<String, Boolean> _features;

    public TopicMapSystemFactoryImpl() {
        _properties = new Properties();
        _features = new HashMap<String, Boolean>(_FEATURES.length);
        for (FeatureInfo feature: _FEATURES) {
            _features.put(feature.name, feature.defaultValue);
        }
        _properties.setProperty(Property.COLLECTION_FACTORY, _COLL_FACTORY_JAVA);
        try {
            // Probe if Trove is available.
            Class.forName("gnu.trove.THashSet");
            _properties.setProperty(Property.COLLECTION_FACTORY, _COLL_FACTORY_TROVE);
        }
        catch (Exception ex) {
            // noop.
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#newTopicMapSystem()
     */
    @Override
    public TopicMapSystem newTopicMapSystem() throws TMAPIException {
        return new TopicMapSystemImpl(_createCollectionFactory(), new HashMap<String, Boolean>(_features), new Properties(_properties));
    }

    /**
     * Creates a collection factory based according to the 
     * {@link Property#COLLECTION_FACTORY} value. If the collection factory
     * is not available, a default collection factory implementation is returned.
     *
     * @return A collection factory.
     */
    private ICollectionFactory _createCollectionFactory() {
        String className = _properties.getProperty(Property.COLLECTION_FACTORY, _COLL_FACTORY_JAVA);
        try {
            return (ICollectionFactory) Class.forName(className).newInstance();
        }
        catch (Exception ex) {
            // Irgendwas geht immer ;)
            return new JavaCollectionFactory();
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
        return supported.booleanValue();
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
        for (FeatureInfo feature_: _FEATURES) {
            if (feature_.name.equals(featureName)) {
                feature = feature_;
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
    public String getProperty(String propertyName) {
        return _properties.getProperty(propertyName);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties properties) {
        _properties = new Properties(properties);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystemFactory#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String propertyName, String value) {
        _properties.setProperty(propertyName, value);
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
        String name;
        boolean defaultValue;
        boolean fixed;

        FeatureInfo(String name, boolean defaultValue, boolean fixed) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.fixed = fixed;
        }
    }

}
