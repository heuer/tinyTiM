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
import org.tinytim.internal.api.IAssociation;
import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;
import org.tinytim.internal.utils.CollectionFactory;

import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * {@link org.tmapi.core.Association} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class AssociationImpl extends ScopedImpl implements IAssociation {

    private Set<Role> _roles;

    AssociationImpl(ITopicMap topicMap, Topic type, IScope scope) {
        super(topicMap, type, scope);
        _roles = CollectionFactory.createIdentitySet(IConstant.ASSOC_ROLE_SIZE);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    @Override
    public TopicMap getParent() {
        return _tm;
    }

    void attachRole(RoleImpl role) {
        role._parent = this;
        _roles.add(role);
    }

    void detachRole(RoleImpl role) {
        role._parent = null;
        _roles.remove(role);
    }

    /**
     * Adds a role to this association.
     *
     * @param role The role to add.
     */
    void addRole(Role role) {
        RoleImpl r = (RoleImpl) role;
        if (r._parent == this) {
            return;
        }
        assert r._parent == null;
        _fireEvent(Event.ADD_ROLE, null, r);
        _roles.add(r);
        r._parent = this;
        TopicImpl player = (TopicImpl) r.getPlayer();
        if (player != null) {
            player.addRolePlayed(r);
        }
    }

    /**
     * Removes a role from this association.
     *
     * @param role The role to remove.
     */
    void removeRole(Role role) {
        RoleImpl r = (RoleImpl) role;
        if (r._parent != this) {
            return;
        }
        _fireEvent(Event.REMOVE_ROLE, r, null);
        _roles.remove(role);
        r._parent = null;
        TopicImpl player = (TopicImpl) r.getPlayer();
        if (player != null) {
            player.removeRolePlayed(r);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#createRole(org.tmapi.core.Topic, org.tmapi.core.Topic)
     */
    public Role createRole(Topic type, Topic player) {
        Check.typeNotNull(this, type);
        Check.playerNotNull(this, player);
        Check.sameTopicMap(this, type, player);
        RoleImpl role = new RoleImpl(_tm, type, player);
        addRole(role);
        return role;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getRoles()
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(_roles);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getRoleTypes()
     */
    public Set<Topic> getRoleTypes() {
        Set<Topic> roleTypes = CollectionFactory.createIdentitySet(_roles.size());
        for (Role role: _roles) {
            roleTypes.add(role.getType());
        }
        return roleTypes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getRoles(org.tmapi.core.Topic)
     */
    public Set<Role> getRoles(Topic type) {
        Check.typeNotNull(type);
        Set<Role> roles = CollectionFactory.createIdentitySet(_roles.size());
        for (Role role: _roles) {
            if (type == role.getType()) {
                roles.add(role);
            }
        }
        return roles;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isAssociation()
     */
    @Override
    public final boolean isAssociation() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() {
        ((AbstractTopicMap) _tm).removeAssociation(this);
        for (Role role: CollectionFactory.createList(_roles)) {
            role.remove();
        }
        _roles = null;
        super.dispose();
    }
}
