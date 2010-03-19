/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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

import java.util.logging.Logger;

import org.tinytim.voc.TMDM;
import org.tinytim.voc.XTM10;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * Utility functions to convert XTM 1.0 legacy features into the TMDM equivalent.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class XTM10Utils {

    private XTM10Utils() {
        // noop.
    }

    private static final Logger LOG = Logger.getLogger(XTM10Utils.class.getName());

    /**
     * Converts XTM 1.0 PSIs to TMDM PSIs and converts the XTM 1.0 
     * reification mechanism to TMDM reification.
     * 
     * @see #convertClassInstanceToTypeInstance(TopicMap)
     * @see #convertReification(TopicMap)
     *
     * @param topicMap
     */
    public static void convertToTMDM(final TopicMap topicMap) {
        convertXTM10PSIs(topicMap);
        convertReification(topicMap);
    }

    /**
     * Converts <tt>xtm10:class-instance</tt>, <tt>xtm10:class</tt>,  
     * <tt>xtm10:instance</tt>, <tt>xtm10:superclass-subclass</tt>, 
     * <tt>xtm10:superclass</tt>, <tt>xtm10:subclass</tt>, and 
     * <tt>xtm10:sort</tt> to the TMDM equivalent. 
     * 
     * The XTM 1.0 PSIs will be removed and replaced with the TMDM equivalent.
     *
     * @param topicMap The topic map to convert.
     */
    public static void convertXTM10PSIs(final TopicMap topicMap) {
        _replaceSubjectIdentifier(topicMap, XTM10.CLASS_INSTANCE, TMDM.TYPE_INSTANCE);
        _replaceSubjectIdentifier(topicMap, XTM10.CLASS, TMDM.TYPE);
        _replaceSubjectIdentifier(topicMap, XTM10.INSTANCE, TMDM.INSTANCE);
        _replaceSubjectIdentifier(topicMap, XTM10.SUPERCLASS_SUBCLASS, TMDM.SUPERTYPE_SUBTYPE);
        _replaceSubjectIdentifier(topicMap, XTM10.SUPERCLASS, TMDM.SUPERTYPE);
        _replaceSubjectIdentifier(topicMap, XTM10.SUBCLASS, TMDM.SUBTYPE);
        _replaceSubjectIdentifier(topicMap, XTM10.SORT, TMDM.SORT);
    }

    /**
     * Replaces the <tt>source</tt> subject identifier with the <tt>target</tt>.
     *
     * @param topicMap The topic map.
     * @param source The source PSI
     * @param target The target PSI.
     */
    private static void _replaceSubjectIdentifier(final TopicMap topicMap, final Locator source, final Locator target) {
        Topic topic = topicMap.getTopicBySubjectIdentifier(source);
        if (topic != null) {
            topic.addSubjectIdentifier(target);
            topic.removeSubjectIdentifier(source);
        }
    }

    /**
     * Converts the XTM 1.0 reification mechanism into TMDM reification.
     * <p>
     * In XTM 1.0 a construct was reified if it has a source locator (item 
     * identifier) equals to a subject identifier of a topic. In TMDM, the 
     * reification of constructs modelled as a property.
     * </p>
     * <p>
     * This function converts the XTM 1.0 reification mechansim into the TMDM
     * reification mechanism by setting the property of reified constructs to
     * the reifying topic. The item identifier and subject identifier which 
     * establish the XTM 1.0 reification will be removed.
     * </p> 
     *
     * @param topicMap The {@link TopicMap} to convert.
     */
    public static void convertReification(final TopicMap topicMap) {
        for (Topic topic: topicMap.getTopics()) {
            if (topic.getReified() != null) {
                continue;
            }
            for (Locator sid: topic.getSubjectIdentifiers()) {
                Construct construct = (Construct) topicMap.getConstructByItemIdentifier(sid);
                if (construct == null || construct instanceof Topic) {
                    continue;
                }
                Reifiable reifiable = (Reifiable) construct;
                if (reifiable.getReifier() != null) {
                    LOG.info("Skipping reifiable construct " + reifiable.getId() + " since it is reified");
                    continue;
                }
                reifiable.setReifier(topic);
                reifiable.removeItemIdentifier(sid);
                topic.removeSubjectIdentifier(sid);
                break;
            }
        }
    }

}
