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
import java.util.List;

import org.tinytim.index.IIndexManager;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.IIntObjectMap;
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
 * 
 * This class relies on the implementation of tinyTiM, if the implementation
 * changes, check the <tt>==</tt> comparisons.
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
        _merge((TopicImpl) source, (TopicImpl) target);
    }

    /**
     * @see #merge(Topic, Topic)
     */
    private static void _merge(TopicImpl source, Topic target) {
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
        IIntObjectMap<Reifiable> sigs = CollectionFactory.createIntObjectMap();
        for (Occurrence occ: target.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), occ);
        }
        Reifiable existing = null;
        for (Occurrence occ: new ArrayList<Occurrence>(source.getOccurrences())) {
            existing = sigs.get(SignatureGenerator.generateSignature(occ));
            if (existing != null) {
                handleExistingConstruct(occ, existing);
                occ.remove();
            }
            else {
                ((OccurrenceImpl) occ).moveTo(target);
            }
        }
        sigs.clear();
        for (Name name: target.getNames()) {
            sigs.put(SignatureGenerator.generateSignature(name), name);
        }
        for (Name name: new ArrayList<Name>(source.getNames())) {
            existing = sigs.get(SignatureGenerator.generateSignature(name));
            if (existing != null) {
                handleExistingConstruct(name, existing);
                moveVariants(name, (Name) existing);
                name.remove();
            }
            else {
                ((NameImpl) name).moveTo(target);
            }
        }
        sigs.clear();
        for (Role role: target.getRolesPlayed()) {
            Association parent = role.getParent();
            sigs.put(SignatureGenerator.generateSignature(parent), parent);
        }
        for (Role role: new ArrayList<Role>(source.getRolesPlayed())) {
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
        List<Role> roles = new ArrayList<Role>(source.getRoles());
        for (Role role: roles) {
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
        for (Variant var: new ArrayList<Variant>(source.getVariants())) {
            existing = sigs.get(SignatureGenerator.generateSignature(var));
            if (existing != null) {
                handleExistingConstruct(var, existing);
                var.remove();
            }
            else {
                ((VariantImpl) var).moveTo(target);
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
        TopicMapImpl tm = (TopicMapImpl) replacement.getTopicMap();
        IIndexManager idxMan = tm.getIndexManager();
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
        List<Locator> iids = new ArrayList<Locator>(source.getItemIdentifiers());
        for (Locator iid: iids) {
            source.removeItemIdentifier(iid);
            target.addItemIdentifier(iid);
        }
    }

}
