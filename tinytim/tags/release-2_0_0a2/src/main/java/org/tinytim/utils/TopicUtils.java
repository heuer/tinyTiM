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
package org.tinytim.utils;

import org.tinytim.core.TopicMapImpl;
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
     * Topic Maps construct and iff it is not used reifier 
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
        IIndexManager idxMan = ((TopicMapImpl) topic.getTopicMap()).getIndexManager();
        TypeInstanceIndex typeInstanceIdx = idxMan.getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        boolean removable = typeInstanceIdx.getAssociations(topic).isEmpty()
                                && typeInstanceIdx.getRoles(topic).isEmpty()
                                && typeInstanceIdx.getOccurrences(topic).isEmpty()
                                && typeInstanceIdx.getNames(topic).isEmpty();
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
