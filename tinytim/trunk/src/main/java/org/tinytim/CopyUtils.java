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

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;

/**
 * This class provides methods to copy Topic Maps constructs from one 
 * topic map to another without creating duplicates.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
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
    }

    private static Topic _copyTopic(Topic topic, TopicMap target,
            Map<Topic, Topic> mergeMap) {
        Topic targetTopic = target.createTopic();
        _copyIdentities(topic, targetTopic);
        _copyTypes(topic, targetTopic, mergeMap);
        _copyCharacteristics(topic, (TopicImpl)targetTopic, mergeMap);
        return targetTopic;
    }

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

    private static void _copyCharacteristics(Topic topic, TopicImpl targetTopic,
            Map<Topic, Topic> mergeMap) {
        Map<String, IReifiable> sigs = ((TopicMapImpl) targetTopic.getTopicMap()).getCollectionFactory().<String, IReifiable>createMap();
        for (Occurrence occ: targetTopic.getOccurrences()) {
            sigs.put(SignatureGenerator.generateSignature(occ), (IReifiable)occ);
        }
    }

    private static void _copyItemIdentifiers(IConstruct source, IConstruct target) {
        for(Locator iid: source.getItemIdentifiers()) {
            target.addSourceLocator(iid);
        }
    }

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
