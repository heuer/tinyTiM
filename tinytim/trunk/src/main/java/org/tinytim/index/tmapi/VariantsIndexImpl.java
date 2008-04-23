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
import org.tinytim.TopicMapImpl;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;
import org.tmapi.index.IndexFlags;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.VariantsIndex;

/**
 * Implementation of the {@link org.tmapi.index.core.VariantsIndex}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class VariantsIndexImpl extends AbstractTMAPIIndex implements
        VariantsIndex {

    private Map<Locator, List<Variant>> _loc2Variants;
    private Map<String, List<Variant>> _value2Variants;

    public VariantsIndexImpl(TopicMapImpl topicMap,
            ICollectionFactory collFactory) {
        super(topicMap, collFactory);
        _loc2Variants = collFactory.createMap();
        _value2Variants = collFactory.createMap();
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.VariantsIndex#getVariantsByResource(org.tmapi.core.Locator)
     */
    public Collection<Variant> getVariantsByResource(Locator loc) {
        List<Variant> variants = _loc2Variants.get(loc);
        return variants == null ? Collections.<Variant>emptySet()
                                : new ArrayList<Variant>(variants);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.core.VariantsIndex#getVariantsByValue(java.lang.String)
     */
    public Collection<Variant> getVariantsByValue(String value) {
        List<Variant> variants = _value2Variants.get(value);
        return variants == null ? Collections.<Variant>emptySet()
                                : new ArrayList<Variant>(variants);
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
        _value2Variants.clear();
        _loc2Variants.clear();
        for (Topic topic: _weakTopicMap.get().getTopics()) {
            for (Iterator<TopicName> nameIter = topic.getTopicNames().iterator(); nameIter.hasNext();) {
                for (Iterator<Variant> iter = nameIter.next().getVariants().iterator(); iter.hasNext();) {
                    Variant variant = iter.next();
                    if (variant.getValue() != null) {
                        String value = variant.getValue();
                        List<Variant> variants = _value2Variants.get(value);
                        if (variants == null) {
                            variants = new ArrayList<Variant>();
                            _value2Variants.put(value, variants);
                        }
                        variants.add(variant);
                    }
                    else if (variant.getResource() != null) {
                        Locator loc = variant.getResource();
                        List<Variant> variants = _loc2Variants.get(loc);
                        if (variants == null) {
                            variants = new ArrayList<Variant>();
                            _loc2Variants.put(loc, variants);
                        }
                        variants.add(variant);
                    }
                }
            }
        }
    }

}
