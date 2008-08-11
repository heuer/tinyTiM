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
package org.tinytim.core;

import org.tinytim.utils.Feature;
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
        assertTrue(_sysFactory.getFeature(Feature.NOTATION_URI));
        assertTrue(_sysFactory.getFeature(Feature.XTM_1_1));
        assertFalse(_sysFactory.getFeature(Feature.XTM_1_0));
        assertFalse(_sysFactory.getFeature(Feature.READ_ONLY));
        assertFalse(_sysFactory.getFeature(Feature.AUTOMERGE));
        assertFalse(_sysFactory.getFeature(Feature.TNC));
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
        _setFeatureToAcceptedValue(Feature.NOTATION_URI, true);
        _setFeatureToUnacceptedValue(Feature.NOTATION_URI, false);
        _setFeatureToAcceptedValue(Feature.XTM_1_0, false);
        _setFeatureToUnacceptedValue(Feature.XTM_1_0, true);
        _setFeatureToAcceptedValue(Feature.XTM_1_1, true);
        _setFeatureToUnacceptedValue(Feature.XTM_1_1, false);
        _setFeatureToAcceptedValue(Feature.READ_ONLY, false);
        _setFeatureToUnacceptedValue(Feature.READ_ONLY, true);
        _setFeatureToAcceptedValue(Feature.AUTOMERGE, false);
        _setFeatureToUnacceptedValue(Feature.AUTOMERGE, true);
        _setFeatureToAcceptedValue(Feature.TNC, false);
        _setFeatureToUnacceptedValue(Feature.TNC, true);
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

}
