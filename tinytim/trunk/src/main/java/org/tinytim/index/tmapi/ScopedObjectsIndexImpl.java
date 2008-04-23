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
package org.tinytim.index.tmapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tinytim.ICollectionFactory;
import org.tinytim.TopicMapImpl;
import org.tinytim.index.IScopedIndex;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.ScopedObjectsIndex;

/**
 * {@link org.tmapi.index.core.ScopedObjectsIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class ScopedObjectsIndexImpl extends AbstractTMAPIIndex implements
        ScopedObjectsIndex {

    public ScopedObjectsIndexImpl(TopicMapImpl topicMap,
            ICollectionFactory collFactory) {
        super(topicMap, collFactory);
    }

    private IScopedIndex _getScopedIndex() {
        return _weakTopicMap.get().getIndexManager().getScopedIndex();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.ScopedObjectsIndex#getScopedObjectsByScopingTopic(org.tmapi.core.Topic)
     */
    public Collection<ScopedObject> getScopedObjectsByScopingTopic(Topic theme) {
        IScopedIndex scopedIdx = _getScopedIndex();
        List<ScopedObject> res = new ArrayList<ScopedObject>(scopedIdx.getAssociationsByTheme(theme));
        res.addAll(scopedIdx.getOccurrencesByTheme(theme));
        res.addAll(scopedIdx.getNamesByTheme(theme));
        res.addAll(scopedIdx.getVariantsByTheme(theme));
        return res;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.ScopedObjectsIndex#getScopedObjectsByScopingTopics(org.tmapi.core.Topic[], boolean)
     */
    public Collection<ScopedObject> getScopedObjectsByScopingTopics(Topic[] themes, boolean matchAll) {
        Set<ScopedObject> result = null;
        if (!matchAll) {
            result = new HashSet<ScopedObject>();
            for (Topic theme: themes) {
                result.addAll(getScopedObjectsByScopingTopic(theme));
            }
            return result;
        }
        else {
            result = new HashSet<ScopedObject>(getScopedObjectsByScopingTopic(themes[0]));
            for (int i=1; i<themes.length; i++) {
                result.retainAll(getScopedObjectsByScopingTopic(themes[i]));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.ScopedObjectsIndex#getScopingTopics()
     */
    public Collection<Topic> getScopingTopics() {
        IScopedIndex scopedIdx = _getScopedIndex();
        List<Topic> res = new ArrayList<Topic>(scopedIdx.getAssociationThemes());
        res.addAll(scopedIdx.getOccurrenceThemes());
        res.addAll(scopedIdx.getNameThemes());
        res.addAll(scopedIdx.getVariantThemes());
        return res;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#getFlags()
     */
    public IndexFlags getFlags() throws TMAPIIndexException {
        return IndexFlagsImpl.AUTOUPDATED;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#reindex()
     */
    public void reindex() throws TMAPIIndexException {
        // noop.
    }

}
