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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.tinytim.core.TinyTimTestCase;
import org.tmapi.core.TopicMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public abstract class AbstractCXTMTestCase extends TinyTimTestCase {

    protected URL _url;
    private String _subdir;

    protected AbstractCXTMTestCase(URL url, String subdir) {
        super(url.getFile().substring(url.getFile().lastIndexOf('/')+1));
        _url = url;
        _subdir = subdir;
    }

    protected abstract TopicMapReader makeReader(TopicMap tm, URL url) throws Exception;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#runTest()
     */
    @Override
    protected void runTest() throws Throwable {
        TopicMapReader reader;
        try {
            reader = makeReader(_tm, _url);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        reader.read();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        CXTMTopicMapWriter writer = new CXTMTopicMapWriter(result, _url.toExternalForm());
        writer.write(_tm);
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        InputStream tmp = new FileInputStream(CXTMTestUtils.getCXTMFile(_url, _subdir));
        int b;
        while ((b = tmp.read()) != -1) {
            expected.write(b);
        }
        byte[] res = result.toByteArray();
        byte[] ref = expected.toByteArray();
        if (!Arrays.equals(res, ref)) {
            System.out.println(result.toString("utf-8"));
            fail("Expected: " + expected.toString("utf-8") + "\n, got: " + result.toString("utf-8"));
        }
    }

}
