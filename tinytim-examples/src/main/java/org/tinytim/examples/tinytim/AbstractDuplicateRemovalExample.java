/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package org.tinytim.examples.tinytim;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * Base class for the duplicate removal examples. 
 * 
 * Sets up the topic map system and provides some utility methods.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class AbstractDuplicateRemovalExample {

    protected TopicMapSystem _system;
    protected TopicMap _topicMap;
    protected Locator _tmIRI;

    /**
     * Creates a topic with the item identifier "a-topic"
     */
    protected Topic createTopic() throws TMAPIException {
        final Locator iid = resolve("a-topic");
        System.out.println("Creating a topic with the item identifier '" + iid.getReference() + "'");
        Topic topic = _topicMap.createTopicByItemIdentifier(iid);
        System.out.println("Created the topic");
        return topic;
    }

    /**
     * Returns a locator resolved against the base locator
     */
    protected Locator resolve(String localId) {
        return _tmIRI.resolve("#" + localId);
    }

    protected Topic createTopic(String localId) {
        return _topicMap.createTopicByItemIdentifier(resolve(localId));
    }

    protected Locator createLocator(String reference) {
        return _tmIRI.resolve(reference);
    }

    protected void runExample() throws TMAPIException {
        _system = TopicMapSystemFactory.newInstance().newTopicMapSystem();
        _tmIRI = _system.createLocator("http://tinytim.sourceforge/example/remove-duplicates");
        _topicMap = _system.createTopicMap(_tmIRI);
    }
}
