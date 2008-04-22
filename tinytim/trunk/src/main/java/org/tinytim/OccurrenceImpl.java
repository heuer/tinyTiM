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

import java.util.Collection;

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Occurrence} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class OccurrenceImpl extends DatatypeAwareConstruct implements 
        Occurrence, ITyped, IScoped {

    OccurrenceImpl(TopicMapImpl topicMap, Topic type, String value, Collection<Topic> scope) {
        super(topicMap, type, value, scope);
    }

    OccurrenceImpl(TopicMapImpl topicMap, Topic type, Locator value, Collection<Topic> scope) {
        super(topicMap, type, value, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Occurrence#getTopic()
     */
    public Topic getTopic() {
        return (Topic) _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() throws TMAPIException {
        ((TopicImpl) _parent).removeOccurrence(this);
        super.dispose();
    }

}
