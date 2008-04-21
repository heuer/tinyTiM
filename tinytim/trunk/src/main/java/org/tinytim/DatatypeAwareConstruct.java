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

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

/**
 * Implementation of {@link org.tinytim.IDatatypeAwareConstruct}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class DatatypeAwareConstruct extends Typed implements 
        IDatatypeAwareConstruct, IScoped {

    private String _value;
    private Locator _resource;

    DatatypeAwareConstruct(TopicMapImpl topicMap, Topic type, String value, Collection<Topic> scope) {
        super(topicMap, type, scope);
        _value = value;
    }

    DatatypeAwareConstruct(TopicMapImpl topicMap, Topic type, Locator value, Collection<Topic> scope) {
        super(topicMap, type, scope);
        _resource = value;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IDatatypeAwareConstruct#getValue2()
     */
    public String getValue2() {
        if (_value != null) {
            return _value;
        }
        return _resource != null ? _resource.getReference() : ""; 
    }

    /**
     * 
     *
     * @param value
     */
    public void setValue(String value) {
        _fireEvent(Event.SET_VALUE, _value, value);
        _resource = null;
        _value = value;
    }

    /**
     * 
     *
     * @return
     */
    public String getValue() {
        return _value;
    }

    /**
     * 
     *
     * @return
     */
    public Locator getResource() {
        return _resource;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Occurrence#setResource(org.tmapi.core.Locator)
     */
    public void setResource(Locator value) {
        _fireEvent(Event.SET_VALUE, _value, value);
        _value = null;
        _resource = value;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IDatatypeAwareConstruct#getDatatype()
     */
    public Locator getDatatype() {
        if (_value != null || _resource == null) {
            return STRING;
        }
        return ANY_URI;
    }

    /* (non-Javadoc)
     * @see org.tinytim.Construct#dispose()
     */
    @Override
    protected void dispose() {
        _value = null;
        _resource = null;
        super.dispose();
    }
}
