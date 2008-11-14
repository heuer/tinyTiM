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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tinytim.core.value.Literal;
import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.api.IVariant;
import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.Name} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class NameImpl extends ScopedImpl implements IName {

    private ILiteral _literal;
    private Set<Variant> _variants;


    NameImpl(ITopicMap tm) {
        super(tm);
    }

    NameImpl(ITopicMap topicMap, Topic type, ILiteral literal, IScope scope) {
        super(topicMap, type, scope);
        _literal = literal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    public Topic getParent() {
        return (Topic) _parent;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteralAware#getLiteral()
     */
    public ILiteral getLiteral() {
        return _literal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteralAware#setLiteral(org.tinytim.internal.api.ILiteral)
     */
    public void setLiteral(ILiteral literal) {
        assert literal != null;
        _fireEvent(Event.SET_LITERAL, _literal, literal);
        _literal = literal;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#getValue()
     */
    public String getValue() {
        return _literal.getValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#setValue(java.lang.String)
     */
    public void setValue(String value) {
        Check.valueNotNull(this, value);
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ScopedImpl#addTheme(org.tmapi.core.Topic)
     */
    @Override
    public void addTheme(Topic theme) {
        IScope scope = _scope;
        super.addTheme(theme);
        if (scope != _scope) {
            if (_variants != null) {
                for (Variant variant: _variants) {
                    ((VariantImpl) variant)._addNameTheme(theme);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ScopedImpl#removeTheme(org.tmapi.core.Topic)
     */
    @Override
    public void removeTheme(Topic theme) {
        IScope scope = _scope;
        super.removeTheme(theme);
        if (scope != _scope) {
            if (_variants != null) {
                for (Variant variant: _variants) {
                    ((VariantImpl) variant)._removeNameTheme(theme);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#getVariants()
     */
    public Set<Variant> getVariants() {
        return _variants == null ? Collections.<Variant>emptySet()
                                 : Collections.unmodifiableSet(_variants);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, java.util.Collection)
     */
    public Variant createVariant(Locator value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return createVariant(Literal.create(value), scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, java.util.Collection)
     */
    public Variant createVariant(String value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return createVariant(Literal.create(value), scope);
    }

   /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Variant createVariant(Locator value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createVariant(value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, java.util.Collection)
     */
    public Variant createVariant(String value, Locator datatype,
            Collection<Topic> scope) {
        Check.valueNotNull(this, value, datatype);
        return _createVariant(value, datatype, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Variant createVariant(String value, Locator datatype, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createVariant(value, datatype, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Topic[])
     */
    public Variant createVariant(String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createVariant(value, Arrays.asList(scope));
    }

    private Variant _createVariant(String value, Locator datatype, Collection<Topic> scope) {
        Check.valueNotNull(this, value, datatype);
        return createVariant(Literal.create(value, datatype), scope);
    }

    public IVariant createVariant(ILiteral literal, Collection<Topic> scope) {
        if (scope.isEmpty()) {
            throw new ModelConstraintException(this, "The scope of the variant must not be unconstrained");
        }
        if (_scope.containsAll(scope)) {
            throw new ModelConstraintException(this, "The variant's scope is not a true superset of the parent's scope");
        }
        VariantImpl variant = new VariantImpl(_tm, literal, Scope.create(scope));
        addVariant(variant);
        for (Topic theme: _scope) {
            variant._addNameTheme(theme);
        }
        return variant;
    }

    void addVariant(Variant variant) {
       VariantImpl v = (VariantImpl) variant;
       if (v._parent == this) {
           return;
       }
       assert v._parent == null;
       _fireEvent(Event.ADD_VARIANT, null, v);
       attachVariant(v);
    }

    void removeVariant(Variant variant) {
        VariantImpl v = (VariantImpl) variant;
        if (v._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_VARIANT, v, null);
        detachVariant(v);
    }

    void attachVariant(VariantImpl variant) {
        if (_variants == null) {
            _variants = CollectionFactory.createIdentitySet(IConstant.NAME_VARIANT_SIZE);
        }
        variant._parent = this;
        _variants.add(variant);
    }

    void detachVariant(VariantImpl variant) {
        _variants.remove(variant);
        variant._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IMovable#moveTo(org.tmapi.core.Construct)
     */
    public void moveTo(Topic newParent) {
        ((TopicImpl) _parent).detachName(this, true);
        ((TopicImpl) newParent).attachName(this, true);
        _fireEvent(Event.MOVED_NAME, _parent, newParent);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isName()
     */
    @Override
    public final boolean isName() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#remove()
     */
    public void remove() {
        ((TopicImpl) _parent).removeName(this);
        super.dispose();
    }

}
