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
package org.tinytim.voc;

/**
 * This class provides some commonly used namespaces.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class Namespace {

    private Namespace() {
        // noop.
    }

    /**
     * XTM 1.0 namespace (<tt>http://www.topicmaps.org/xtm/1.0/</tt>).
     */
    public static final String XTM_10 = "http://www.topicmaps.org/xtm/1.0/";

    /**
     * XLink namespace (<tt>http://www.w3.org/1999/xlink</tt>).
     */
    public static final String XLINK = "http://www.w3.org/1999/xlink";

    /**
     * XTM 1.0 model namespace (<tt>http://www.topicmaps.org/xtm/1.0/core.xtm#</tt>).
     */
    public static final String XTM_10_MODEL = "http://www.topicmaps.org/xtm/1.0/core.xtm#";

    /**
     * TMDM 1.0 namespace (<tt>http://psi.topicmaps.org/iso13250/model/</tt>).
     */
    public static final String TMDM_MODEL = "http://psi.topicmaps.org/iso13250/model/";

    /**
     * XTM 2.0 namespace (<tt>http://www.topicmaps.org/xtm/</tt>).
     */
    public static final String XTM_20 = "http://www.topicmaps.org/xtm/";

    /**
     * XML Schema Datatypes namespace (<tt>http://www.w3.org/2001/XMLSchema#</tt>).
     */
    public static final String XSD = "http://www.w3.org/2001/XMLSchema#";

}
