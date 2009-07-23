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
public class TestXTM20ValidatingTopicMapReader extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        for (URL url: CXTMTestUtils.filterValidFiles("xtm2", "xtm")) {
            suite.addTest(new TestXTM20TopicMapReaderCase(url, "xtm2", true));
        }
        return suite;
    }

    private static class TestXTM20TopicMapReaderCase extends AbstractCXTMTestCase {

        private boolean _validate;

        TestXTM20TopicMapReaderCase(URL url, String subdir) {
            this(url, subdir, false);
        }

        TestXTM20TopicMapReaderCase(URL url, String subdir, boolean validate) {
            super(url, subdir);
            _validate = validate;
        }


        /* (non-Javadoc)
         * @see org.tinytim.mio.AbstractCXTMTestCase#makeReader(org.tmapi.core.TopicMap, java.net.URL)
         */
        @Override
        protected TopicMapReader makeReader(TopicMap tm, URL file) throws Exception {
            XTM20TopicMapReader reader = new XTM20TopicMapReader(tm, file.openStream(), file.toExternalForm());
            reader.setValidation(_validate);
            return reader;
        }

    }
}
