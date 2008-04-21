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

import org.tmapi.core.Topic;

/**
 * Class that provides a "type" property and fires and event if that type
 * property changes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class Typed extends Scoped {

    //NOTE: This class does NOT implement ITyped by intention!
    //      DatatypeAwareConstruct extends this class and variants are not ITyped!

    private Topic _type;

    Typed(TopicMapImpl topicMap, Topic type, Collection<Topic> scope) {
        super(topicMap, scope);
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
        if (_type == type) {
            return;
        }
        _fireEvent(Event.SET_TYPE, _type, type);
        _type = type;
    }

    /* (non-Javadoc)
     * @see org.tinytim.Scoped#dispose()
     */
    @Override
    protected void dispose() {
        _type = null;
        super.dispose();
    }

}
