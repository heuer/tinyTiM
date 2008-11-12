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

import org.tinytim.internal.utils.Check;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * Class that provides a "type" property and fires an event if that property 
 * changes. Additionally, this class provides a {@link IReifiable} 
 * implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class TypedImpl extends ConstructImpl implements Reifiable {

    //NOTE: This class does NOT implement Typed by intention!
    //      DatatypeAwareConstruct extends this class and variants are not Typed!

    private Topic _type;
    private Topic _reifier;

    TypedImpl(ITopicMap tm) {
        super(tm);
    }

    TypedImpl(ITopicMap topicMap, Topic type) {
        super(topicMap);
        _type = type;
    }

    /* (non-Javadoc)
     * @see org.tinytim.ITyped#getType()
     */
    public Topic getType() {
        return _type;
    }

    /* (non-Javadoc)
     * @see org.tinytim.ITyped#setType(org.tmapi.core.Topic)
     */
    public void setType(Topic type) {
        Check.typeNotNull(this, type);
        if (_type == type) {
            return;
        }
        _fireEvent(Event.SET_TYPE, _type, type);
        _type = type;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.IReifiable#getReifier()
     */
    public Topic getReifier() {
        return _reifier;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IReifiable#setReifier(org.tmapi.core.Topic)
     */
    public void setReifier(Topic reifier) {
        if (_reifier == reifier) {
            return;
        }
        _fireEvent(Event.SET_REIFIER, _reifier, reifier);
        if (_reifier != null) {
            ((TopicImpl) _reifier)._reified = null;
        }
        _reifier = reifier;
        if (reifier != null) {
            ((TopicImpl) reifier)._reified = this;
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.Construct#dispose()
     */
    @Override
    protected void dispose() {
        _type = null;
        _reifier = null;
        super.dispose();
    }

}
