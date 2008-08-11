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

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Role} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class RoleImpl extends TypedImpl implements Role {

    private Topic _player;

    RoleImpl(TopicMapImpl tm) {
        super(tm);
    }

    RoleImpl(TopicMapImpl tm, Topic type, Topic player) {
        super(tm, type);
        _player = player;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    public Association getParent() {
        return (Association) _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Role#getPlayer()
     */
    public Topic getPlayer() {
        return _player;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Role#setPlayer(org.tmapi.core.Topic)
     */
    public void setPlayer(Topic player) {
        if (player == null) {
            throw new IllegalArgumentException("The role player must not be null");
        }
        if (_player == player) {
            return;
        }
        _fireEvent(Event.SET_PLAYER, _player, player);
        if (_player != null) {
            ((TopicImpl)_player).removeRolePlayed(this);
        }
        _player = player;
        if (player != null) {
            ((TopicImpl) player).addRolePlayed(this);
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#isRole()
     */
    @Override
    public final boolean isRole() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Construct#remove()
     */
    public void remove() {
        ((AssociationImpl) _parent).removeRole(this);
        super.dispose();
    }

}
