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
package org.tinytim.mio;

import java.io.ByteArrayOutputStream;

import org.tinytim.core.TinyTimTestCase;
import org.tmapi.core.Topic;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestJTMTopicMapWriter extends TinyTimTestCase {

    public void testWriting() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String base = "http://www.semagia.com/";
        TopicMapWriter writer = new JTMTopicMapWriter(out, base);
        _tm.createTopicByItemIdentifier(createLocator(base + "#iid"));
        Topic topic = createTopic();
        topic.addItemIdentifier(createLocator("http://www.sesssmsm.de/"));
        _tm.setReifier(topic);
        createAssociation();
        createRole();
        createRole();
        createRole();
        createRole();
        createVariant();
        createVariant();
        createName();
        writer.write(_tm);
        System.out.println(out.toString("utf-8"));
    }
}
