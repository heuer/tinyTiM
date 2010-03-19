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

import org.tinytim.core.value.TestLiteral;
import org.tinytim.core.value.TestLiteralNormalizer;
import org.tinytim.core.value.TestLocatorImpl;
import org.tinytim.internal.utils.TestSignatureGenerator;
//import org.tinytim.mio.TestTinyTimMapInputHandler;
import org.tinytim.utils.TestDuplicateRemovalUtils;
import org.tinytim.utils.TestTopicUtils;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs all tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class AllTests extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDuplicateRemovalUtils.class);
        suite.addTestSuite(TestLocatorImpl.class);
        suite.addTestSuite(TestIConstruct.class);
        suite.addTestSuite(TestScope.class);
        suite.addTestSuite(TestLiteral.class);
//TODO: Add me
//        suite.addTestSuite(TestTinyTimMapInputHandler.class);
        suite.addTestSuite(TestLiteralNormalizer.class);
        suite.addTestSuite(TestSignatureGenerator.class);
        suite.addTest(TestTMAPICore.suite());
        suite.addTest(TestTMAPIIndex.suite());
        suite.addTestSuite(TestTopicMapSystemFactoryImpl.class);
        suite.addTestSuite(TestTopicUtils.class);
        return suite;
    }

}
