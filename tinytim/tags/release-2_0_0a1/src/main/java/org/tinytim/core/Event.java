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

/**
 * Event constants.
 * 
 * All events are sent before a change happens. This allows to check
 * some constraints.
 * 
 * This class is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public enum Event {

    /**
     * Notification that a topic should be added.
     */
    ADD_TOPIC,
    /**
     * Notification that a topic should be removed.
     */
    REMOVE_TOPIC,
    /**
     * Notification that an association should be added.
     */
    ADD_ASSOCIATION,
    /**
     * Notification that an association should be removed.
     */
    REMOVE_ASSOCIATION,
    /**
     * Notification that a role should be added.
     */
    ADD_ROLE,
    /**
     * Notification that a role should be removed.
     */
    REMOVE_ROLE,
    /**
     * Notification that an occurrence should be added.
     */
    ADD_OCCURRENCE,
    /**
     * Notification that an occurrence should be removed.
     */
    REMOVE_OCCURRENCE,
    /**
     * Notification that a name should be added.
     */
    ADD_NAME,
    /**
     * Notification that a name should be removed.
     */
    REMOVE_NAME,
    /**
     * Notification that a variant should be added.
     */
    ADD_VARIANT,
    /**
     * Notification that a variant should be removed.
     */
    REMOVE_VARIANT,

    /**
     * Notification that a subject identifier should be added.
     */
    ADD_SID,
    /**
     * Notification that a subject identifier should be removed.
     */
    REMOVE_SID,
    /**
     * Notification that a subject locator should be added.
     */
    ADD_SLO,
    /**
     * Notification that a subject locator should be removed.
     */
    REMOVE_SLO,
    /**
     * Notification that an item identifier should be added.
     */
    ADD_IID,
    /**
     * Notification that an item identifier should be removed.
     */
    REMOVE_IID,

    /**
     * Notification that a type should be added to a topic.
     */
    ADD_TYPE,
    /**
     * Notification that a type should be removed from a topic.
     */
    REMOVE_TYPE,
    /**
     * Notification that the type of a {@link ITyped} construct should be set.
     */
    SET_TYPE,

    /**
     * Notification that the scope is changed.
     */
    SET_SCOPE,

    /**
     * Notification that the player of a role should be set.
     */
    SET_PLAYER,

    /**
     * Notification that the reifier of a {@link IReifiable} construct 
     * should be set.
     */
    SET_REIFIER,

    /**
     * Notification that the literal value of a name, an occurrence or variant 
     * should be set.
     */
    SET_LITERAL,

    MOVE_OCCURRENCE,
    MOVE_NAME,
    MOVE_VARIANT

}
