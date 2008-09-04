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
        suite.addTestSuite(TestIRI.class);
        suite.addTestSuite(TestIConstruct.class);
        suite.addTestSuite(TestScope.class);
        suite.addTestSuite(TestLiteral.class);
        suite.addTestSuite(TestLiteralNormalizer.class);
        suite.addTestSuite(TestItemIdentifierConstraint.class);
        suite.addTestSuite(TestSignatureGenerator.class);
        suite.addTest(TestTMAPICore.suite());
        suite.addTest(TestTMAPIIndex.suite());
        suite.addTestSuite(TestTopicMapSystemFactoryImpl.class);
        suite.addTestSuite(TestTopicUtils.class);
        return suite;
    }
}
