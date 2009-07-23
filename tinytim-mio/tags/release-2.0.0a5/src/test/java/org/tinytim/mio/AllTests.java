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
package org.tinytim.mio;

import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class AllTests extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TestJTMTopicMapReader.suite());
        suite.addTest(TestN3TopicMapReader.suite());
        suite.addTest(TestLTMTopicMapReader.suite());
        suite.addTest(TestTMXMLTopicMapReader.suite());
        suite.addTest(TestTMXMLValidatingTopicMapReader.suite());
        suite.addTest(TestSnelloTopicMapReader.suite());
        suite.addTest(TestXTM10TopicMapReader.suite());
        suite.addTest(TestXTM10ValidatingTopicMapReader.suite());
        suite.addTest(TestXTM20TopicMapReader.suite());
        suite.addTest(TestXTM20ValidatingTopicMapReader.suite());
        return suite;
    }
}
