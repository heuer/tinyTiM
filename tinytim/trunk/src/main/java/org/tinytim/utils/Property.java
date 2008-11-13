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
package org.tinytim.utils;

/**
 * This class provides access to tinyTiM-specifc properties.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Property {

    private static final String _PROPERTY_BASE = "http://tinytim.sourceforge.net/property/";

    public static final String SYSTEM = _PROPERTY_BASE + "system";

    /**
     * 
     */
    public static final String TMSHARE = _PROPERTY_BASE + "tmshare";

    /**
     * 
     */
    public static final String TMSHARE_SUBSCRIBE = TMSHARE + "-subscribe";

    /**
     * 
     */
    public static final String TMSHARE_INTERVALL = TMSHARE + "-intervall";

    public static final String PERSISTENT = _PROPERTY_BASE + "persistent";

    public static final String PERSISTENT_DIRECTORY = PERSISTENT + "-directoy";

}
