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

import org.tmapi.core.Association;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
interface ITopicMap extends TopicMap, IEventHandler, IConstruct, IIndexManagerAware {

    /**
     * 
     *
     * @return
     */
    Topic getDefaultTopicNameType();

    void removeTopic(Topic topic);

    void removeAssociation(Association association);

    void addAssociation(Association assoc);

    /**
     * Returns a topic without any identity.
     * <p>
     * The topic won't have an item identifier, subject identifier, or subject
     * locator, just an internal identifier.
     * </p>
     *
     * @return A topic without any identity.
     */
    public Topic createTopicWithoutIdentity();

}
