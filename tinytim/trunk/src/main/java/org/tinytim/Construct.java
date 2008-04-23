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

import java.util.Collections;
import java.util.Set;

import org.tmapi.core.DuplicateSourceLocatorException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;

/**
 * Base class for all Topic Maps constructs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class Construct implements TopicMapObject, IConstruct {

    protected String _id;
    protected TopicMapImpl _tm;
    protected IConstruct _parent;
    private Set<Locator> _iids;

    Construct(TopicMapImpl topicMap) {
        _tm = topicMap;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IConstruct#getParent()
     */
    public IConstruct getParent() {
        return _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#getObjectId()
     */
    public String getObjectId() {
        return _id;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#getSourceLocators()
     */
    public Set<Locator> getSourceLocators() {
        return getItemIdentifiers();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#addSourceLocator(org.tmapi.core.Locator)
     */
    public void addSourceLocator(Locator loc)
            throws DuplicateSourceLocatorException {
        addItemIdentifier(loc);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#removeSourceLocator(org.tmapi.core.Locator)
     */
    public void removeSourceLocator(Locator loc) {
        removeItemIdentifier(loc);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#getTopicMap()
     */
    public TopicMap getTopicMap() {
        return _tm;
    }

    /* (non-Javadoc)
     * @see org.tinytim.IConstruct#getItemIdentifiers()
     */
    public Set<Locator> getItemIdentifiers() {
        return _iids == null ? Collections.<Locator>emptySet()
                             : Collections.unmodifiableSet(_iids);
    }

    /* (non-Javadoc)
     * @see org.tinytim.IConstruct#addItemIdentifier(org.tmapi.core.Locator)
     */
    public void addItemIdentifier(Locator itemIdentifier) {
        if (_iids != null && _iids.contains(itemIdentifier)) {
            return;
        }
        _fireEvent(Event.ADD_IID, null, itemIdentifier);
        if (_iids == null) {
            _iids = _tm.getCollectionFactory().createSet();
        }
        _iids.add(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see org.tinytim.IConstruct#removeItemIdentifier(org.tmapi.core.Locator)
     */
    public void removeItemIdentifier(Locator itemIdentifier) {
        if (_iids == null || !_iids.contains(itemIdentifier)) {
            return;
        }
        _fireEvent(Event.REMOVE_IID, itemIdentifier, null);
        _iids.remove(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof Construct) && _id.equals(((Construct) obj)._id);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    /**
     * Notifies the topic map about an event.
     * 
     * If the topic map is <code>null</code>, no event is sent.
     *
     * @param evt The event.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    protected void _fireEvent(Event evt, Object oldValue, Object newValue) {
        if (_tm != null) {
            _tm.handleEvent(evt, this, oldValue, newValue);
        }
    }

    /**
     * Releases used resources.
     * 
     * Should be called in the {@link org.tmapi.core.TopicMapObject#remove()}
     * method.
     */
    protected void dispose() {
        _tm = null;
        _parent = null;
        _iids = null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" iids=[");
        for (Locator sid: getItemIdentifiers()) {
            sb.append(sid);
            sb.append(',');
        }
        sb.append("]");
        return sb.toString();
    }
}
