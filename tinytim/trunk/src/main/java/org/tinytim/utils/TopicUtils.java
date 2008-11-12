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

import org.tinytim.core.IIndexManagerAware;
import org.tinytim.index.IIndexManager;
import org.tmapi.core.Topic;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * This class provides utility functions for {@link org.tmapi.core.Topic}s.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TopicUtils {

    private TopicUtils() {
        // noop.
    }

    /**
     * Returns if the <tt>topic</tt> is removable.
     * <p>
     * A topic is removable iff it plays no role, is not used as type of
     * a typed Topic Maps construct, and is not not used as theme of a scoped
     * Topic Maps construct.
     * </p>
     * This function returns the same result as 
     * <tt>isRemovable(topic, false)</tt>.
     *
     * @param topic The topic to check.
     * @return <tt>true</tt> if the topic is removable, <tt>false</tt> 
     *          otherwise.
     */
    public static boolean isRemovable(Topic topic) {
        return isRemovable(topic, false);
    }

    /**
     * Returns if the <tt>topic</tt> is removable.
     * <p>
     * A topic is removable iff it plays no role, is not used as type of
     * a typed Topic Maps construct, is not not used as theme of a scoped
     * Topic Maps construct and iff it is not used as reifier 
     * (if <tt>includeReified</tt> is <tt>true</tt>).
     * </p>
     *
     * @param topic The topic to check.
     * @param includeReified Indicates if a reified Topic Maps construct (if any)
     *          is considered as dependency.
     * @return <tt>true</tt> if the topic is removable, <tt>false</tt> 
     *          otherwise.
     */
    public static boolean isRemovable(Topic topic, boolean includeReified) {
        if (includeReified && topic.getReified() != null) {
            return false;
        }
        if (!topic.getRolesPlayed().isEmpty()) {
            return false;
        }
        IIndexManager idxMan = ((IIndexManagerAware) topic.getTopicMap()).getIndexManager();
        TypeInstanceIndex typeInstanceIdx = idxMan.getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        boolean removable = typeInstanceIdx.getAssociations(topic).isEmpty()
                                && typeInstanceIdx.getRoles(topic).isEmpty()
                                && typeInstanceIdx.getOccurrences(topic).isEmpty()
                                && typeInstanceIdx.getNames(topic).isEmpty()
                                && typeInstanceIdx.getTopics(topic).isEmpty();
        typeInstanceIdx.close();
        if (removable) {
            ScopedIndex scopedIdx = idxMan.getScopedIndex();
            if (!scopedIdx.isAutoUpdated()) {
                scopedIdx.reindex();
            }
            removable = scopedIdx.getAssociations(topic).isEmpty()
                            && scopedIdx.getOccurrences(topic).isEmpty()
                            && scopedIdx.getNames(topic).isEmpty()
                            && scopedIdx.getVariants(topic).isEmpty();
            scopedIdx.close();
        }
        return removable;
    }

}
