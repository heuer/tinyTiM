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

import org.tinytim.utils.DuplicateRemovalUtils;

import org.tmapi.core.Name;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;

/**
 * This example shows how to use the {@DuplicateRemovalUtils} and the effects of the
 * DuplicateRemovalUtils on topic names.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class RemoveDuplicateNames extends AbstractDuplicateRemovalExample {

    public static void main(String[] args) {
        RemoveDuplicateNames example = new RemoveDuplicateNames();
        try {
            example.runExample();
        }
        catch (TMAPIException ex) {
            ex.printStackTrace();
        }
    }

    public void runExample() throws TMAPIException {
        super.runExample();
        Topic topic = createTopic();
        final String value = "tinyTiM";
        System.out.println("Creating a name: '" + value + "'");
        topic.createName(value);
        System.out.println("Created the name.");
        System.out.println("Creating another name with the same value: '" + value + "'");
        topic.createName(value);
        System.out.println("Created the name.");
        System.out.println("The topic has " + topic.getNames().size() + " names:");
        for (Name name: topic.getNames()) {
            System.out.println("- " + name.getValue());
        }
        System.out.println("=> The topic has two names with the value '" + value + "' (duplicates)");
        System.out.println("Invoking " + DuplicateRemovalUtils.class.getName() + ".removeDuplicates(topic)");
        DuplicateRemovalUtils.removeDuplicates(topic);
        System.out.println("The topic has " + topic.getNames().size() + " name:");
        for (Name name: topic.getNames()) {
            System.out.println("- " + name.getValue());
        }
        System.out.println("=> The topic has no duplicate names");
    }

}
