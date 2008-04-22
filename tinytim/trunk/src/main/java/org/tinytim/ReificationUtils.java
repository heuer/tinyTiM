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

import java.util.Iterator;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapObject;

/**
 * Functions to support the XTM 1.0 reification mechanism.
 * 
 * This class is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class ReificationUtils {

    /**
     * Returns all Topic Maps constructs which have an item identifier 
     * equals to a subject identifier of the specified <code>reifier</code>.
     *
     * @param reifier A topic.
     * @return A (maybe empty) collection of Topic Maps constructs.
     */
    public static Set<TopicMapObject> getReified(Topic reifier) {
        TopicMapImpl tm = (TopicMapImpl) reifier.getTopicMap();
        Set<TopicMapObject> reified = tm.getCollectionFactory().createSet();
        for (Locator sid: ((TopicImpl) reifier).getSubjectIdentifiers()) {
            TopicMapObject obj = tm.getObjectByItemIdentifier(sid);
            if (obj != null) {
                reified.add(obj);
            }
        }
        return reified;
    }

    /**
     * Returns a topic that has a subject identifier equals to one of the 
     * item identifiers of the <code>reifiable</code>.
     *
     * @param reifiable The reifiable Topic Maps construct.
     * @return A topic or <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public static Topic getReifier(TopicMapObject reifiable) {
        if (reifiable instanceof Topic) {
            throw new IllegalArgumentException("Topics are not reifiable");
        }
        TopicMapImpl tm = (TopicMapImpl) reifiable.getTopicMap();
        for (Iterator<Locator> iter = reifiable.getSourceLocators().iterator(); iter.hasNext();) {
            Topic reifier = tm.getTopicBySubjectIdentifier(iter.next());
            if (reifier != null) {
                return reifier;
            }
        }
        return null;
    }
}
