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
package org.tinytim.internal.api;

/**
 * Event constants.
 * <p>
 * All events are sent before a change happens. This allows to check
 * some constraints.
 * </p>
 * <p>
 * This class is not meant to be used outside of the tinyTiM package.
 * </p>
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

    /**
     * Notification that an occurrence should be moved from one topic to another.
     */
    MOVED_OCCURRENCE,
    /**
     * Notification that a name should be moved from one topic to another.
     */
    MOVED_NAME,
    /**
     * Notification that a variant should be moved from one name to another.
     */
    MOVED_VARIANT, 
    
    ATTACHED_NAME, DETACHED_NAME, ATTACHED_OCCURRENCE, DETACHED_OCCURRENCE,

}
