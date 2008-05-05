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
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.Variant} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class VariantImpl extends DatatypeAwareConstruct implements 
        Variant, IScoped, IMovable<TopicName> {

    VariantImpl(TopicMapImpl topicMap, String value, Collection<Topic> scope) {
        super(topicMap, null, value, scope);
    }

    VariantImpl(TopicMapImpl topicMap, Locator value, Collection<Topic> scope) {
        super(topicMap, null, value, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Variant#getTopicName()
     */
    public TopicName getTopicName() {
        return (TopicName) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.Scoped#getScope()
     */
    @Override
    public Set<Topic> getScope() {
        if (_tm == null || _parent == null || !_tm._inheritNameScope) {
            return super.getScope();
        }
        else {
            Set<Topic> scope = _tm.getCollectionFactory().createSet();
            scope.addAll(super.getScope());
            scope.addAll(((IScoped) _parent).getScope());
            return scope;
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.IMovable#moveTo(java.lang.Object)
     */
    public void moveTo(TopicName newParent) {
        _fireEvent(Event.MOVE_VARIANT, _parent, newParent);
        ((TopicNameImpl) _parent).detachVariant(this);
        ((TopicNameImpl) newParent).attachVariant(this);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Variant#remove()
     */
    public void remove() throws TMAPIException {
        ((TopicNameImpl) _parent).removeVariant(this);
        super.dispose();
    }

}
