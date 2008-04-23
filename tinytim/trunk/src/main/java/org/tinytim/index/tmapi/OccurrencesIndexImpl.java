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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tinytim.ICollectionFactory;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicMapImpl;
import org.tinytim.index.ITypeInstanceIndex;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.OccurrencesIndex;

/**
 * Implementation of the {@link org.tmapi.index.core.OccurrencesIndex}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class OccurrencesIndexImpl extends AbstractTMAPIIndex implements
        OccurrencesIndex {

    private Map<Locator, List<Occurrence>> _loc2Occs;
    private Map<String, List<Occurrence>> _value2Occs;

    public OccurrencesIndexImpl(TopicMapImpl topicMap,
            ICollectionFactory collFactory) {
        super(topicMap, collFactory);
        _loc2Occs = collFactory.createMap();
        _value2Occs = collFactory.createMap();
    }

    private ITypeInstanceIndex _getTypeInstanceIndex() {
        return _weakTopicMap.get().getIndexManager().getTypeInstanceIndex();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.OccurrencesIndex#getOccurrenceTypes()
     */
    @Override
    public Collection<Topic> getOccurrenceTypes() {
        return _getTypeInstanceIndex().getOccurrenceTypes();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.OccurrencesIndex#getOccurrencesByType(org.tmapi.core.Topic)
     */
    @Override
    public Collection<OccurrenceImpl> getOccurrencesByType(Topic type) {
        return _getTypeInstanceIndex().getOccurrences(type);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.OccurrencesIndex#getOccurrencesByResource(org.tmapi.core.Locator)
     */
    @Override
    public Collection<Occurrence> getOccurrencesByResource(Locator value) {
        List<Occurrence> occs = _loc2Occs.get(value);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : new ArrayList<Occurrence>(occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.OccurrencesIndex#getOccurrencesByValue(java.lang.String)
     */
    @Override
    public Collection<Occurrence> getOccurrencesByValue(String value) {
        List<Occurrence> occs = _value2Occs.get(value);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : new ArrayList<Occurrence>(occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#getFlags()
     */
    @Override
    public IndexFlags getFlags() throws TMAPIIndexException {
        return IndexFlagsImpl.NOT_AUTOUPDATED;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#reindex()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void reindex() throws TMAPIIndexException {
        _value2Occs.clear();
        _loc2Occs.clear();
        for (Topic topic: _weakTopicMap.get().getTopics()) {
            for (Iterator<Occurrence> iter = topic.getOccurrences().iterator(); iter.hasNext();) {
                Occurrence occ = iter.next();
                if (occ.getValue() != null) {
                    String value = occ.getValue();
                    List<Occurrence> occs = _value2Occs.get(value);
                    if (occs == null) {
                        occs = new ArrayList<Occurrence>();
                        _value2Occs.put(value, occs);
                    }
                    occs.add(occ);
                }
                else if (occ.getResource() != null) {
                    Locator loc = occ.getResource();
                    List<Occurrence> occs = _loc2Occs.get(loc);
                    if (occs == null) {
                        occs = new ArrayList<Occurrence>();
                        _loc2Occs.put(loc, occs);
                    }
                    occs.add(occ);
                }
            }
        }
    }

}
