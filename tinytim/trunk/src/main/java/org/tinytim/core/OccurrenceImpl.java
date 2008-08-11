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
package org.tinytim.core;

import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Occurrence} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class OccurrenceImpl extends DatatypeAwareConstruct implements 
        Occurrence, IMovable<Topic> {

    OccurrenceImpl(TopicMapImpl tm) {
        super(tm);
    }

    OccurrenceImpl(TopicMapImpl topicMap, Topic type, ILiteral literal, IScope scope) {
        super(topicMap, type, literal, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Occurrence#getParent()
     */
    public Topic getParent() {
        return (Topic) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IMovable#moveTo(java.lang.Object)
     */
    public void moveTo(Topic newParent) {
        _fireEvent(Event.MOVE_OCCURRENCE, _parent, newParent);
        ((TopicImpl) _parent).detachOccurrence(this);
        ((TopicImpl) newParent).attachOccurrence(this);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isOccurrence()
     */
    @Override
    public final boolean isOccurrence() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#remove()
     */
    public void remove() {
        ((TopicImpl) _parent).removeOccurrence(this);
        super.dispose();
    }

}
