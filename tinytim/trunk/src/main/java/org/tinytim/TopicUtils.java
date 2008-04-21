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
package org.tinytim;

import org.tinytim.index.IScopedIndex;
import org.tinytim.index.ITypeInstanceIndex;
import org.tinytim.index.IndexManager;
import org.tmapi.core.Topic;

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
     * Returns if the <code>topic</code> is removable.
     * 
     * A topic is removable iff it plays no role, is not used as type of
     * a typed Topic Maps construct, is not not used as theme of a scoped
     * Topic Maps construct and iff it is not used reifier.
     *
     * @param topic The topic to check.
     * @return <code>true</code> if the topic has no dependencies, 
     *          otherwise <code>false</code>.
     */
    public static boolean isRemovable(Topic topic) {
        if (((TopicImpl) topic)._reified != null) {
            return false;
        }
        if (!topic.getRolesPlayed().isEmpty()) {
            return false;
        }
        IndexManager idxMan = ((TopicMapImpl) topic.getTopicMap()).getIndexManager();
        ITypeInstanceIndex typeInstanceIdx = idxMan.getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        if (!typeInstanceIdx.getAssociations(topic).isEmpty()
                || !typeInstanceIdx.getRoles(topic).isEmpty()
                || !typeInstanceIdx.getOccurrences(topic).isEmpty()
                || !typeInstanceIdx.getNames(topic).isEmpty()) {
            return false;
        }
        IScopedIndex scopedIdx = idxMan.getScopedIndex();
        if (!scopedIdx.isAutoUpdated()) {
            scopedIdx.reindex();
        }
        if (!scopedIdx.getAssociationsByTheme(topic).isEmpty()
                || !scopedIdx.getOccurrencesByTheme(topic).isEmpty()
                || !scopedIdx.getNamesByTheme(topic).isEmpty()
                || !scopedIdx.getVariantsByTheme(topic).isEmpty()) {
            return false;
        }
        return true;
    }
}
