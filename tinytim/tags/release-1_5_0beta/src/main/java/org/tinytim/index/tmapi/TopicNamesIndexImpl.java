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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.tinytim.ICollectionFactory;
import org.tinytim.TopicMapImpl;
import org.tinytim.TopicNameImpl;
import org.tinytim.index.ITypeInstanceIndex;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.TopicNamesIndex;

/**
 * Implementation of the {@link org.tmapi.index.core.TopicNamesIndex};
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TopicNamesIndexImpl extends AbstractTMAPIIndex implements
        TopicNamesIndex {

    private Map<String, List<TopicName>> _value2Names;

    public TopicNamesIndexImpl(TopicMapImpl topicMap,
            ICollectionFactory collFactory) {
        super(topicMap, collFactory);
        _value2Names = collFactory.createMap();
    }

    private ITypeInstanceIndex _getTypeInstanceIndex() {
        return _weakTopicMap.get().getIndexManager().getTypeInstanceIndex();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicNamesIndex#getTopicNameTypes()
     */
    public Collection<Topic> getTopicNameTypes() {
        return _getTypeInstanceIndex().getNameTypes();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicNamesIndex#getTopicNamesByType(org.tmapi.core.Topic)
     */
    public Collection<TopicNameImpl> getTopicNamesByType(Topic type) {
        return _getTypeInstanceIndex().getNames(type);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.TopicNamesIndex#getTopicNamesByValue(java.lang.String)
     */
    public Collection<TopicName> getTopicNamesByValue(String value) {
        List<TopicName> names = _value2Names.get(value);
        return names == null ? Collections.<TopicName>emptySet()
                            : new ArrayList<TopicName>(names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#getFlags()
     */
    public IndexFlags getFlags() throws TMAPIIndexException {
        return IndexFlagsImpl.NOT_AUTOUPDATED;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#reindex()
     */
    @SuppressWarnings("unchecked")
    public void reindex() throws TMAPIIndexException {
        _value2Names.clear();
        ITypeInstanceIndex typeInstanceIdx = _weakTopicMap.get().getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        for (Topic type: typeInstanceIdx.getNameTypes()) {
            for (TopicName name: typeInstanceIdx.getNames(type)) {
                _index(name);
            }
        }
        for (TopicName name: typeInstanceIdx.getNames(null)) {
            _index(name);
        }
    }

    private void _index(TopicName name) {
        String value = name.getValue();
        List<TopicName> names = _value2Names.get(value);
        if (names == null) {
            names = new ArrayList<TopicName>();
            _value2Names.put(value, names);
        }
        names.add(name);
    }

}
