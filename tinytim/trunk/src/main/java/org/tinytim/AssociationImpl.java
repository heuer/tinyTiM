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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Association} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class AssociationImpl extends Scoped implements Association,
        IReifiable, ITyped, IScoped {

    private Set<AssociationRole> _roles;
    private Topic _type;

    AssociationImpl(TopicMapImpl topicMap) {
        super(topicMap, null);
        _roles = topicMap.getCollectionFactory().createSet(2);
    }

    /**
     * Adds a role to this association.
     *
     * @param role The role to add.
     */
    void addRole(AssociationRole role) {
        AssociationRoleImpl r = (AssociationRoleImpl) role;
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
    void removeRole(AssociationRole role) {
        AssociationRoleImpl r = (AssociationRoleImpl) role;
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
     * @see org.tmapi.core.Association#createAssociationRole(org.tmapi.core.Topic, org.tmapi.core.Topic)
     */
    public AssociationRole createAssociationRole(Topic player, Topic type) {
        AssociationRoleImpl role = new AssociationRoleImpl(_tm, type, player);
        addRole(role);
        return role;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getAssociationRoles()
     */
    public Set<AssociationRole> getAssociationRoles() {
        return Collections.unmodifiableSet(_roles);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#getType()
     */
    public Topic getType() {
        return _type;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Association#setType(org.tmapi.core.Topic)
     */
    public void setType(Topic type) {
        if (_type == type) {
            return;
        }
        _fireEvent(Event.SET_TYPE, _type, type);
        _type = type;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapObject#remove()
     */
    public void remove() throws TMAPIException {
        _tm.removeAssociation(this);
        for (AssociationRole role: new ArrayList<AssociationRole>(_roles)) {
            role.remove();
        }
        _roles = null;
        super.dispose();
    }
}
