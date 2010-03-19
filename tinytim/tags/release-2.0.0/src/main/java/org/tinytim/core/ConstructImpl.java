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

import java.util.Collections;
import java.util.Set;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;

/**
 * Base class for all Topic Maps constructs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class ConstructImpl implements IConstruct {

    protected String _id;
    protected ITopicMap _tm;
    protected Construct _parent;
    private Set<Locator> _iids;

    protected ConstructImpl(ITopicMap topicMap) {
        _tm = topicMap;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#getParent()
     */
    public Construct getParent() {
        return _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#getId()
     */
    public String getId() {
        return _id;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#getTopicMap()
     */
    public TopicMap getTopicMap() {
        return _tm;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#getItemIdentifiers()
     */
    public Set<Locator> getItemIdentifiers() {
        return _iids == null ? Collections.<Locator>emptySet()
                             : Collections.unmodifiableSet(_iids);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#addItemIdentifier(org.tmapi.core.Locator)
     */
    public void addItemIdentifier(Locator iid) {
        Check.itemIdentifierNotNull(this, iid);
        if (_iids != null && _iids.contains(iid)) {
            return;
        }
        _fireEvent(Event.ADD_IID, null, iid);
        if (_iids == null) {
            _iids = CollectionFactory.createIdentitySet(IConstant.CONSTRUCT_IID_SIZE);
        }
        _iids.add(iid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#removeItemIdentifier(org.tmapi.core.Locator)
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
    public final boolean equals(Object obj) {
        return this == obj;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
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
        if (_tm != null && _parent != null) {
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
     * @see org.tinytim.internal.api.IConstruct#isAssociation()
     */
    public boolean isAssociation() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isName()
     */
    public boolean isName() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isOccurrence()
     */
    public boolean isOccurrence() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isRole()
     */
    public boolean isRole() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isTopic()
     */
    public boolean isTopic() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isTopicMap()
     */
    public boolean isTopicMap() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstruct#isVariant()
     */
    public boolean isVariant() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" ").append(_id);
        sb.append(" iids=[");
        for (Locator iid: getItemIdentifiers()) {
            sb.append(iid);
            sb.append(',');
        }
        sb.append("]");
        return sb.toString();
    }

}
