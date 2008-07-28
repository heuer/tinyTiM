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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tinytim.ICollectionFactory;
import org.tinytim.JavaCollectionFactory;
import org.tinytim.Property;
import org.tinytim.TMAPIFeature;
import org.tinytim.TopicMapSystemImpl;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;

/**
 * Tests against the {@link org.tinytim.TopicMapSystemFactoryImpl}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTopicMapSystemFactoryImpl extends TinyTimTestCase {

    /**
     * Tests the default feature values.
     *
     * @throws Exception
     */
    public void testDefaultFeatureValues() throws Exception {
        assertTrue(_sysFactory.getFeature(TMAPIFeature.NOTATION_URI));
        assertTrue(_sysFactory.getFeature(TMAPIFeature.XTM_1_1));
        assertFalse(_sysFactory.getFeature(TMAPIFeature.XTM_1_0));
        assertFalse(_sysFactory.getFeature(TMAPIFeature.READ_ONLY));
        assertFalse(_sysFactory.getFeature(TMAPIFeature.AUTOMERGE));
        assertFalse(_sysFactory.getFeature(TMAPIFeature.TNC));
    }


    private void _setFeatureToAcceptedValue(String featureName, boolean value) throws Exception {
        try {
            _sysFactory.setFeature(featureName, value);
        }
        catch (FeatureNotSupportedException ex) {
            fail("Unexpected exception while setting '" + featureName + "' to '" + value + "'");
        }
    }

    private void _setFeatureToUnacceptedValue(String featureName, boolean value) throws Exception {
        try {
            _sysFactory.setFeature(featureName, value);
            fail("Expected exception while setting '" + featureName + "' to '" + value + "'");
        }
        catch (FeatureNotSupportedException ex) {
            // noop.
        }
    }

    /**
     * Tests if enabling / disabling of various features delivers the expected 
     * results. 
     *
     * @throws Exception
     */
    public void testSetFeatureValues() throws Exception {
        _setFeatureToAcceptedValue(TMAPIFeature.NOTATION_URI, true);
        _setFeatureToUnacceptedValue(TMAPIFeature.NOTATION_URI, false);
        _setFeatureToAcceptedValue(TMAPIFeature.XTM_1_0, false);
        _setFeatureToUnacceptedValue(TMAPIFeature.XTM_1_0, true);
        _setFeatureToAcceptedValue(TMAPIFeature.XTM_1_1, true);
        _setFeatureToUnacceptedValue(TMAPIFeature.XTM_1_1, false);
        _setFeatureToAcceptedValue(TMAPIFeature.READ_ONLY, false);
        _setFeatureToUnacceptedValue(TMAPIFeature.READ_ONLY, true);
        _setFeatureToAcceptedValue(TMAPIFeature.AUTOMERGE, false);
        _setFeatureToUnacceptedValue(TMAPIFeature.AUTOMERGE, true);
        _setFeatureToAcceptedValue(TMAPIFeature.TNC, false);
        _setFeatureToUnacceptedValue(TMAPIFeature.TNC, true);
    }

    /**
     * Tests if an unknown feature throws the expected exception.
     *
     * @throws Exception
     */
    public void testUnrecognizedFeature() throws Exception {
        try {
            String unknownFeatureName = "http://www.semagia.com/tinyTiM/unknownTMAPIFeature";
            _sysFactory.setFeature(unknownFeatureName, true);
            fail("Expected an exception while setting a unknown feature");
        }
        catch (FeatureNotRecognizedException ex) {
            // noop.
        }
    }

    /**
     * Tests if the collection factory property is set.
     * 
     * @throws Exception 
     */
    public void testCollectionFactoryProperty() throws Exception {
        boolean troveAvailable = false;
        try {
            Class.forName("gnu.trove.THashSet");
            troveAvailable = true;
        }
        catch (Exception ex) {
            // noop.
        }
        if (troveAvailable) {
            assertEquals("org.tinytim.TroveCollectionFactory", _sysFactory.getProperty(Property.COLLECTION_FACTORY));
        }
        else {
            assertEquals("org.tinytim.JavaCollectionFactory", _sysFactory.getProperty(Property.COLLECTION_FACTORY));
            assertTrue(((TopicMapSystemImpl) _sysFactory.newTopicMapSystem()).getCollectionFactory() instanceof JavaCollectionFactory);
        }
    }

    /**
     * Tests if the TopicMapSystemFactory creates automatically a default 
     * CollectionFactory iff the class name in the property is invaild / 
     * not resolvable.
     *
     * @throws Exception
     */
    public void testCollectionFactoryFallback() throws Exception {
        _sysFactory.setProperty(Property.COLLECTION_FACTORY, "a.non.existent.CollectionFactory");
        TopicMapSystemImpl sys = (TopicMapSystemImpl) _sysFactory.newTopicMapSystem();
        assertTrue(sys.getCollectionFactory() instanceof JavaCollectionFactory);
    }

    /**
     * Sets the setting of a custom {@link ICollectionFactory}.
     *
     * @throws Exception
     */
    public void testCustomCollectionFactory() throws Exception {
        _sysFactory.setProperty(Property.COLLECTION_FACTORY, MyCollectionFactory.class.getName());
        TopicMapSystemImpl sys = (TopicMapSystemImpl) _sysFactory.newTopicMapSystem();
        assertTrue(sys.getCollectionFactory() instanceof MyCollectionFactory);
    }

    /**
     * {@link ICollectionFactory} implementation that uses the Java collections. 
     */
    public static final class MyCollectionFactory implements ICollectionFactory {
        public <K, V> Map<K, V> createMap() {
            return new HashMap<K, V>();
        }
        public <K, V> Map<K, V> createMap(int size) {
            return createMap();
        }
        public <E> Set<E> createSet(int size) {
            return createSet();
        }
        public <E> Set<E> createSet() {
            return new HashSet<E>();
        }
    }
}
