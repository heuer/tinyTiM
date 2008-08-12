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
package org.tinytim.utils;

import java.util.Collection;
import java.util.logging.Logger;

import org.tinytim.core.TopicMapImpl;
import org.tinytim.voc.TMDM;
import org.tinytim.voc.XTM10;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

/**
 * This class provides functions that can be used to convert the type-instance
 * relationships that are modelled as associations into the [types] property
 * of {@link org.tmapi.core.Topic}s.
 * 
 * Copied from the TMAPIX-project.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TypeInstanceConverter {

    //TODO: Implement types -> associations

    private static final Logger LOG = Logger.getLogger(TypeInstanceConverter.class.getName());

    private TypeInstanceConverter() {
        // noop.
    }

    /**
     * Converts type-instance relationships (TMDM 1.0) and class-instance
     * relationships into the [types] property of the {@link org.tmapi.core.Topic}.
     * 
     * @see #convertTMDMAssociationsToTypes(TopicMap)
     * @see #convertXTMAssociationsToTypes(TopicMap)
     *
     * @param topicMap The topic map to convert.
     */
    public static void convertAssociationsToTypes(TopicMap topicMap) {
        convertTMDMAssociationsToTypes(topicMap);
        convertXTMAssociationsToTypes(topicMap);
    }

    /**
     * Converts class-instance relationships (XTM 1.0) into the [types] property
     * of {@link org.tmapi.core.Topic}s. The associations are removed from the 
     * topic map.
     *
     * @param topicMap The topic map to convert.
     */
    public static void convertXTMAssociationsToTypes(TopicMap topicMap) {
        _associationsToTypes(topicMap, XTM10.CLASS_INSTANCE,
                XTM10.CLASS, XTM10.INSTANCE);
    }

    /**
     * Converts type-instance relationships (TMDM 1.0) into the [types] property
     * of {@link org.tmapi.core.Topic}s. The associations are removed from the 
     * topic map.
     *
     * @param topicMap The topic map to convert.
     */
    public static void convertTMDMAssociationsToTypes(TopicMap topicMap) {
        _associationsToTypes(topicMap, TMDM.TYPE_INSTANCE,
                TMDM.TYPE, TMDM.INSTANCE);
    }

    private static void _associationsToTypes(final TopicMap topicMap, 
            final Locator typeInstance_, final Locator type_, final Locator instance_) {
        TopicMapImpl tm = (TopicMapImpl) topicMap;
        final Topic typeInstance = tm.getTopicBySubjectIdentifier(typeInstance_);
        if (typeInstance == null) {
            return;
        }
        final Topic type = tm.getTopicBySubjectIdentifier(type_);
        if (type == null) {
            return;
        }
        final Topic instance = tm.getTopicBySubjectIdentifier(instance_);
        if (instance == null) {
            return;
        }
        TypeInstanceIndex typeInstanceIdx = tm.getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        for (Association assoc: typeInstanceIdx.getAssociations(typeInstance)) {
            Topic[] pair = _getTypeInstancePair(assoc, type, instance);
            if (pair == null) {
                continue;
            }
            pair[1].addType(pair[0]);
            assoc.remove();
        }
    }

    private static void _info(Association assoc, String msg) {
        LOG.info("The association (ID: '" + assoc.getId() + "') cannot be converted into a type property. Reason: " + msg);
    }

    private static Topic[] _getTypeInstancePair(final Association assoc, final Topic type, final Topic instance) {
        Collection<Role> roles = assoc.getRoles();
        if (roles.size() != 2) {
            _info(assoc, "Not a binary association.");
            return null;
        }
        if (assoc.getReifier() != null) {
            _info(assoc, "It is reified");
            return null;
        }
        if (!assoc.getItemIdentifiers().isEmpty()) {
            _info(assoc, "It has item identifiers");
            return null;
        }
        if (!assoc.getScope().isEmpty()) {
            _info(assoc, "The scope is not unconstrained");
            return null;
        }
        Topic[] pair = new Topic[2];
        for (Role role: roles) {
            if (type.equals(role.getType())) {
                pair[0] = role.getPlayer();
            }
            else if (instance.equals(role.getType())) {
                pair[1] = role.getPlayer();
            }
        }
        if (pair[0] == null) {
            _info(assoc, "The type player is null.");
            return null;
        }
        if (pair[1] == null) {
            _info(assoc, "The instance player is null");
            return null;
        }
        return pair;
    }
}
