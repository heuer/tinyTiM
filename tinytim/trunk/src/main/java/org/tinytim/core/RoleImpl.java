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

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IAssociation;
import org.tinytim.internal.api.IRole;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;

/**
 * {@link org.tmapi.core.Role} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class RoleImpl extends TypedImpl implements IRole {

    private Topic _player;

    RoleImpl(ITopicMap tm, Topic type, Topic player) {
        super(tm, type);
        _player = player;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ConstructImpl#getParent()
     */
    @Override
    public Association getParent() {
        return (Association) _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Role#getPlayer()
     */
    @Override
    public Topic getPlayer() {
        return _player;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Role#setPlayer(org.tmapi.core.Topic)
     */
    @Override
    public void setPlayer(Topic player) {
        Check.playerNotNull(this, player);
        Check.sameTopicMap(this, player);
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

    @Override
    public void moveTo(IAssociation newParent) {
        ((AssociationImpl) _parent).detachRole(this);
        ((AssociationImpl) newParent).attachRole(this);
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
    @Override
    public void remove() {
        ((AssociationImpl) _parent).removeRole(this);
        super.dispose();
    }

}
