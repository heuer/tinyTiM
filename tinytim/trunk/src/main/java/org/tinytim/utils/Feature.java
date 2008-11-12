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
 * This class provides access to the feature strings that TMAPI-compatible
 * Topic Maps processors must recognize (but not necessarily support).
 * <p>
 * Copied from the TMAPIX-project.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Feature {

    private Feature() {
        // noop.
    }

    private static final String _FEATURE_BASE = "http://tmapi.org/features/";

    /**
     * An implementation which supports this feature can process locator 
     * addresses in URI notation as defined by RFC 2396.
     *
     * An implementation that supports URI notation locators MUST support 
     * the expansion of relative URIs that use a hierarchical URI scheme to 
     * fully specified URIs against a specified base URI, and MAY support 
     * the expansion of relative URIs that use other scheme-specific mechansims 
     * for relative URI expansion. 
     */
    public static final String NOTATION_URI= _FEATURE_BASE + "notation/URI";

    /**
     * An implementation which supports this feature supports the Topic Maps 
     * data model defined by the XTM 1.0 specification.
     */
    public static final String XTM_1_0 = _FEATURE_BASE + "model/xtm1.0";

    /**
     * An implementation which supports this feature supports the 
     * <a href="http://www.isotopicmaps.org/sam/sam-model/">Topic Maps 
     * Data Model (TMDM) ISO/IEC 13250-2</a>.
     */
    public static final String XTM_1_1 = _FEATURE_BASE + "model/xtm1.1";

    /**
     * An implementation which supports this feature MUST detect when two 
     * topic instances have topic names which match both in the scope of the 
     * name and the value of the name string, and, if XTM 1.1 is supported, 
     * the types are equal. Topics which have matching names must either be 
     * merged or a {@link org.tmapi.core.TopicsMustMergeException} must be 
     * raised, depending on the value of the 
     * <a href="http://tmapi.org/features/automerge">http://tmapi.org/features/automerge</a> 
     * feature.
     */
    public static final String TNC = _FEATURE_BASE + "merge/byTopicName";

    /**
     * This feature indicates that the underlying 
     * {@link org.tmapi.core.TopicMapSystem} cannot be modified.
     */
    public static final String READ_ONLY = _FEATURE_BASE + "readOnly";

    /**
     * If an implementation supports this feature, then whenever the 
     * implementation detects that two Topics should be merged (by one or more 
     * of the merge features defined under 
     * <a href="http://tmapi.org/features/merge/">http://tmapi.org/features/merge/</a>), 
     * then the implementation MUST merge the properties of these two Topics 
     * automatically and transparently to the API client.
     */
    public static final String AUTOMERGE = _FEATURE_BASE + "automerge";
}
