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

import java.util.Set;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.api.IVariant;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Name;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Variant} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class VariantImpl extends DatatypeAwareConstruct implements 
        IVariant {

    VariantImpl(ITopicMap tm) {
        super(tm);
    }

    VariantImpl(ITopicMap topicMap, ILiteral literal, IScope scope) {
        super(topicMap, null, literal, scope);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    public Name getParent() {
        return (Name) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IMovable#moveTo(org.tmapi.core.Construct)
     */
    public void moveTo(Name newParent) {
        ((NameImpl) _parent).detachVariant(this);
        ((NameImpl) newParent).attachVariant(this);
        _fireEvent(Event.MOVED_VARIANT, _parent, newParent);
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

    /* (non-Javadoc)
     * @see org.tinytim.core.ScopedImpl#getScope()
     */
    @Override
    public Set<Topic> getScope() {
        Set<Topic> scope = CollectionFactory.createIdentitySet(getParent().getScope());
        scope.addAll(_scope.asSet());
        return scope;
    }

    void _addNameTheme(Topic theme) {
        if (!_scope.contains(theme)) {
            _fireEvent(Event.SET_SCOPE, _scope, _scope.add(theme));
        }
    }

    void _removeNameTheme(Topic theme) {
        IScope scope = Scope.create(getScope());
        _fireEvent(Event.SET_SCOPE, scope.add(theme), scope);
    }

}
