/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinytim.core;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopicMap;

import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Occurrence} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class OccurrenceImpl extends DatatypeAwareConstruct implements IOccurrence {

    OccurrenceImpl(ITopicMap tm) {
        super(tm);
    }

    OccurrenceImpl(ITopicMap topicMap, Topic type, ILiteral literal, IScope scope) {
        super(topicMap, type, literal, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Occurrence#getParent()
     */
    public Topic getParent() {
        return (Topic) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IMovable#moveTo(org.tmapi.core.Construct)
     */
    public void moveTo(Topic newParent) {
        ((TopicImpl) _parent).detachOccurrence(this, true);
        ((TopicImpl) newParent).attachOccurrence(this, true);
        _fireEvent(Event.MOVED_OCCURRENCE, _parent, newParent);
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
