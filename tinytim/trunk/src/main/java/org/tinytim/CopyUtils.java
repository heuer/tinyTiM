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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * This class provides methods to copy Topic Maps constructs from one 
 * topic map to another without creating duplicates.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class CopyUtils {

    /**
     * Copies the topics and associations from the <code>source</code> to the
     * <code>target</code> topic map.
     *
     * @param source The topic map to take the topics and associations from.
     * @param target The topic map which should receive the topics and associations.
     */
    public static void copy(TopicMap source, TopicMap target) {
        _copy((TopicMapImpl) source, (TopicMapImpl) target);
    }

    /**
     * @see #copy(TopicMap, TopicMap)
     */
    @SuppressWarnings("unchecked")
    private static void _copy(TopicMapImpl source, TopicMapImpl target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Neither the source topic map nor the target topic map must be null");
        }
        if (source == target) {
            return;
        }
        Map<Topic, Topic> mergeMap = target.getCollectionFactory().createMap();
        Topic existing = null;
        TopicMapObject existingConstruct = null;
        for (Topic topic: source.getTopics()) {
            for (Iterator<Locator> iter = topic.getSubjectLocators().iterator(); iter.hasNext();) {
                existing = target.getTopicBySubjectLocator(iter.next());
                if (existing != null) {
                    _addMerge(topic, existing, mergeMap);
                }
            }
            for (Iterator<Locator> iter = topic.getSubjectIdentifiers().iterator(); iter.hasNext();) {
                Locator sid = iter.next();
                existing = target.getTopicBySubjectIdentifier(sid);
                if (existing != null) {
                    _addMerge(topic, existing, mergeMap);
                }
                existingConstruct = target.getObjectByItemIdentifier(sid);
                if (existingConstruct instanceof Topic) {
                    _addMerge(topic, (Topic) existingConstruct, mergeMap);
                }
            }
            for (Iterator<Locator> iter = topic.getSourceLocators().iterator(); iter.hasNext();) {
                Locator iid = iter.next();
                existingConstruct = target.getObjectByItemIdentifier(iid);
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
     * Copies the <code>topic</code> to the <code>target</code> topic map.
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
     * locators) from the <code>source/code> to the <code>targetTopic</code>.
     *
     * @param topic The topic to take the identities from.
     * @param targetTopic The topic which gets the identities.
     */
    @SuppressWarnings("unchecked")
    private static void _copyIdentities(Topic topic, Topic targetTopic) {
        for(Iterator<Locator> iter = topic.getSubjectIdentifiers().iterator(); iter.hasNext();) {
            targetTopic.addSubjectIdentifier(iter.next());
        }
        for(Iterator<Locator> iter = topic.getSubjectLocators().iterator(); iter.hasNext();) {
            targetTopic.addSubjectLocator(iter.next());
        }
        _copyItemIdentifiers((IConstruct)topic, (IConstruct)targetTopic);
    }

    /**
     * Copies the types from the <code>topic</code> to the <code>targetTopic</code>.
     *
     * @param topic The topic to take the types from.
     * @param targetTopic The topic which receives the types.
     * @param mergeMap The map which holds the merge mappings.
     */
    @SuppressWarnings("unchecked")
    private static void _copyTypes(Topic topic, Topic targetTopic,
            Map<Topic, Topic> mergeMap) {
        for (Iterator<Topic> iter = topic.getTypes().iterator(); iter.hasNext();) {
            Topic type = iter.next();
            Topic targetType = mergeMap.get(type);
            if (targetType == null) {
                targetType = _copyTopic(type, targetTopic.getTopicMap(), mergeMap);
            }
            targetTopic.addType(targetType);
        }
    }

    /**
     * Copies the occurrences and names from <code>topic</code> to the 
     * <code>targetTopic</code>.
     *
     * @param topic The topic to take the characteristics from.
     * @param targetTopic The target topic which gets the charateristics.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyCharacteristics(Topic topic, TopicImpl targetTopic,
            Map<Topic, Topic> mergeMap) {
        Map<String, IReifiable> sigs = ((TopicMapImpl) targetTopic.getTopicMap()).getCollectionFactory().<String, IReifiable>createMap();
        for (Occurrence occ: targetTopic.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), (IReifiable)occ);
        }
        IReifiable existing = null;
        for (Occurrence occ: ((TopicImpl) topic).getOccurrences()) {
            Occurrence targetOcc = targetTopic.createOccurrence((String)null, null, null);
            _copyType((ITyped)occ, (ITyped)targetOcc, mergeMap);
            _copyScope((IScoped)occ, (IScoped)targetOcc, mergeMap);
            if (occ.getValue() != null) {
                targetOcc.setValue(occ.getValue());
            }
            else if (occ.getResource() != null) {
                targetOcc.setResource(occ.getResource());
            }
            existing = sigs.get(SignatureGenerator.generateSignature(targetOcc));
            if (existing != null) {
                MergeUtils.removeConstruct((IConstruct) targetOcc);
                targetOcc = (Occurrence)existing;
            }
            _copyReifier((IReifiable) occ, (IReifiable) targetOcc, mergeMap);
            _copyItemIdentifiers((IConstruct) occ, (IConstruct) targetOcc);
        }
        sigs.clear();
        for (TopicName name: targetTopic.getTopicNames()) {
            sigs.put(SignatureGenerator.generateSignature(name), (IReifiable)name);
        }
        for (TopicName name: ((TopicImpl) topic).getTopicNames()) {
            TopicName targetName = targetTopic.createTopicName(name.getValue(), null);
            _copyType((ITyped) name, (ITyped) targetName, mergeMap);
            _copyScope((IScoped) name, (IScoped) targetName, mergeMap);
            existing = sigs.get(SignatureGenerator.generateSignature(targetName));
            if (existing != null) {
                MergeUtils.removeConstruct((IConstruct)targetName);
                targetName = (TopicName) existing;
            }
            _copyReifier((IReifiable) name, (IReifiable) targetName, mergeMap);
            _copyItemIdentifiers((IConstruct) name, (IConstruct) targetName);
            _copyVariants(name, targetName, mergeMap);
        }
    }

    /**
     * Copies the variants from <code>source</code> to the <code>target</code>.
     *
     * @param source The name to take the variants from.
     * @param target The target name which receives the variants.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyVariants(TopicName source, TopicName target,
            Map<Topic, Topic> mergeMap) {
        Map<String, Variant> sigs = ((TopicMapImpl) target.getTopicMap()).getCollectionFactory().createMap();
        for (Variant variant: ((TopicNameImpl) target).getVariants()) {
            sigs.put(SignatureGenerator.generateSignature(variant), variant);
        }
        Variant existing = null;
        for (Variant variant: ((TopicNameImpl) source).getVariants()) {
            Variant targetVar = target.createVariant((String) null, null);
            _copyScope((IScoped) variant, (IScoped) targetVar, mergeMap);
            if (variant.getValue() != null) {
                targetVar.setValue(variant.getValue());
            }
            else if (variant.getResource() != null) {
                targetVar.setResource(variant.getResource());
            }
            existing = sigs.get(SignatureGenerator.generateSignature(targetVar));
            if (existing != null) {
                MergeUtils.removeConstruct((IConstruct) targetVar);
                targetVar = existing;
            }
            _copyReifier((IReifiable) variant, (IReifiable) targetVar, mergeMap);
            _copyItemIdentifiers((IConstruct) variant, (IConstruct) targetVar);
        }
    }

    /**
     * Copies the reifier of <code>source</code> (if any) to the <code>target</code>.
     *
     * @param source The reifiable Topic Maps construct to take the reifier from.
     * @param target The target Topic Maps construct.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyReifier(IReifiable source, IReifiable target, 
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
     * Copies the type of the <code>source</code> (if any) to the <code>target</code>.
     *
     * @param source The Topic Maps construct to take the type from.
     * @param target The Topic Maps construct which receives the type.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyType(ITyped source, ITyped target, 
            Map<Topic, Topic> mergeMap) {
        Topic sourceType = source.getType();
        if (sourceType == null) {
            return;
        }
        Topic type = mergeMap.containsKey(sourceType) ? mergeMap.get(sourceType)
                            : _copyTopic(sourceType, target.getTopicMap(), mergeMap);
        target.setType(type);
    }

    /**
     * Copies all themes from the <code>source</code> scoped Topic Maps construct
     * to the <code>target</code>.
     *
     * @param source The source to take the scope from.
     * @param target The target which receives the scope.
     * @param mergeMap The map which holds the merge mappings.
     */
    private static void _copyScope(IScoped source, IScoped target, 
            Map<Topic, Topic> mergeMap) {
        Topic theme = null;
        for (Topic sourceTheme: source.getScope()) {
            theme = mergeMap.containsKey(sourceTheme) ? mergeMap.get(sourceTheme)
                            : _copyTopic(sourceTheme, target.getTopicMap(), mergeMap);
            target.addTheme(theme);
        }
    }

    /**
     * Copies the item identifiers from <code>source</code> to <code>target</code>.
     *
     * @param source The source Topic Maps construct.
     * @param target The target Topic Maps construct.
     */
    private static void _copyItemIdentifiers(IConstruct source, IConstruct target) {
        for(Locator iid: source.getItemIdentifiers()) {
            target.addSourceLocator(iid);
        }
    }

    /**
     * Copies the associations from the <code>source</code> topic map to the
     * <code>target</code> topic map.
     *
     * @param source The topic map to take the associations from.
     * @param target The topic map which receives the associations.
     * @param mergeMap The map which holds the merge mappings.
     */
    @SuppressWarnings("unchecked")
    private static void _copyAssociations(TopicMapImpl source, 
            TopicMapImpl target, Map<Topic, Topic> mergeMap) {
        Set<Association> assocs = target.getAssociations();
        Map<String, Association> sigs = target.getCollectionFactory().createMap(assocs.size());
        for (Association assoc: assocs) {
            sigs.put(SignatureGenerator.generateSignature(assoc), assoc);
        }
        Association existing = null;
        for (Association assoc: source.getAssociations()) {
            Association targetAssoc = target.createAssociation();
            _copyType((ITyped) assoc, (ITyped) targetAssoc, mergeMap);
            _copyScope((IScoped) assoc, (IScoped) targetAssoc, mergeMap);
            for (Iterator<AssociationRole> iter = assoc.getAssociationRoles().iterator(); iter.hasNext();) {
                AssociationRole role = iter.next();
                AssociationRole targetRole = targetAssoc.createAssociationRole(role.getPlayer(), role.getType());
                _copyItemIdentifiers((IConstruct)role, (IConstruct)targetRole);
                _copyReifier((IReifiable) role, (IReifiable) targetRole, mergeMap);
            }
            existing = sigs.get(SignatureGenerator.generateSignature(targetAssoc));
            if (existing != null) {
                MergeUtils.moveRoleCharacteristics(targetAssoc, existing);
                MergeUtils.removeConstruct((IConstruct) targetAssoc);
                targetAssoc = existing;
            }
            _copyReifier((IReifiable) assoc, (IReifiable) targetAssoc, mergeMap);
            _copyItemIdentifiers((IConstruct) assoc, (IConstruct) targetAssoc);
        }
    }

    /**
     * Adds a mapping from <code>source</code> to <code>target</code> into the
     * <code>mergeMap</code>.
     * 
     * If <code>source</code> has already a mapping to another target topic, 
     * <code>target</code> is merged with the existing target topic.
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
