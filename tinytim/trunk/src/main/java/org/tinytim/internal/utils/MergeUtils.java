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
package org.tinytim.internal.utils;

import java.util.Collection;
import java.util.List;

import org.tinytim.internal.api.IIndexManager;
import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.IVariant;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * This class provides functions to merge topic maps and topics.
 * <p>
 * This class is not meant to be used outside the tinyTiM package
 * </p>
 * <p>
 * This class relies on the implementation of tinyTiM, if the implementation
 * changes, check the <tt>==</tt> comparisons.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class MergeUtils {

    private MergeUtils() {
        // noop.
    }

    /**
     * Merges two topic maps.
     *
     * @param source The source topic map.
     * @param target The target topic map which receives all 
     *                  topics / associations from <tt>source</tt>.
     */
    public static void merge(TopicMap source, TopicMap target) {
        CopyUtils.copy(source, target);
    }

    /**
     * Merges two topics.
     * 
     * The topics MUST belong to the same topic map. The <tt>source</tt>
     * will be removed from the topic map and <tt>target</tt> takes all
     * characteristics of the <tt>source</tt>.
     *
     * @param source The source topic.
     * @param target The target topic which receives all characteristics from
     *                  <tt>source</tt>.
     */
    public static void merge(Topic source, Topic target) {
        _merge((ITopic) source, target);
    }

    /**
     * @see #merge(Topic, Topic)
     */
    private static void _merge(ITopic source, Topic target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Neither the source topic nor the target topic must be null");
        }
        if (source == target) {
            return;
        }
        if (source.getTopicMap() != target.getTopicMap()) {
            throw new IllegalArgumentException("The topics must belong to the same topic map");
        }
        Reifiable sourceReifiable = source.getReified();
        if (sourceReifiable != null && target.getReified() != null) {
            // This should be enforced by the model
            assert sourceReifiable != target.getReified();
            throw new ModelConstraintException(target, "The topics cannot be merged. They reify different Topic Maps constructs");
        }
        _moveItemIdentifiers(source, target);
        if (sourceReifiable != null) {
            sourceReifiable.setReifier(target);
        }
        List<Locator> locs = CollectionFactory.createList(source.getSubjectIdentifiers());
        for (Locator sid: locs) {
            source.removeSubjectIdentifier(sid);
            target.addSubjectIdentifier(sid);
        }
        locs = CollectionFactory.createList(source.getSubjectLocators());
        for (Locator slo: locs) {
            source.removeSubjectLocator(slo);
            target.addSubjectLocator(slo);
        }
        _replaceTopics(source, target);
        for(Topic type: source.getTypes()) {
            target.addType(type);
        }
        IIntObjectMap<Reifiable> sigs = CollectionFactory.createIntObjectMap();
        for (Occurrence occ: target.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), occ);
        }
        Reifiable existing = null;
        for (Occurrence occ: CollectionFactory.createList(source.getOccurrences())) {
            existing = sigs.get(SignatureGenerator.generateSignature(occ));
            if (existing != null) {
                handleExistingConstruct(occ, existing);
                occ.remove();
            }
            else {
                ((IOccurrence) occ).moveTo(target);
            }
        }
        sigs.clear();
        for (Name name: target.getNames()) {
            sigs.put(SignatureGenerator.generateSignature(name), name);
        }
        for (Name name: CollectionFactory.createList(source.getNames())) {
            existing = sigs.get(SignatureGenerator.generateSignature(name));
            if (existing != null) {
                handleExistingConstruct(name, existing);
                moveVariants(name, (Name) existing);
                name.remove();
            }
            else {
                ((IName) name).moveTo(target);
            }
        }
        sigs.clear();
        for (Role role: target.getRolesPlayed()) {
            Association parent = role.getParent();
            sigs.put(SignatureGenerator.generateSignature(parent), parent);
        }
        for (Role role: CollectionFactory.createList(source.getRolesPlayed())) {
            role.setPlayer(target);
            Association parent = role.getParent();
            existing = sigs.get(SignatureGenerator.generateSignature(parent));
            if (existing != null) {
                handleExistingConstruct(parent, existing);
                moveRoleCharacteristics(parent, (Association)existing);
                parent.remove();
            }
        }
        source.remove();
    }

    /**
     * Moves role item identifiers and reifier from the <tt>source</tt> to
     * the <tt>target</tt>'s equivalent role.
     *
     * @param source The association to remove the characteristics from.
     * @param target The association which takes the role characteristics.
     */
    public static void moveRoleCharacteristics(Association source, Association target) {
        IIntObjectMap<Role> sigs = CollectionFactory.createIntObjectMap();
        for (Role role: target.getRoles()) {
            sigs.put(SignatureGenerator.generateSignature(role), role);
        }
        for (Role role: CollectionFactory.createList(source.getRoles())) {
            handleExistingConstruct(role, sigs.get(SignatureGenerator.generateSignature(role)));
            role.remove();
        }
    }

    /**
     * Moves the variants from <tt>source</tt> to <tt>target</tt>.
     *
     * @param source The name to take the variants from.
     * @param target The target to add the variants to.
     */
    public static void moveVariants(Name source, Name target) {
        IIntObjectMap<Variant> sigs = CollectionFactory.createIntObjectMap();
        for (Variant var: target.getVariants()) {
            sigs.put(SignatureGenerator.generateSignature(var), var);
        }
        Variant existing = null;
        for (Variant var: CollectionFactory.createList(source.getVariants())) {
            existing = sigs.get(SignatureGenerator.generateSignature(var));
            if (existing != null) {
                handleExistingConstruct(var, existing);
                var.remove();
            }
            else {
                ((IVariant) var).moveTo(target);
            }
        }
    }

    /**
     * Moves the item identifiers and reifier from <tt>source</tt> to 
     * <tt>target</tt>.
     * 
     * If the <tt>source</tt> is reified, the <tt>target</tt>'s reifier
     * is set to the source reifier unless the target is also reified.
     * If <tt>source</tt> and <tt>target</tt> are reified, the reifiers
     * are merged.
     *
     * @param source The source Topic Maps construct.
     * @param target The target Topic Maps construct.
     */
    public static void handleExistingConstruct(Reifiable source, Reifiable target) {
        _moveItemIdentifiers(source, target);
        if (source.getReifier() == null) {
            return;
        }
        if (target.getReifier() != null) {
            Topic reifier = source.getReifier();
            source.setReifier(null);
            merge(reifier, target.getReifier());
        }
        else {
            Topic reifier = source.getReifier();
            source.setReifier(null);
            target.setReifier(reifier);
        }
    }

    /**
     * Replaces the <tt>source</tt> topic with the <tt>replacement</tt>
     * everywhere where <tt>source</tt> is used as type or theme.
     *
     * @param source The topic to replace.
     * @param replacement The topic which replaces the <tt>source</tt>.
     */
    private static void _replaceTopics(Topic source, Topic replacement) {
        IIndexManager idxMan = ((IIndexManagerAware) replacement.getTopicMap()).getIndexManager();
        TypeInstanceIndex typeInstanceIndex = idxMan.getTypeInstanceIndex();
        if (!typeInstanceIndex.isAutoUpdated()) {
            typeInstanceIndex.reindex();
        }
        for (Topic topic: typeInstanceIndex.getTopics(source)) {
            topic.removeType(source);
            topic.addType(replacement);
        }
        _replaceTopicAsType(typeInstanceIndex.getAssociations(source), replacement);
        _replaceTopicAsType(typeInstanceIndex.getRoles(source), replacement);
        _replaceTopicAsType(typeInstanceIndex.getOccurrences(source), replacement);
        _replaceTopicAsType(typeInstanceIndex.getNames(source), replacement);
        typeInstanceIndex.close();
        ScopedIndex scopedIndex = idxMan.getScopedIndex();
        if (!scopedIndex.isAutoUpdated()) {
            scopedIndex.reindex();
        }
        _replaceTopicAsTheme(scopedIndex.getAssociations(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getOccurrences(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getNames(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getVariants(source), source, replacement);
        scopedIndex.close();
    }

    /**
     * Sets <tt>replacement</tt> as type of each typed Topic Maps construct.
     *
     * @param typedConstructs A collection of typed constructs.
     * @param replacement The type.
     */
    private static void _replaceTopicAsType(Collection<? extends Typed> typedConstructs,
            Topic replacement) {
        for (Typed typed: typedConstructs) {
            typed.setType(replacement);
        }
    }

    /**
     * Replaces the <tt>oldTheme</tt> with the <tt>newTheme</tt> in each
     * scoped Topic Maps construct.
     *
     * @param scopedCollection A collection of scoped Topic Maps constructs.
     * @param oldTheme The old theme.
     * @param newTheme The theme that is used as replacement for <tt>oldTheme</tt>.
     */
    private static void _replaceTopicAsTheme(Collection<? extends Scoped> scopedCollection,
            Topic oldTheme, Topic newTheme) {
        for (Scoped scoped: scopedCollection) {
            scoped.removeTheme(oldTheme);
            scoped.addTheme(newTheme);
        }
    }

    /**
     * Moves the item identifiers from <tt>source</tt> to <tt>target</tt>.
     *
     * @param source The source to remove the item identifiers from.
     * @param target The target which get the item identifiers.
     */
    private static void _moveItemIdentifiers(Construct source, Construct target) {
        List<Locator> iids = CollectionFactory.createList(source.getItemIdentifiers());
        for (Locator iid: iids) {
            source.removeItemIdentifier(iid);
            target.addItemIdentifier(iid);
        }
    }

}
