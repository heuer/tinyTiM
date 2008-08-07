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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * {@link org.tmapi.core.Association} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class AssociationImpl extends ScopedImpl implements Association {

    private Set<Role> _roles;

    AssociationImpl(TopicMapImpl topicMap, Topic type, Collection<Topic> scope) {
        super(topicMap, type, scope);
        _roles = _makeSet(2);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    @Override
    public TopicMap getParent() {
        return _tm;
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
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("The player must not be null");
        }
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
        Set<Topic> roleTypes = _makeSet(_roles.size());
        for (Role role: _roles) {
            roleTypes.add(role.getType());
        }
        return roleTypes;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getRoles(org.tmapi.core.Topic)
     */
    public Set<Role> getRoles(Topic type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        Set<Role> roles = _makeSet(_roles.size());
        for (Role role: _roles) {
            if (type == role.getType()) {
                roles.add(role);
            }
        }
        return roles;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() {
        _tm.removeAssociation(this);
        for (Role role: new ArrayList<Role>(_roles)) {
            role.remove();
        }
        _roles = null;
        super.dispose();
    }
}
