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
package org.tinytim.utils;

import java.util.Collection;

import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.IIntObjectMap;
import org.tinytim.internal.utils.MergeUtils;
import org.tinytim.internal.utils.SignatureGenerator;

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
        for (Topic topic: topicMap.getTopics()) {
            removeDuplicates(topic);
        }
        IIntObjectMap<Association> sig2Assoc = CollectionFactory.createIntObjectMap();
        TypeInstanceIndex typeInstanceIdx = ((IIndexManagerAware) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIdx.isAutoUpdated()) {
            typeInstanceIdx.reindex();
        }
        for (Topic type: typeInstanceIdx.getAssociationTypes()) {
            _removeDuplicateAssociations(sig2Assoc, typeInstanceIdx.getAssociations(type));
        }
    }

    /**
     * 
     *
     * @param sig2Assoc
     * @param assocs
     */
    private static void _removeDuplicateAssociations(IIntObjectMap<Association> sig2Assoc, Collection<Association> assocs) {
        sig2Assoc.clear();
        Association existing = null;
        for (Association assoc: assocs) {
            removeDuplicates(assoc);
            int sig = SignatureGenerator.generateSignature(assoc);
            existing = sig2Assoc.get(sig);
            if (existing != null) {
                MergeUtils.handleExistingConstruct(assoc, existing);
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
        _removeDuplicateOccurrences(topic.getOccurrences());
        _removeDuplicateNames(topic.getNames());
    }

    /**
     * Removes duplicate variants from a name.
     *
     * @param name The name from which the duplicates should be removed.
     */
    public static void removeDuplicates(Name name) {
        IIntObjectMap<Variant> sigs = CollectionFactory.createIntObjectMap();
        for (Variant variant: CollectionFactory.createList(name.getVariants())) {
            int sig = SignatureGenerator.generateSignature(variant);
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
    private static void _removeDuplicateOccurrences(Collection<Occurrence> occs) {
        IIntObjectMap<Occurrence> sigs = CollectionFactory.createIntObjectMap(occs.size());
        Occurrence existing = null;
        for (Occurrence occ: CollectionFactory.createList(occs)) {
            int sig = SignatureGenerator.generateSignature(occ);
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
    private static void _removeDuplicateNames(Collection<Name> names) {
        IIntObjectMap<Name> sigs = CollectionFactory.createIntObjectMap(names.size());
        Name existing = null;
        for (Name name: CollectionFactory.createList(names)) {
            removeDuplicates(name);
            int sig = SignatureGenerator.generateSignature(name);
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
        IIntObjectMap<Role> sig2Role = CollectionFactory.createIntObjectMap();
        Role existing = null;
        for (Role role: CollectionFactory.createList(assoc.getRoles())) {
            int sig = SignatureGenerator.generateSignature(role);
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
