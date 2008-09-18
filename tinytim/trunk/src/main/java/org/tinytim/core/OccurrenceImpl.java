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
