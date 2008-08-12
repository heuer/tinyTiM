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

import org.tmapi.core.ModelConstraintException;
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

    TypedImpl(TopicMapImpl tm) {
        super(tm);
    }

    TypedImpl(TopicMapImpl topicMap, Topic type) {
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
        if (type == null) {
            throw new ModelConstraintException(this, "The type cannot be set to null");
        }
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
