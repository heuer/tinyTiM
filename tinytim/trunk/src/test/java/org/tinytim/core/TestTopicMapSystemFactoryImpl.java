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
        assertFalse(_sysFactory.getFeature(Feature.READ_ONLY));
        assertFalse(_sysFactory.getFeature(Feature.AUTOMERGE));
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
        _setFeatureToAcceptedValue(Feature.READ_ONLY, false);
        _setFeatureToUnacceptedValue(Feature.READ_ONLY, true);
        _setFeatureToAcceptedValue(Feature.AUTOMERGE, false);
        _setFeatureToUnacceptedValue(Feature.AUTOMERGE, true);
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
