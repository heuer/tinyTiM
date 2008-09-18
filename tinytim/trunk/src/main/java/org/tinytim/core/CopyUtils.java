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

import java.util.Map;
import java.util.Set;

import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.internal.utils.IIntObjectMap;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

/**
 * This class provides methods to copy Topic Maps constructs from one 
 * topic map to another without creating duplicates.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class CopyUtils {

    private CopyUtils() {
        // noop.
    }

    /**
     * Copies the topics and associations from the <tt>source</tt> to the
     * <tt>target</tt> topic map.
     *
     * @param source The topic map to take the topics and associations from.
     * @param target The topic map which should receive the topics and associations.
     */
    public static void copy(TopicMap source, TopicMap target) {
        _copy(source, (TopicMapImpl) target);
    }

    /**
     * @see #copy(TopicMap, TopicMap)
     */
    private static void _copy(TopicMap source, TopicMapImpl target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Neither the source topic map nor the target topic map must be null");
        }
        if (source == target) {
            return;
        }
        Map<Topic, Topic> mergeMap = CollectionFactory.createIdentityMap();
        Topic existing = null;
        Construct existingConstruct = null;
        for (Topic topic: source.getTopics()) {
            for (Locator slo: topic.getSubjectLocators()) {
                existing = target.getTopicBySubjectLocator(slo);
                if (existing != null) {
                    _addMerge(topic, existing, mergeMap);
                }
            }
            for (Locator sid: topic.getSubjectIdentifiers()) {
                existing = target.getTopicBySubjectIdentifier(sid);
                if (existing != null) {
                    _addMerge(topic, existing, mergeMap);
                }
                existingConstruct = target.getConstructByItemIdentifier(sid);
                if (existingConstruct instanceof Topic) {
                    _addMerge(topic, (Topic) existingConstruct, mergeMap);
                }
            }
            for (Locator iid: topic.getItemIdentifiers()) {
                existingConstruct = target.getConstructByItemIdentifier(iid);
                if (existingConstruct instanceof Topic) {
                    _addMerge(topic, (Topic) existingConstruct, mergeMap);
                }
                existing = target.getTopicBySubjectIdentifier(iid);
                if (existing != null) {
                    _addMerge(topic, existing, mergeMap);
                }
            }
        }
        if (source.getReifier() != null && target.getReifier() != null) {
            _addMerge(source.getReifier(), target.getReifier(), mergeMap);
        }
        for (Topic topic: source.getTopics()) {
            if (!mergeMap.containsKey(topic)) {
                _copyTopic(topic, target, mergeMap);
            }
        }
        for (Topic topic: mergeMap.keySet()) {
            Topic targetTopic = mergeMap.get(topic);
            _copyIdentities(topic, targetTopic);
            _copyTypes(topic, targetTopic, mergeMap);
            _copyCharacteristics(topic, (TopicImpl)targetTopic, mergeMap);
        }
        _copyAssociations(source, target, mergeMap);
    }

    /**
     * Copies the <tt>topic</tt> to the <tt>target</tt> topic map.
     *
     * @param topic The topic to copy.
     * @param target The target topic map.
     * @param mergeMap The map which holds the merge mappings.
     * @return The newly created topic in the target topic map.
     */
    private static Topic _copyTopic(Topic topic, TopicMap target,
            Map<Topic, Topic> mergeMap) {
        Topic targetTopic = target.createTopic();
        _copyIdentities(topic, targetTopic);
        _copyTypes(topic, targetTopic, mergeMap);
        _copyCharacteristics(topic, (TopicImpl)targetTopic, mergeMap);
        return targetTopic;
    }

    /**
     * Copies the identities (item identifiers, subject identifiers and subject
     * locators) from the <tt>source/tt> to the <tt>targetTopic</tt>.
     *
     * @param topic The topic to take the identities from.
     * @param targetTopic The topic which gets the identities.
     */
    private static void _copyIdentities(Topic topic, Topic targetTopic) {
        for(Locator sid: topic.getSubjectIdentifiers()) {
            targetTopic.addSubjectIdentifier(sid);
        }
        for(Locator slo: topic.getSubjectLocators()) {
            targetTopic.addSubjectLocator(slo);
        }
        _copyItemIdentifiers(topic, targetTopic);
    }

    /**
     * Copies the types from the <tt>topic</tt> to the <tt>targetTopic</tt>.
     *
     * @param topic The topic to take the types from.
     * @param targetTopic The topic which receives the types.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyTypes(Topic topic, Topic targetTopic,
            Map<Topic, Topic> mergeMap) {
        for (Topic type: topic.getTypes()) {
            Topic targetType = mergeMap.get(type);
            if (targetType == null) {
                targetType = _copyTopic(type, targetTopic.getTopicMap(), mergeMap);
            }
            targetTopic.addType(targetType);
        }
    }

    /**
     * Copies the occurrences and names from <tt>topic</tt> to the 
     * <tt>targetTopic</tt>.
     *
     * @param topic The topic to take the characteristics from.
     * @param targetTopic The target topic which gets the charateristics.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyCharacteristics(Topic topic, TopicImpl targetTopic,
            Map<Topic, Topic> mergeMap) {
        IIntObjectMap<Reifiable> sigs = CollectionFactory.createIntObjectMap();
        for (Occurrence occ: targetTopic.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), occ);
        }
        Reifiable existing = null;
        final TopicMap tm = targetTopic.getTopicMap();
        Topic type = null;
        Set<Topic> scope = null;
        Occurrence targetOcc = null;
        for (Occurrence occ: topic.getOccurrences()) {
            type = _copyType(occ, tm, mergeMap);
            scope = _copyScope(occ, tm, mergeMap);
            targetOcc = targetTopic._createOccurrence(type, ((ILiteralAware) occ).getLiteral(), scope);
            existing = sigs.get(SignatureGenerator.generateSignature(targetOcc));
            if (existing != null) {
                targetOcc.remove();
                targetOcc = (Occurrence)existing;
            }
            _copyReifier(occ, targetOcc, mergeMap);
            _copyItemIdentifiers(occ, targetOcc);
        }
        sigs.clear();
        for (Name name: targetTopic.getNames()) {
            sigs.put(SignatureGenerator.generateSignature(name), name);
        }
        
        for (Name name: topic.getNames()) {
            type = _copyType(name, tm, mergeMap);
            scope = _copyScope(name, tm, mergeMap);
            Name targetName = targetTopic._createName(type, ((ILiteralAware) name).getLiteral(), scope);
            existing = sigs.get(SignatureGenerator.generateSignature(targetName));
            if (existing != null) {
                targetName.remove();
                targetName = (Name) existing;
            }
            _copyReifier(name, targetName, mergeMap);
            _copyItemIdentifiers(name, targetName);
            _copyVariants(name, (NameImpl)targetName, mergeMap);
        }
    }

    /**
     * Copies the variants from <tt>source</tt> to the <tt>target</tt>.
     *
     * @param source The name to take the variants from.
     * @param target The target name which receives the variants.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyVariants(Name source, NameImpl target,
            Map<Topic, Topic> mergeMap) {
        IIntObjectMap<Variant> sigs = CollectionFactory.createIntObjectMap();
        for (Variant variant: target.getVariants()) {
            sigs.put(SignatureGenerator.generateSignature(variant), variant);
        }
        final TopicMap tm = target.getTopicMap();
        Variant existing = null;
        Set<Topic> scope = null;
        for (Variant variant: source.getVariants()) {
            scope = _copyScope( variant, tm, mergeMap);
            Variant targetVar = target._createVariant(((ILiteralAware) variant).getLiteral(), scope);
            existing = sigs.get(SignatureGenerator.generateSignature(targetVar));
            if (existing != null) {
                targetVar.remove();
                targetVar = existing;
            }
            _copyReifier(variant, targetVar, mergeMap);
            _copyItemIdentifiers(variant, targetVar);
        }
    }

    /**
     * Copies the reifier of <tt>source</tt> (if any) to the <tt>target</tt>.
     *
     * @param source The reifiable Topic Maps construct to take the reifier from.
     * @param target The target Topic Maps construct.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyReifier(Reifiable source, Reifiable target, 
            Map<Topic, Topic> mergeMap) {
        Topic sourceReifier = source.getReifier();
        if (sourceReifier == null) {
            return;
        }
        Topic reifier = mergeMap.containsKey(sourceReifier) ? mergeMap.get(sourceReifier)
                            : _copyTopic(sourceReifier, target.getTopicMap(), mergeMap);
        target.setReifier(reifier);
    }

    /**
     * Copies the type of the <tt>source</tt> (if any) to the <tt>target</tt>.
     *
     * @param source The Topic Maps construct to take the type from.
     * @param target The Topic Maps construct which receives the type.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static Topic _copyType(Typed source, TopicMap tm,
                Map<Topic, Topic> mergeMap) {
        Topic sourceType = source.getType();
       return mergeMap.containsKey(sourceType) ? mergeMap.get(sourceType)
                            : _copyTopic(sourceType, tm, mergeMap);
    }

    /**
     * Copies all themes from the <tt>source</tt> scoped Topic Maps construct
     * to the <tt>target</tt>.
     *
     * @param source The source to take the scope from.
     * @param target The target which receives the scope.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static Set<Topic>_copyScope(Scoped source, TopicMap tm, 
            Map<Topic, Topic> mergeMap) {
        Set<Topic> themes = CollectionFactory.createIdentitySet(source.getScope().size());
        Topic theme = null;
        for (Topic sourceTheme: source.getScope()) {
            theme = mergeMap.containsKey(sourceTheme) ? mergeMap.get(sourceTheme)
                            : _copyTopic(sourceTheme, tm, mergeMap);
            themes.add(theme);
        }
        return themes;
    }

    /**
     * Copies the item identifiers from <tt>source</tt> to <tt>target</tt>.
     *
     * @param source The source Topic Maps construct.
     * @param target The target Topic Maps construct.
     */
    private static void _copyItemIdentifiers(Construct source, Construct target) {
        for(Locator iid: source.getItemIdentifiers()) {
            target.addItemIdentifier(iid);
        }
    }

    /**
     * Copies the associations from the <tt>source</tt> topic map to the
     * <tt>target</tt> topic map.
     *
     * @param source The topic map to take the associations from.
     * @param target The topic map which receives the associations.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyAssociations(TopicMap source, 
            TopicMapImpl target, Map<Topic, Topic> mergeMap) {
        Set<Association> assocs = target.getAssociations();
        IIntObjectMap<Association> sigs = CollectionFactory.createIntObjectMap(assocs.size());
        for (Association assoc: assocs) {
            sigs.put(SignatureGenerator.generateSignature(assoc), assoc);
        }
        Association existing = null;
        Topic type = null;
        Set<Topic> scope = null;
        for (Association assoc: source.getAssociations()) {
            type = _copyType(assoc, target, mergeMap);
            scope = _copyScope(assoc, target, mergeMap);
            Association targetAssoc = target.createAssociation(type, scope);
            for (Role role: assoc.getRoles()) {
                Role targetRole = targetAssoc.createRole(role.getType(), role.getPlayer());
                _copyItemIdentifiers(role, targetRole);
                _copyReifier(role, targetRole, mergeMap);
            }
            existing = sigs.get(SignatureGenerator.generateSignature(targetAssoc));
            if (existing != null) {
                MergeUtils.moveRoleCharacteristics(targetAssoc, existing);
                targetAssoc.remove();
                targetAssoc = existing;
            }
            _copyReifier(assoc, targetAssoc, mergeMap);
            _copyItemIdentifiers(assoc, targetAssoc);
        }
    }

    /**
     * Adds a mapping from <tt>source</tt> to <tt>target</tt> into the
     * <tt>mergeMap</tt>.
     * 
     * If <tt>source</tt> has already a mapping to another target topic, 
     * <tt>target</tt> is merged with the existing target topic.
     *
     * @param source The source topic.
     * @param target The target topic.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _addMerge(Topic source, Topic target, Map<Topic, Topic> mergeMap) {
        Topic prevTarget = mergeMap.get(source);
        if (prevTarget != null) {
            if (!prevTarget.equals(target)) {
                MergeUtils.merge(target, prevTarget);
            }
        }
        else {
            mergeMap.put(source, target);
        }
    }

}
