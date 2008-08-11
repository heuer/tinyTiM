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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.Name} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class NameImpl extends ScopedImpl implements Name, IMovable<Topic>,
    ILiteralAware {

    private ILiteral _literal;
    private Set<Variant> _variants;


    NameImpl(TopicMapImpl tm) {
        super(tm);
    }

    NameImpl(TopicMapImpl topicMap, Topic type, ILiteral literal, IScope scope) {
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
     * @see org.tinytim.core.ILiteralAware#getLiteral()
     */
    public ILiteral getLiteral() {
        return _literal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteralAware#setLiteral(org.tinytim.core.ILiteral)
     */
    public void setLiteral(ILiteral literal) {
        assert literal != null;
        _fireEvent(Event.SET_LITERAL, _literal, literal);
        _literal = literal;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#getValue()
     */
    public String getValue() {
        return _literal.getValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#setValue(java.lang.String)
     */
    public void setValue(String value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#getVariants()
     */
    public Set<Variant> getVariants() {
        return _variants == null ? Collections.<Variant>emptySet()
                                 : Collections.unmodifiableSet(_variants);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, java.util.Collection)
     */
    public Variant createVariant(Locator value, Collection<Topic> scope) {
        return _createVariant(Literal.create(value), scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, java.util.Collection)
     */
    public Variant createVariant(String value, Collection<Topic> scope) {
        return _createVariant(Literal.create(value), scope);
    }

   /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Variant createVariant(Locator value, Topic... scope) {
        return createVariant(value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, java.util.Collection)
     */
    public Variant createVariant(String value, Locator datatype,
            Collection<Topic> scope) {
        return _createVariant(value, datatype, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Variant createVariant(String value, Locator datatype, Topic... scope) {
        return createVariant(value, datatype, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Name#createVariant(java.lang.String, org.tmapi.core.Topic[])
     */
    public Variant createVariant(String value, Topic... scope) {
        return createVariant(value, Arrays.asList(scope));
    }

    private Variant _createVariant(String value, Locator datatype, Collection<Topic> scope) {
        return _createVariant(Literal.create(value, datatype), scope);
    }

    Variant _createVariant(ILiteral literal, Collection<Topic> scope) {
        if (scope.isEmpty()) {
            throw new IllegalArgumentException("The scope of the variant must not be unconstrained");
        }
        Set<Topic> scope_ = _makeSet(scope.size());
        scope_.addAll(scope);
        scope_.removeAll(super.getScope());
        if (scope_.isEmpty()) {
            throw new ModelConstraintException(this, "The variant's scope is not a true superset of the parent's scope");
        }
        Variant variant = new VariantImpl(_tm, literal, Scope.create(scope_));
        addVariant(variant);
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
           _variants = _makeSet();
       }
       variant._parent = this;
       _variants.add(variant);
   }

   void detachVariant(VariantImpl variant) {
       _variants.remove(variant);
       variant._parent = null;
   }

    /* (non-Javadoc)
     * @see org.tinytim.core.IMovable#moveTo(org.tmapi.core.Construct)
     */
    public void moveTo(Topic newParent) {
        _fireEvent(Event.MOVE_NAME, _parent, newParent);
        ((TopicImpl) _parent).detachName(this);
        ((TopicImpl) newParent).attachName(this);
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
