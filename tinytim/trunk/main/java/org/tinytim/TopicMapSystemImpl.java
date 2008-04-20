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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;

/**
 * {@link org.tmapi.core.TopicMapSystem} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class TopicMapSystemImpl implements TopicMapSystem {

    private Map<Locator, TopicMap> _topicMaps;
    private Properties _properties;
    private Map<String, Boolean> _features;
    private ICollectionFactory _collectionFactory;


    TopicMapSystemImpl(ICollectionFactory collFactory, Map<String, Boolean> features, Properties properties) {
        _collectionFactory = collFactory;
        _features = features;
        _properties = properties;
        _topicMaps = collFactory.<Locator, TopicMap>createMap();
    }

    /**
     * Returns the collection factory.
     *
     * @return The collection factory.
     */
    ICollectionFactory getCollectionFactory() {
        return _collectionFactory;
    }

    /**
     * Removes a topic map from this system.
     *
     * @param tm The topic map to remove.
     */
    void removeTopicMap(TopicMap tm) {
        _topicMaps.remove(tm.getBaseLocator());
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String)
     */
    public TopicMap createTopicMap(String baseLocator) throws TopicMapExistsException {
        return _createTopicMap(new IRI(baseLocator));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String, java.lang.String)
     */
    public TopicMap createTopicMap(String reference, String notation)
            throws TopicMapExistsException {
        assert "URI".equals(notation);
        return _createTopicMap(new IRI(reference));
    }

    /**
     * Creates a topic map with the specified <code>locator</code>.
     *
     * @param locator The locator which is used to address the topic map.
     * @return A newly created topic map instance.
     * @throws TopicMapExistsException If a topic map with the specified <code>locator</code>
     *                                  exists.
     */
    private TopicMap _createTopicMap(Locator locator) throws TopicMapExistsException {
        if (_topicMaps.containsKey(locator)) {
            throw new TopicMapExistsException("A topic map with the IRI + '" + locator.getReference() + "' exists in the system");
        }
        TopicMap tm = new TopicMapImpl(this, locator);
        _topicMaps.put(locator, tm);
        return tm;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getBaseLocators()
     */
    public Set<Locator> getBaseLocators() {
        //FIXME: For some reason the TMAPI tests assume that this method returns a copy
        Set<Locator> locs = _topicMaps.keySet();
        Set<Locator> locators = _collectionFactory.createSet(locs.size());
        locators.addAll(locs);
        return locators;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getFeature(java.lang.String)
     */
    public boolean getFeature(String featureName) throws FeatureNotRecognizedException {
        final Boolean supported = _features.get(featureName);
        if (supported == null) {
            TopicMapSystemFactoryImpl.reportFeatureNotRecognized(featureName);
        }
        return supported;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getProperty(java.lang.String)
     */
    public String getProperty(String propertyName) {
        return _properties.getProperty(propertyName);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String)
     */
    public TopicMap getTopicMap(String reference) {
        return getTopicMap(new IRI(reference));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getTopicMap(org.tmapi.core.Locator)
     */
    public TopicMap getTopicMap(Locator iri) {
        return _topicMaps.get(iri);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String, java.lang.String)
     */
    public TopicMap getTopicMap(String reference, String notation) {
        assert "URI".equals(notation);
        return getTopicMap(new IRI(reference));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem#close()
     */
    public void close() {
        _features = null;
        _properties = null;
        _topicMaps = null;
        _collectionFactory = null;
    }

}
