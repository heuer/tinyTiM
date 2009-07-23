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

import java.net.URL;

import org.tmapi.core.TopicMap;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestLTMTopicMapReader extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        for (URL url: CXTMTestUtils.filterValidFiles("ltm", "ltm")) {
            suite.addTest(new TestLTMTopicMapReaderCase(url, "ltm"));
        }
        return suite;
    }

    private static class TestLTMTopicMapReaderCase extends AbstractCXTMTestCase {

        protected TestLTMTopicMapReaderCase(URL url, String subdir) {
            super(url, subdir);
        }

        /* (non-Javadoc)
         * @see org.tinytim.mio.AbstractCXTMTestCase#makeReader(org.tmapi.core.TopicMap, java.net.URL)
         */
        @Override
        protected TopicMapReader makeReader(TopicMap tm, URL file) throws Exception {
            LTMTopicMapReader reader = new LTMTopicMapReader(tm, file.openStream(), file.toExternalForm());
            reader.setLegacyReifierHandling(true);
            return reader;
        }

    }
}
