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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class CXTMTestUtils {

    public static final String DOC_IRI = "http://tinytim.sourceforge.net/map";

    private static final String _TEST_DIR = System.getProperty("org.tinytim.cxtm-test-dir");

    public static List<URL> filterValidFiles(String subdir, String ext) {
        File dir = null;
        if (_TEST_DIR == null) {
            dir = new File(CXTMTestUtils.class.getResource("../../../" + subdir + "/in").getFile());
        }
        else {
            dir = new File(_TEST_DIR + subdir + "/in");
        }
        List<URL> urls = new ArrayList<URL>();
        for (File file : dir.listFiles()) {
            if (!file.getName().endsWith(ext)) {
                continue;
            }
            try {
                urls.add(file.toURI().toURL());
            } 
            catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return urls;
    }

    public static String getCXTMFile(URL url, String subdir) {
        String fileName = url.getFile().replace("/in/", "/baseline/");
        return fileName + ".cxtm";
    }
}
