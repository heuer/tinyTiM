/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package org.tinytim.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.tinytim.internal.api.IAssociation;
import org.tinytim.voc.TMDM;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;

/**
 * Utility functions to retrieve the supertypes / subtypes of a topic and
 * to check if a topic is an instance of another topic.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TypeInstanceUtils {

    private static final RolePlayerWalker _SUPERTYPES_WALKER = new RolePlayerWalker(TMDM.SUPERTYPE_SUBTYPE, TMDM.SUBTYPE, TMDM.SUPERTYPE);
    private static final RolePlayerWalker _SUBTYPES_WALKER = new RolePlayerWalker(TMDM.SUPERTYPE_SUBTYPE, TMDM.SUPERTYPE, TMDM.SUBTYPE);

    private TypeInstanceUtils() {
        // noop
    }

    /**
     * Returns if <tt>instance</tt> is an instance of <tt>type</tt>.
     * <p>
     * The typed construct is an instance of <tt>type</tt> if {@link Typed#getType()} 
     * is equal to the provided <tt>type</tt> or if <tt>type</tt> is a supertype
     * of {@link Typed#getType()}.
     * </p>
     *
     * @param instance The instance.
     * @param type The type.
     * @return <tt>true</tt> if the typed construct is an instance of <tt>type</tt>,
     *          otherwise <tt>false</tt>.
     */
    public static boolean isInstanceOf(Typed instance, Topic type) {
        if (instance == null) {
            throw new IllegalArgumentException("The instance must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        return instance.getType().equals(type) 
                || _SUPERTYPES_WALKER.isAssociated(instance.getType(), type); 
    }

    /**
     * Returns if <tt>instance</tt> is an instance of <tt>type</tt>.
     * <p>
     * The topic is an instance of <tt>type</tt> if {@link Topic#getTypes()} 
     * contains <tt>type</tt> or if one of the topics returned by {@link Topic#getTypes()}
     * is a subtype of <tt>type</tt>.
     * </p>
     *
     * @param instance The instance.
     * @param type The type.
     * @return <tt>true</tt> if the topic is an instance of <tt>type</tt>, 
     *          otherwise <tt>false</tt>.
     */
    public static boolean isInstanceOf(Topic instance, Topic type) {
        if (instance == null) {
            throw new IllegalArgumentException("The instance must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        Collection<Topic> types = instance.getTypes();
        if (types.contains(type)) {
            return true;
        }
        for (Topic topicType: types) {
            if (_SUPERTYPES_WALKER.isAssociated(topicType, type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the supertypes of <tt>subtype</tt>.
     * <p>
     * If <tt>subtype</tt> does not participate in a supertype-subtype association,
     * the returned collection is empty.
     * </p>
     *
     * @param subtype The subtype.
     * @return A (maybe empty) collection of supertypes.
     */
    public static Collection<Topic> getSupertypes(Topic subtype) {
        if (subtype == null) {
            throw new IllegalArgumentException("The subtype must not be null");
        }
        return _SUPERTYPES_WALKER.walk(subtype);
    }

    /**
     * Returns the subtypes of <tt>supertype</tt>.
     * <p>
     * If <tt>supertype</tt> does not participate in a supertype-subtype association,
     * the returned collection is empty.
     * </p>
     *
     * @param supertype The supertype.
     * @return A (maybe empty) collection of subtypes.
     */
    public static Collection<Topic> getSubtypes(Topic supertype) {
        if (supertype == null) {
            throw new IllegalArgumentException("The supertype must not be null");
        }
        return _SUBTYPES_WALKER.walk(supertype);
    }


    private static class RolePlayerWalker {

        private final Locator _assocTypeLoc;
        private final Locator _rolePlayingTypeLoc;
        private final Locator _otherRoleTypeLoc;

        public RolePlayerWalker(Locator associationType, Locator rolePlayingType, Locator otherRoleType) {
            _assocTypeLoc = associationType;
            _rolePlayingTypeLoc = rolePlayingType;
            _otherRoleTypeLoc = otherRoleType;
        }

        /**
         * Walks through the association players and reports those which are 
         * playing the counterpart role.
         *
         * @param start The starting point.
         * @return A colleciton of topics.
         */
        public Set<Topic> walk(Topic start) {
            return _walk(start, null);
        }

        /**
         * Returns if <tt>start</tt> is associated with <tt>end</tt>.
         *
         * @param start The starting point.
         * @param end The end point.
         * @return <tt>true</tt> if the start topic is associated with the end topic.
         */
        public boolean isAssociated(Topic start, Topic end) {
            return _walk(start, end).contains(end);
        }

        /**
         * Walks through the players.
         *
         * @param start The starting topic, must not be <tt>null</tt>.
         * @param end The end topic, maybe <tt>null</tt>.
         * @return A collection of topics.
         */
        public Set<Topic> _walk(Topic start, Topic end) {
            final TopicMap tm = start.getTopicMap();
            final Topic assocType = tm.getTopicBySubjectIdentifier(_assocTypeLoc);
            final Topic rolePlayingType = tm.getTopicBySubjectIdentifier(_rolePlayingTypeLoc);
            final Topic otherRoleType = tm.getTopicBySubjectIdentifier(_otherRoleTypeLoc);
            if (assocType == null 
                    || rolePlayingType == null 
                    || otherRoleType == null) {
                return Collections.emptySet();
            }
            Set<Topic> players = new HashSet<Topic>();
            _walk(start, players, end, assocType, rolePlayingType, otherRoleType);
            return players;
        }

        private void _walk(Topic start, Set<Topic> result, Topic goal, Topic assocType, Topic rolePlayingType, Topic otherRoleType) {
            for (Role role: start.getRolesPlayed(rolePlayingType, assocType)) {
                IAssociation parent = (IAssociation) role.getParent();
                if (!parent.getScopeObject().isUnconstrained()) {
                    continue;
                }
                Set<Role> roles = role.getParent().getRoles();
                if (roles.size() != 2) {
                    continue;
                }
                for (Role r: roles) {
                    if (!r.getType().equals(otherRoleType)) {
                        continue;
                    }
                    Topic player = r.getPlayer();
                    if (goal != null && player.equals(goal)) {
                        result.add(goal);
                        // No need to walk further
                        return;
                    }
                    else if (!result.contains(player)) {
                        result.add(player);
                        _walk(player, result, goal, assocType, rolePlayingType, otherRoleType);
                    }
                }
            }
        }
    }


}
