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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tinytim.index.IScopedIndex;
import org.tinytim.index.ITypeInstanceIndex;
import org.tinytim.index.IndexManager;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * This class does provides functions to merge topic maps and topics.
 * 
 * This class relies on the implementation of tinyTiM, if the implementation
 * changes, check the <code>==</code> comparisons.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class MergeUtils {

    private MergeUtils() {
        // noop.
    }

    /**
     * Merges two topic maps.
     *
     * @param source The source topic map.
     * @param target The target topic map which receives all 
     *                  topics / associations from <code>source</code>.
     */
    public static void merge(TopicMap source, TopicMap target) {
        CopyUtils.copy(source, target);
    }

    /**
     * Merges two topics.
     * 
     * The topics MUST belong to the same topic map. The <code>source</code>
     * will be removed from the topic map and <code>target</code> takes all
     * characteristics of the <code>source</code>.
     *
     * @param source The source topic.
     * @param target The target topic which receives all characteristics from
     *                  <code>source</code>.
     */
    public static void merge(Topic source, Topic target) {
        _merge((TopicImpl) source, (TopicImpl) target);
    }

    /**
     * @see #merge(Topic, Topic)
     */
    private static void _merge(TopicImpl source, TopicImpl target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Neither the source topic nor the target topic must be null");
        }
        if (source == target) {
            return;
        }
        if (source.getTopicMap() != target.getTopicMap()) {
            throw new IllegalArgumentException("The topics must belong to the same topic map");
        }
        IReifiable sourceReifiable = source._reified;
        if (sourceReifiable != null && target._reified != null) {
            // This should be enforced by the model
            assert sourceReifiable != target._reified;
            throw new ModelConstraintException(target, "The topics cannot be merged. They reify different Topic Maps constructs");
        }
        _moveItemIdentifiers((IConstruct)source, (IConstruct)target);
        if (sourceReifiable != null) {
            sourceReifiable.setReifier(target);
        }
        List<Locator> locs = new ArrayList<Locator>(source.getSubjectIdentifiers());
        for (Locator sid: locs) {
            source.removeSubjectIdentifier(sid);
            target.addSubjectIdentifier(sid);
        }
        locs = new ArrayList<Locator>(source.getSubjectLocators());
        for (Locator slo: locs) {
            source.removeSubjectLocator(slo);
            target.addSubjectLocator(slo);
        }
        _replaceTopics(source, target);
        for(Topic type: source.getTypes()) {
            target.addType(type);
        }
        Map<String, IReifiable> sigs = ((TopicMapImpl) source.getTopicMap()).getCollectionFactory().<String, IReifiable>createMap();
        for (Occurrence occ: target.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), (IReifiable)occ);
        }
        IReifiable existing = null;
        for (Occurrence occ: new ArrayList<Occurrence>(source.getOccurrences())) {
            existing = sigs.get(SignatureGenerator.generateSignature(occ));
            if (existing != null) {
                handleExistingConstruct((IReifiable) occ, existing);
                removeConstruct((IConstruct)occ);
            }
            else {
                source.removeOccurrence(occ);
                target.addOccurrence(occ);
            }
        }
        sigs.clear();
        for (TopicName name: target.getTopicNames()) {
            sigs.put(SignatureGenerator.generateSignature(name), (IReifiable) name);
        }
        for (TopicName name: new ArrayList<TopicName>(source.getTopicNames())) {
            existing = sigs.get(SignatureGenerator.generateSignature(name));
            if (existing != null) {
                handleExistingConstruct((IReifiable) name, existing);
                moveVariants((TopicNameImpl)name, (TopicNameImpl) existing);
                removeConstruct((IConstruct) name);
            }
            else {
                source.removeName(name);
                target.addName(name);
            }
        }
        sigs.clear();
        for (AssociationRole role: target.getRolesPlayed()) {
            Association parent = role.getAssociation();
            sigs.put(SignatureGenerator.generateSignature(parent), (IReifiable) parent);
        }
        for (AssociationRole role: new ArrayList<AssociationRole>(source.getRolesPlayed())) {
            role.setPlayer(target);
            Association parent = role.getAssociation();
            existing = sigs.get(SignatureGenerator.generateSignature(parent));
            if (existing != null) {
                handleExistingConstruct((IReifiable)parent, existing);
                _moveRoleCharacteristics(parent, (Association)existing);
                removeConstruct((IConstruct)parent);
            }
        }
        removeConstruct(source);
    }

    /**
     * Removes a Topic Maps construct.
     * 
     * If the construct is not removable, a runtime exception is thrown.
     *
     * @param construct The construct to remove.
     */
    static void removeConstruct(IConstruct construct) {
        try {
            construct.remove();
        }
        catch (TMAPIException ex) {
            throw new TMAPIRuntimeException("Unexpected exception while Topic Maps construct removal", ex);
        }
    }

    /**
     * Moves role item identifiers and reifier from the <code>source</code> to
     * the <code>target</code>'s equivalent role.
     *
     * @param source The association to remove the characteristics from.
     * @param target The association which takes the role characteristics.
     */
    @SuppressWarnings("unchecked")
    private static void _moveRoleCharacteristics(Association source, Association target) {
        Map<String, AssociationRole> sigs = ((TopicMapImpl) target.getTopicMap()).getCollectionFactory().<String, AssociationRole>createMap();
        for (AssociationRole role: ((AssociationImpl)target).getAssociationRoles()) {
            sigs.put(SignatureGenerator.generateSignature(role), role);
        }
        List<AssociationRole> roles = new ArrayList<AssociationRole>(source.getAssociationRoles());
        for (AssociationRole role: roles) {
            handleExistingConstruct((IReifiable)role, (IReifiable)sigs.get(SignatureGenerator.generateSignature(role)));
            removeConstruct((IConstruct)role);
        }
    }

    /**
     * Moves the variants from <code>source</code> to <code>target</code>.
     *
     * @param source The name to take the variants from.
     * @param target The target to add the variants to.
     */
    static void moveVariants(TopicNameImpl source, TopicNameImpl target) {
        Map<String, Variant> sigs = ((TopicMapImpl) target.getTopicMap()).getCollectionFactory().<String, Variant>createMap();
        for (Variant var: target.getVariants()) {
            sigs.put(SignatureGenerator.generateSignature(var), var);
        }
        Variant existing = null;
        for (Variant var: new ArrayList<Variant>(source.getVariants())) {
            existing = sigs.get(SignatureGenerator.generateSignature(var));
            if (existing != null) {
                handleExistingConstruct((IReifiable) var, (IReifiable) existing);
                removeConstruct((IConstruct)var);
            }
            else {
                source.removeVariant(var);
                target.addVariant(var);
            }
        }
    }

    /**
     * Moves the item identifiers and reifier from <code>source</code> to 
     * <code>target</code>.
     * 
     * If the <code>source</code> is reified, the <code>target</code>'s reifier
     * is set to the source reifier unless the target is also reified.
     * If <code>source</code> and <code>target</code> are reified, the reifiers
     * are merged.
     *
     * @param source The source Topic Maps construct.
     * @param target The target Topic Maps construct.
     */
    static void handleExistingConstruct(IReifiable source, IReifiable target) {
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
     * Replaces the <code>source</code> topic with the <code>replacement</code>
     * everywhere where <code>source</code> is used as type or theme.
     *
     * @param source The topic to replace.
     * @param replacement The topic which replaces the <code>source</code>.
     */
    private static void _replaceTopics(Topic source, Topic replacement) {
        TopicMapImpl tm = (TopicMapImpl) replacement.getTopicMap();
        IndexManager idxMan = tm.getIndexManager();
        ITypeInstanceIndex typeInstanceIndex = idxMan.getTypeInstanceIndex();
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
        IScopedIndex scopedIndex = idxMan.getScopedIndex();
        if (!scopedIndex.isAutoUpdated()) {
            scopedIndex.reindex();
        }
        _replaceTopicAsTheme(scopedIndex.getAssociationsByTheme(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getOccurrencesByTheme(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getNamesByTheme(source), source, replacement);
        _replaceTopicAsTheme(scopedIndex.getVariantsByTheme(source), source, replacement);
        scopedIndex.close();
    }

    /**
     * Sets <code>replacement</code> as type of each typed Topic Maps construct.
     *
     * @param typedConstructs A collection of typed constructs.
     * @param replacement The type.
     */
    private static void _replaceTopicAsType(Collection<? extends ITyped> typedConstructs,
            Topic replacement) {
        for (ITyped typed: typedConstructs) {
            typed.setType(replacement);
        }
    }

    private static void _replaceTopicAsTheme(Collection<? extends IScoped> scopedCollection,
            Topic oldTheme, Topic newTheme) {
        for (IScoped scoped: scopedCollection) {
            scoped.removeTheme(oldTheme);
            scoped.addTheme(newTheme);
        }
    }

    /**
     * Moves the item identifiers from <code>source</code> to <code>target</code>.
     *
     * @param source The source to remove the item identifiers from.
     * @param target The target which get the item identifiers.
     */
    private static void _moveItemIdentifiers(IConstruct source, IConstruct target) {
        List<Locator> iids = new ArrayList<Locator>(source.getItemIdentifiers());
        for (Locator iid: iids) {
            source.removeItemIdentifier(iid);
            target.addItemIdentifier(iid);
        }
    }

}
