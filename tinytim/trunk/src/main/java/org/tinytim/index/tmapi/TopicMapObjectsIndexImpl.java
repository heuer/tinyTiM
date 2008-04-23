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

import org.tinytim.ICollectionFactory;
import org.tinytim.TopicMapImpl;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMapObject;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.TopicMapObjectsIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TopicMapObjectsIndexImpl extends AbstractTMAPIIndex implements TopicMapObjectsIndex {

    public TopicMapObjectsIndexImpl(TopicMapImpl topicMap,
            ICollectionFactory collFactory) {
        super(topicMap, collFactory);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicMapObjectsIndex#getTopicMapObjectBySourceLocator(org.tmapi.core.Locator)
     */
    public TopicMapObject getTopicMapObjectBySourceLocator(Locator iid) {
        return _weakTopicMap.get().getObjectByItemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#getFlags()
     */
    public IndexFlags getFlags() throws TMAPIIndexException {
        return IndexFlagsImpl.AUTOUPDATED;
    }

    public void reindex() throws TMAPIIndexException {
        // noop.
    }

}
