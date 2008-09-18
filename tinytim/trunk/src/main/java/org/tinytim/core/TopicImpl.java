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

import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.utils.TopicUtils;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;

/**
 * {@link org.tmapi.core.Topic} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class TopicImpl extends ConstructImpl implements Topic {

    private Set<Role> _rolesPlayed;
    Reifiable _reified;
    private Set<Topic> _types;
    private Set<Locator> _sids;
    private Set<Locator> _slos;
    private Set<Occurrence> _occs;
    private Set<Name> _names;

    TopicImpl(TopicMapImpl topicMap) {
        super(topicMap);
        _sids = CollectionFactory.createIdentitySet(IConstant.TOPIC_SID_SIZE);
        _occs = CollectionFactory.createIdentitySet(IConstant.TOPIC_OCCURRENCE_SIZE);
        _names = CollectionFactory.createIdentitySet(IConstant.TOPIC_NAME_SIZE);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    @Override
    public TopicMap getParent() {
        return _tm;
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
    public void addSubjectIdentifier(Locator sid) {
        if (sid == null) {
            throw new ModelConstraintException(this, "The subject identifier must not be null");
        }
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
    public void addSubjectLocator(Locator slo) {
        if (slo == null) {
            throw new ModelConstraintException(this, "The subject locator must not be null");
        }
        if (_slos != null && _sids.contains(slo)) {
            return;
        }
        _fireEvent(Event.ADD_SLO, null, slo);
        if (_slos == null) {
            _slos = CollectionFactory.createIdentitySet(IConstant.TOPIC_SLO_SIZE);
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
        _slos.remove(slo);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getOccurrences()
     */
    public Set<Occurrence> getOccurrences() {
        return Collections.unmodifiableSet(_occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
     */
    public Occurrence createOccurrence(Topic type, String value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return _createOccurrence(type, Literal.create(value), scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Locator, org.tmapi.core.Topic, java.util.Collection)
     */
    public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return _createOccurrence(type, Literal.create(value), scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, java.util.Collection)
     */
    public Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> scope) {
        Check.valueNotNull(this, value, datatype);
        return _createOccurrence(type, Literal.create(value, datatype), scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, datatype, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    public Occurrence createOccurrence(Topic type, Locator value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
     */
    public Occurrence createOccurrence(Topic type, String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, Arrays.asList(scope));
    }

    Occurrence _createOccurrence(Topic type, ILiteral literal, Collection<Topic> scope) {
        Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        Occurrence occ = new OccurrenceImpl(_tm, type, literal, Scope.create(scope));
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
    public Set<Name> getNames() {
        return Collections.unmodifiableSet(_names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getNames(org.tmapi.core.Topic)
     */
    public Set<Name> getNames(Topic type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        Set<Name> names = CollectionFactory.createIdentitySet();
        for (Name name: _names) {
            if (type == name.getType()) {
                names.add(name);
            }
        }
        return names;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(java.lang.String, org.tmapi.core.Topic[])
     */
    public Name createName(String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createName(value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
     */
    public Name createName(Topic type, String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createName(type, value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getOccurrences(org.tmapi.core.Topic)
     */
    public Set<Occurrence> getOccurrences(Topic type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        Set<Occurrence> occs = CollectionFactory.createIdentitySet();
        for (Occurrence occ: _occs) {
            if (type == occ.getType()) {
                occs.add(occ);
            }
        }
        return occs;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(java.lang.String, java.util.Collection)
     */
    public Name createName(String value, Collection<Topic> scope) {
        return createName(_tm.getDefaultTopicNameType(), value, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
     */
    public Name createName(Topic type, String value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return _createName(type, Literal.create(value), scope);
    }

    public Name _createName(Topic type, ILiteral literal, Collection<Topic> scope) {
        Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        NameImpl name = new NameImpl(_tm, type, literal, Scope.create(scope));
        addName(name);
        return name;
    }

    void addName(Name name) {
        NameImpl n = (NameImpl) name;
        if (n._parent == this) {
            return;
        }
        assert n._parent == null;
        _fireEvent(Event.ADD_NAME, null, n);
        attachName(n);
    }

    void removeName(Name name) {
        NameImpl n = (NameImpl) name;
        if (n._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_NAME, n, null);
        detachName(n);
    }

    void attachName(NameImpl name) {
        name._parent = this;
        _names.add(name);
    }

    void detachName(NameImpl name) {
        _names.remove(name);
        name._parent = null;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getReified()
     */
    public Reifiable getReified() {
        return _reified;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed()
     */
    public Set<Role> getRolesPlayed() {
        return _rolesPlayed == null ? Collections.<Role>emptySet()
                                    : Collections.unmodifiableSet(_rolesPlayed); 
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic)
     */
    public Set<Role> getRolesPlayed(Topic type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        if (_rolesPlayed == null) {
            return Collections.emptySet();
        }
        Set<Role> roles = CollectionFactory.createIdentitySet(_rolesPlayed.size());
        for (Role role: _rolesPlayed) {
            if (type == role.getType()) {
                roles.add(role);
            }
        }
        return roles;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic, org.tmapi.core.Topic)
     */
    public Set<Role> getRolesPlayed(Topic type, Topic assoc) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        if (assoc == null) {
            throw new IllegalArgumentException("The association type must not be null");
        }
        if (_rolesPlayed == null) {
            return Collections.emptySet();
        }
        Set<Role> roles = CollectionFactory.createIdentitySet(_rolesPlayed.size());
        for (Role role: _rolesPlayed) {
            if (type == role.getType() && assoc == role.getParent().getType()) {
                roles.add(role);
            }
        }
        return roles;
    }

    void addRolePlayed(Role role) {
        if (_rolesPlayed == null) {
            _rolesPlayed = CollectionFactory.createIdentitySet(IConstant.TOPIC_ROLE_SIZE);
        }
        _rolesPlayed.add(role);
    }

    void removeRolePlayed(Role role) {
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
        Check.typeNotNull(this, type);
        if (_types != null && _types.contains(type)) {
            return;
        }
        _fireEvent(Event.ADD_TYPE, null, type);
        if (_types == null) {
            _types = CollectionFactory.createIdentitySet(IConstant.TOPIC_TYPE_SIZE);
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
    public void mergeIn(Topic source) {
        MergeUtils.merge(source, this);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isTopic()
     */
    @Override
    public final boolean isTopic() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() throws TopicInUseException {
        if (!TopicUtils.isRemovable(this, true)) {
            throw new TopicInUseException(this, "The topic is used as type, player, reifier, or theme");
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
