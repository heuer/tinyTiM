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

import java.util.Collection;

import org.tinytim.ICollectionFactory;
import org.tinytim.TopicMapImpl;
import org.tinytim.index.ITypeInstanceIndex;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.TopicsIndex;

/**
 * {@link org.tmapi.index.core.TopicsIndex} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TopicsIndexImpl extends AbstractTMAPIIndex implements TopicsIndex {

    public TopicsIndexImpl(TopicMapImpl topicMap, ICollectionFactory collFactory) {
        super(topicMap, collFactory);
    }

    private ITypeInstanceIndex _getTypeInstanceIndex() {
        return _weakTopicMap.get().getIndexManager().getTypeInstanceIndex();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicsIndex#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectIdentifier(Locator sid) {
        return _weakTopicMap.get().getTopicBySubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicsIndex#getTopicBySubjectLocator(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectLocator(Locator slo) {
        return _weakTopicMap.get().getTopicBySubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicsIndex#getTopicTypes()
     */
    public Collection<Topic> getTopicTypes() {
        return _getTypeInstanceIndex().getTopicTypes();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicsIndex#getTopicsByType(org.tmapi.core.Topic)
     */
    public Collection<Topic> getTopicsByType(Topic type) {
        return _getTypeInstanceIndex().getTopics(type);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicsIndex#getTopicsByTypes(org.tmapi.core.Topic[], boolean)
     */
    public Collection<Topic> getTopicsByTypes(Topic[] types, boolean matchAll) {
        return _getTypeInstanceIndex().getTopics(types, matchAll);
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
