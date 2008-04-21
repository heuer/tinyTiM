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
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.MergeException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.TopicName} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class TopicNameImpl extends Typed implements TopicName, ITyped, IScoped {

    private String _value;
    private Set<Variant> _variants;


    TopicNameImpl(TopicMapImpl topicMap, Topic type, String value, Collection<Topic> scope) {
        super(topicMap, type, scope);
        _value = value;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#getTopic()
     */
    public Topic getTopic() {
        return (Topic) _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#getValue()
     */
    public String getValue() {
        return _value;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#setValue(java.lang.String)
     */
    public void setValue(String value) throws MergeException {
        _value = value;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#getVariants()
     */
    public Set<Variant> getVariants() {
        return _variants == null ? Collections.<Variant>emptySet()
                                 : Collections.unmodifiableSet(_variants);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#createVariant(org.tmapi.core.Locator, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Variant createVariant(Locator value, Collection scope) {
        Variant variant = new VariantImpl(_tm, value, scope);
        addVariant(variant);
        return variant;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicName#createVariant(java.lang.String, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Variant createVariant(String value, Collection scope) {
        Variant variant = new VariantImpl(_tm, value, scope);
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
       if (_variants == null) {
           _variants = _tm.getCollectionFactory().createSet();
       }
       v._parent = this;
       _variants.add(v);
    }

   void removeVariant(Variant variant) {
       VariantImpl v = (VariantImpl) variant;
       if (v._parent != this) {
           return;
       }
       _fireEvent(Event.REMOVE_VARIANT, v, null);
       _variants.remove(v);
       v._parent = null;
   }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() throws TMAPIException {
        ((TopicImpl) _parent).removeName(this);
        super.dispose();
    }

}
