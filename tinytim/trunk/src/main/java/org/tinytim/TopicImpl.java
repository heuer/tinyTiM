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

import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.MergeException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;

/**
 * {@link org.tmapi.core.Topic} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TopicImpl extends Construct implements Topic {

    private Set<AssociationRole> _rolesPlayed;
    IReifiable _reified;
    private Set<Topic> _types;
    private Set<Locator> _sids;
    private Set<Locator> _slos;
    private Set<Occurrence> _occs;
    private Set<TopicName> _names;

    TopicImpl(TopicMapImpl topicMap) {
        super(topicMap);
        ICollectionFactory collFactory = topicMap.getCollectionFactory();
        _sids = collFactory.<Locator>createSet(2);
        _occs = collFactory.<Occurrence>createSet(2);
        _names = collFactory.<TopicName>createSet(2);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getSubjectIdentifiers()
     */
    public Set<Locator> getSubjectIdentifiers() {
        return Collections.unmodifiableSet(_sids);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#addSubjectIdentifier(org.tmapi.core.Locator)
     */
    public void addSubjectIdentifier(Locator sid) throws MergeException {
        if (_sids.contains(sid)) {
            return;
        }
        _fireEvent(Event.ADD_SID, null, sid);
        _sids.add(sid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#removeSubjectIdentifier(org.tmapi.core.Locator)
     */
    public void removeSubjectIdentifier(Locator sid) {
        if (!_sids.contains(sid)) {
            return;
        }
        _fireEvent(Event.REMOVE_SID, sid, null);
        _sids.remove(sid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getSubjectLocators()
     */
    public Set<Locator> getSubjectLocators() {
        return _slos == null ? Collections.<Locator>emptySet()
                             : Collections.unmodifiableSet(_slos);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#addSubjectLocator(org.tmapi.core.Locator)
     */
    public void addSubjectLocator(Locator slo) throws MergeException,
            ModelConstraintException {
        if (_slos != null && _sids.contains(slo)) {
            return;
        }
        _fireEvent(Event.ADD_SLO, null, slo);
        if (_slos == null) {
            _slos = _tm.getCollectionFactory().createSet();
        }
        _slos.add(slo);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#removeSubjectLocator(org.tmapi.core.Locator)
     */
    public void removeSubjectLocator(Locator slo) {
        if (_slos == null || !_slos.contains(slo)) {
            return;
        }
        _fireEvent(Event.REMOVE_SLO, slo, null);
        _sids.remove(slo);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getOccurrences()
     */
    public Set<Occurrence> getOccurrences() {
        return Collections.unmodifiableSet(_occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(java.lang.String, org.tmapi.core.Topic, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Occurrence createOccurrence(String value, Topic type, Collection scope) {
        Occurrence occ = new OccurrenceImpl(_tm, type, value, scope);
        addOccurrence(occ);
        return occ;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Locator, org.tmapi.core.Topic, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Occurrence createOccurrence(Locator value, Topic type, Collection scope) {
        Occurrence occ = new OccurrenceImpl(_tm, type, value, scope);
        addOccurrence(occ);
        return occ;
    }

    /**
     * Adds an occurrence to the [occurrences] property.
     *
     * @param occ The occurrence to add.
     */
    void addOccurrence(Occurrence occ) {
        OccurrenceImpl o = (OccurrenceImpl) occ;
        if (o._parent == this) {
            return;
        }
        _fireEvent(Event.ADD_OCCURRENCE, null, o);
        attachOccurrence(o);
    }

    /**
     * Removes an occurrence from the [occurrences] property.
     *
     * @param occ The occurrence to remove.
     */
    void removeOccurrence(Occurrence occ) {
        OccurrenceImpl o = (OccurrenceImpl) occ;
        if (o._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_OCCURRENCE, o, null);
        detachOccurrence(o);
    }

    void attachOccurrence(OccurrenceImpl occ) {
        occ._parent = this;
        _occs.add(occ);
    }

    void detachOccurrence(OccurrenceImpl occ) {
        _occs.remove(occ);
        occ._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getTopicNames()
     */
    public Set<TopicName> getTopicNames() {
        return Collections.unmodifiableSet(_names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createTopicName(java.lang.String, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public TopicName createTopicName(String value, Collection scope)
            throws MergeException {
        return createTopicName(value, null, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createTopicName(java.lang.String, org.tmapi.core.Topic, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public TopicName createTopicName(String value, Topic type, Collection scope)
            throws UnsupportedOperationException, MergeException {
        TopicNameImpl name = new TopicNameImpl(_tm, type, value, scope);
        addName(name);
        return name;
    }

    void addName(TopicName name) {
        TopicNameImpl n = (TopicNameImpl) name;
        if (n._parent == this) {
            return;
        }
        assert n._parent == null;
        _fireEvent(Event.ADD_NAME, null, n);
        attachName(n);
    }

    void removeName(TopicName name) {
        TopicNameImpl n = (TopicNameImpl) name;
        if (n._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_NAME, n, null);
        detachName(n);
    }

    void attachName(TopicNameImpl name) {
        name._parent = this;
        _names.add(name);
    }

    void detachName(TopicNameImpl name) {
        _names.remove(name);
        name._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getReified()
     */
    public Set<TopicMapObject> getReified() {
        if (_tm._oldReification) {
            return ReificationUtils.getReified(this);
        }
        return _reified != null ? Collections.<TopicMapObject>singleton(_reified)
                                : Collections.<TopicMapObject>emptySet();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed()
     */
    public Set<AssociationRole> getRolesPlayed() {
        return _rolesPlayed == null ? Collections.<AssociationRole>emptySet()
                                    : Collections.unmodifiableSet(_rolesPlayed); 
    }

    void addRolePlayed(AssociationRole role) {
        if (_rolesPlayed == null) {
            _rolesPlayed = _tm.getCollectionFactory().createSet(4);
        }
        _rolesPlayed.add(role);
    }

    void removeRolePlayed(AssociationRole role) {
        if (_rolesPlayed == null) {
            return;
        }
        _rolesPlayed.remove(role);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getTypes()
     */
    public Set<Topic> getTypes() {
        return _types == null ? Collections.<Topic>emptySet()
                              : Collections.unmodifiableSet(_types);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#addType(org.tmapi.core.Topic)
     */
    public void addType(Topic type) {
        if (_types != null && _types.contains(type)) {
            return;
        }
        _fireEvent(Event.ADD_TYPE, null, type);
        if (_types == null) {
            _types = _tm.getCollectionFactory().createSet();
        }
        _types.add(type);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#removeType(org.tmapi.core.Topic)
     */
    public void removeType(Topic type) {
        if (_types == null || !_types.contains(type)) {
            return;
        }
        _fireEvent(Event.REMOVE_TYPE, type, null);
        _types.remove(type);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#mergeIn(org.tmapi.core.Topic)
     */
    public void mergeIn(Topic source) throws MergeException {
        MergeUtils.merge(source, this);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() throws TopicInUseException {
        if (!TopicUtils.isRemovable(this, false)) {
            throw new TopicInUseException("The topic is used as type, player, or theme");
        }
        if (_reified != null) {
            _reified.setReifier(null);
        }
        _tm.removeTopic(this);
        _sids = null;
        _slos = null;
        _types = null;
        _occs = null;
        _names = null;
        _rolesPlayed = null;
        _reified = null;
        super.dispose();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(", sids=[");
        for (Locator sid: getSubjectIdentifiers()) {
            sb.append(sid);
            sb.append(',');
        }
        sb.append("], slos=[");
        for (Locator slo: getSubjectLocators()) {
            sb.append(slo);
            sb.append(',');
        }
        sb.append("]");
        return sb.toString();
    }

}
