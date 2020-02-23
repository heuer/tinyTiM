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

import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;

/**
 * This example shows how to use the {@DuplicateRemovalUtils} and the effects of the
 * DuplicateRemovalUtils on occurrences.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class RemoveDuplicateOccurrences extends AbstractDuplicateRemovalExample {

    /**
     * 
     *
     * @param args
     */
    public static void main(String[] args) {
        RemoveDuplicateOccurrences example = new RemoveDuplicateOccurrences();
        try {
            example.runExample();
        }
        catch (TMAPIException ex) {
            ex.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.examples.tinytim.AbstractDuplicateRemovalExample#runExample()
     */
    public void runExample() throws TMAPIException {
        super.runExample();
        Topic topic = createTopic();
        final String website = "website";
        final String value = "http://tinytim.sourceforge.net/";
        System.out.println("Creating an occurrence with the type: '" + website + "' and the URI value '" + value + "'");
        createWebsite(topic, value);
        System.out.println("Created the occurrence.");
        System.out.println("Creating another occurrence with the type and value: '" + website + "' and the URI value '" + value + "'");
        createWebsite(topic, value);
        System.out.println("Created the occurrence.");
        System.out.println("The topic has " + topic.getOccurrences().size() + " occurrences:");
        for (Occurrence occ: topic.getOccurrences()) {
            System.out.println("* " + occ.getValue());
        }
        System.out.println("=> The topic has two occs with the value '" + value + "' (duplicates)");
        System.out.println("Invoking " + DuplicateRemovalUtils.class.getName() + ".removeDuplicates(topic)");
        DuplicateRemovalUtils.removeDuplicates(topic);
        System.out.println("The topic has " + topic.getOccurrences().size() + " occurrence:");
        for (Occurrence occ: topic.getOccurrences()) {
            System.out.println("* " + occ.getValue());
        }
        System.out.println("=> The topic has no duplicate occurrences");
    }

    /** 
     * Creates an occurrence of type "website"
     */
    private void createWebsite(Topic topic, String value) {
        topic.createOccurrence(createTopic("website"), createLocator(value));
    }

}
