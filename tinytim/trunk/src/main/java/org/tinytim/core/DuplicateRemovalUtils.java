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
import java.util.Map;

import org.tinytim.utils.ICollectionFactory;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;
import org.tmapi.index.TypeInstanceIndex;

/**
 * Removes duplicates from Topic Maps constructs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class DuplicateRemovalUtils {

    private DuplicateRemovalUtils() {
        // noop.
    }

    /**
     * Removes duplicate Topic Maps constructs from a topic map.
     *
     * @param topicMap The topic map to remove the duplicates from.
     */
    public static void removeDuplicates(TopicMap topicMap) {
        TopicMapImpl tm = (TopicMapImpl) topicMap;
        for (Topic topic: tm.getTopics()) {
            removeDuplicates(topic);
        }
        Map<Integer, Association> sig2Assoc = tm.getCollectionFactory().createMap();
        TypeInstanceIndex typeInstanceIdx = tm.getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        for (Topic type: typeInstanceIdx.getAssociationTypes()) {
            _removeDuplicateAssociations(sig2Assoc, typeInstanceIdx.getAssociations(type));
        }
    }

    private static void _removeDuplicateAssociations(Map<Integer, Association> sig2Assoc, Collection<Association> assocs) {
        sig2Assoc.clear();
        Association existing = null;
        Integer sig = null;
        for (Association assoc: assocs) {
            removeDuplicates(assoc);
            sig = SignatureGenerator.generateSignature(assoc);
            existing = sig2Assoc.get(sig);
            if (existing != null) {
                MergeUtils.moveRoleCharacteristics(assoc, existing);
                assoc.remove();
            }
            else {
                sig2Assoc.put(sig, assoc);
            }
        }
    }

    /**
     * Removes duplicate occurrences and names from a topic.
     *
     * @param topic The topic from which duplicates should be removed from.
     */
    public static void removeDuplicates(Topic topic) {
        ICollectionFactory collFactory = ((TopicMapImpl) topic.getTopicMap()).getCollectionFactory();
        _removeDuplicateOccurrences(topic.getOccurrences(), collFactory);
        _removeDuplicateNames(topic.getNames(), collFactory);
    }

    /**
     * Removes duplicate variants from a name.
     *
     * @param name The name from which the duplicates should be removed.
     */
    public static void removeDuplicates(Name name) {
        Map<Integer, Variant> sigs = ((TopicMapImpl) name.getTopicMap()).getCollectionFactory().createMap();
        Integer sig = null;
        for (Variant variant: new ArrayList<Variant>(name.getVariants())) {
            sig = SignatureGenerator.generateSignature(variant);
            Variant existing = sigs.get(sig);
            if (existing != null) {
                MergeUtils.handleExistingConstruct(variant, existing);
                variant.remove();
            }
            else {
                sigs.put(sig, variant);
            }
        }
    }

    /**
     * 
     *
     * @param occs
     */
    private static void _removeDuplicateOccurrences(Collection<Occurrence> occs, ICollectionFactory collFactory) {
        Map<Integer, Occurrence> sigs = collFactory.createMap(occs.size());
        Occurrence existing = null;
        Integer sig = null;
        for (Occurrence occ: new ArrayList<Occurrence>(occs)) {
            sig = SignatureGenerator.generateSignature(occ);
            existing = sigs.get(sig);
            if (existing != null) {
                MergeUtils.handleExistingConstruct(occ, existing);
                occ.remove();
            }
            else {
                sigs.put(sig, occ);
            }
        }
    }

    /**
     * 
     *
     * @param names
     */
    private static void _removeDuplicateNames(Collection<Name> names, ICollectionFactory collFactory) {
        Map<Integer, Name> sigs = collFactory.createMap(names.size());
        Name existing = null;
        Integer sig = null;
        for (Name name: new ArrayList<Name>(names)) {
            removeDuplicates(name);
            sig = SignatureGenerator.generateSignature(name);
            existing = sigs.get(sig);
            if (existing != null) {
                MergeUtils.handleExistingConstruct(name, existing);
                MergeUtils.moveVariants(name, existing);
                name.remove();
            }
            else {
                sigs.put(sig, name);
            }
        }
    }

    /**
     * Removes duplicate roles from an association.
     *
     * @param assoc The association to remove duplicate roles from.
     */
    public static void removeDuplicates(Association assoc) {
        Map<Integer, Role> sig2Role = ((TopicMapImpl) assoc.getTopicMap()).getCollectionFactory().createMap();
        Role existing = null;
        Integer sig = null;
        for (Role role: new ArrayList<Role>(assoc.getRoles())) {
            sig = SignatureGenerator.generateSignature(role);
            existing = sig2Role.get(sig);
            if (existing != null) {
                MergeUtils.handleExistingConstruct(role, existing);
                role.remove();
            }
            else {
                sig2Role.put(sig, role);
            }
        }
    }

}
