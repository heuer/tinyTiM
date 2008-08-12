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

import java.util.Set;

import org.tinytim.internal.utils.CollectionFactory;
import org.tmapi.core.Name;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.Variant} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class VariantImpl extends DatatypeAwareConstruct implements 
        Variant, IMovable<Name> {

    VariantImpl(TopicMapImpl tm) {
        super(tm);
    }

    VariantImpl(TopicMapImpl topicMap, ILiteral literal, IScope scope) {
        super(topicMap, null, literal, scope);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    public Name getParent() {
        return (Name) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ScopedImpl#getScope()
     */
    @Override
    public Set<Topic> getScope() {
        if (_tm == null || _parent == null) {
            return super.getScope();
        }
        Set<Topic> scope = CollectionFactory.createIdentitySet(4);
        scope.addAll(super.getScope());
        scope.addAll(((Scoped) _parent).getScope());
        return scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IMovable#moveTo(java.lang.Object)
     */
    public void moveTo(Name newParent) {
        _fireEvent(Event.MOVE_VARIANT, _parent, newParent);
        ((NameImpl) _parent).detachVariant(this);
        ((NameImpl) newParent).attachVariant(this);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isVariant()
     */
    @Override
    public final boolean isVariant() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Variant#remove()
     */
    public void remove() {
        ((NameImpl) _parent).removeVariant(this);
        super.dispose();
    }

}
