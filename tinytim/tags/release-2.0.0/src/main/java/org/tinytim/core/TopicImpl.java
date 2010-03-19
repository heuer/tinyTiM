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
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.MergeUtils;
import org.tinytim.utils.TopicUtils;
import org.tinytim.voc.TMDM;

import org.tmapi.core.Locator;
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
final class TopicImpl extends ConstructImpl implements ITopic {

    private Set<Role> _rolesPlayed;
    Reifiable _reified;
    private Set<Topic> _types;
    private final Set<Locator> _sids;
    private Set<Locator> _slos;
    private final Set<Occurrence> _occs;
    private final Set<Name> _names;

    TopicImpl(ITopicMap topicMap) {
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
    @Override
    public void addSubjectIdentifier(Locator sid) {
        Check.subjectIdentifierNotNull(this, sid);
        if (_sids.contains(sid)) {
            return;
        }
        _fireEvent(Event.ADD_SID, null, sid);
        _sids.add(sid);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#removeSubjectIdentifier(org.tmapi.core.Locator)
     */
    @Override
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
    @Override
    public Set<Locator> getSubjectLocators() {
        return _slos == null ? Collections.<Locator>emptySet()
                             : Collections.unmodifiableSet(_slos);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#addSubjectLocator(org.tmapi.core.Locator)
     */
    @Override
    public void addSubjectLocator(Locator slo) {
        Check.subjectLocatorNotNull(this, slo);
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
    @Override
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
    @Override
    public Set<Occurrence> getOccurrences() {
        return Collections.unmodifiableSet(_occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
     */
    @Override
    public Occurrence createOccurrence(Topic type, String value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return createOccurrence(type, Literal.create(value), _tm.createScope(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Locator, org.tmapi.core.Topic, java.util.Collection)
     */
    @Override
    public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return createOccurrence(type, Literal.create(value), _tm.createScope(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, java.util.Collection)
     */
    @Override
    public Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> scope) {
        Check.valueNotNull(this, value, datatype);
        return createOccurrence(type, Literal.create(value, datatype), _tm.createScope(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    @Override
    public Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, datatype, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, org.tmapi.core.Topic[])
     */
    @Override
    public Occurrence createOccurrence(Topic type, Locator value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
     */
    @Override
    public Occurrence createOccurrence(Topic type, String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createOccurrence(type, value, Arrays.asList(scope));
    }

    @Override
    public IOccurrence createOccurrence(Topic type, ILiteral literal, IScope scope) {
        Check.typeNotNull(this, type);
        Check.valueNotNull(this, literal);
        Check.scopeNotNull(this, scope.asSet());
        Check.sameTopicMap(this, type);
        IOccurrence occ = new OccurrenceImpl(_tm, type, literal, scope); 
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
        attachOccurrence(o, false);
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
        detachOccurrence(o, false);
    }

    void attachOccurrence(OccurrenceImpl occ, boolean silently) {
        occ._parent = this;
        _occs.add(occ);
        if (!silently) {
            _fireEvent(Event.ATTACHED_OCCURRENCE, null, occ);
        }
    }

    void detachOccurrence(OccurrenceImpl occ, boolean silently) {
        _occs.remove(occ);
        occ._parent = null;
        if (!silently) {
            _fireEvent(Event.DETACHED_OCCURRENCE, occ, null);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getNames()
     */
    @Override
    public Set<Name> getNames() {
        return Collections.unmodifiableSet(_names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getNames(org.tmapi.core.Topic)
     */
    @Override
    public Set<Name> getNames(Topic type) {
        Check.typeNotNull(type);
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
    @Override
    public Name createName(String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createName(value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])
     */
    @Override
    public Name createName(Topic type, String value, Topic... scope) {
        Check.scopeNotNull(this, scope);
        return createName(type, value, Arrays.asList(scope));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getOccurrences(org.tmapi.core.Topic)
     */
    @Override
    public Set<Occurrence> getOccurrences(Topic type) {
        Check.typeNotNull(type);
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
    @Override
    public Name createName(String value, Collection<Topic> scope) {
        return createName(_tm.createTopicBySubjectIdentifier(TMDM.TOPIC_NAME), value, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic, java.lang.String, java.util.Collection)
     */
    @Override
    public Name createName(Topic type, String value, Collection<Topic> scope) {
        Check.valueNotNull(this, value);
        return createName(type, Literal.create(value), _tm.createScope(scope));
    }

    @Override
    public IName createName(Topic type, ILiteral literal, IScope scope) {
        Check.typeNotNull(this, type);
        Check.scopeNotNull(this, scope);
        Check.sameTopicMap(this, type);
        Check.valueNotNull(this, literal);
        IName name = new NameImpl(_tm, type, literal, scope);
        this.addName(name);
        return name;
    }

    void addName(Name name) {
        NameImpl n = (NameImpl) name;
        if (n._parent == this) {
            return;
        }
        assert n._parent == null;
        _fireEvent(Event.ADD_NAME, null, n);
        attachName(n, false);
    }

    void removeName(Name name) {
        NameImpl n = (NameImpl) name;
        if (n._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_NAME, n, null);
        detachName(n, false);
    }

    void attachName(NameImpl name, boolean silently) {
        name._parent = this;
        _names.add(name);
        if (!silently) {
            _fireEvent(Event.ATTACHED_NAME, null, name);
        }
    }

    void detachName(NameImpl name, boolean silently) {
        _names.remove(name);
        name._parent = null;
        if (!silently) {
            _fireEvent(Event.DETACHED_NAME, name, null);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getReified()
     */
    @Override
    public Reifiable getReified() {
        return _reified;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed()
     */
    @Override
    public Set<Role> getRolesPlayed() {
        return _rolesPlayed == null ? Collections.<Role>emptySet()
                                    : Collections.unmodifiableSet(_rolesPlayed); 
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic)
     */
    @Override
    public Set<Role> getRolesPlayed(Topic type) {
        Check.typeNotNull(type);
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
    @Override
    public Set<Role> getRolesPlayed(Topic type, Topic assoc) {
        Check.typeNotNull(type);
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
    @Override
    public Set<Topic> getTypes() {
        return _types == null ? Collections.<Topic>emptySet()
                              : Collections.unmodifiableSet(_types);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Topic#addType(org.tmapi.core.Topic)
     */
    @Override
    public void addType(Topic type) {
        Check.typeNotNull(this, type);
        Check.sameTopicMap(this, type);
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
    @Override
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
    @Override
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
     * @see org.tmapi.core.Construct#remove()
     */
    @Override
    public void remove() throws TopicInUseException {
        if (!TopicUtils.isRemovable(this, true)) {
            throw new TopicInUseException(this, "The topic is used as type, player, reifier, or theme");
        }
        if (_reified != null) {
            _reified.setReifier(null);
        }
        ((AbstractTopicMap) _tm).removeTopic(this);
        _sids.clear();
        _slos = null;
        _types = null;
        _occs.clear();
        _names.clear();
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
