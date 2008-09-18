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
package org.tinytim.voc;

import org.tmapi.core.Locator;

/**
 * Constants for XTM 1.0 PSIs.
 * <p>
 * The XTM 1.0 PSIs are outdated and have no relevance for the 
 * Topic Maps Data Model. These constants are provided for (de-)serializing
 * topic maps which depend on the XTM 1.0 "model" but they should not be
 * used for new topic maps, use {@link TMDM}.
 * </p>
 * Copied from the Semagia MIO project.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XTM10 extends Vocabulary {

    private static final String _BASE = "http://www.topicmaps.org/xtm/1.0/core.xtm#";

    /**
     * The core concept of class-instance; the class of association that 
     * represents class-instance relationships between topics, and that is 
     * semantically equivalent to the use of <instanceOf> subelements.
     */
    public static final Locator CLASS_INSTANCE = _createLocator(_BASE + "class-instance");

    /**
     * The core concept of class; the role of class as played by one of the 
     * members of a class-instance association.
     */
    public static final Locator CLASS = _createLocator(_BASE + "class");

    /**
     * The core concept of instance; the role of instance as played by one of 
     * the members of a class-instance association.
     */
    public static final Locator INSTANCE = _createLocator(_BASE + "instance");

    /**
     * The core concept of superclass-subclass; the class of association that 
     * represents superclass-subclass relationships between topics.
     */
    public static final Locator SUPERCLASS_SUBCLASS = _createLocator(_BASE + "superclass-subclass");

    /**
     * The core concept of superclass; the role of superclass as played by one 
     * of the members of a superclass-subclass association.
     */
    public static final Locator SUPERCLASS = _createLocator(_BASE + "superclass");

    /**
     * The core concept of subclass; the role of subclass as played by one of 
     * the members of a superclass-subclass association.
     */
    public static final Locator SUBCLASS = _createLocator(_BASE + "subclass");

    /**
     * The core concept of association; the generic class to which all 
     * associations belong unless otherwise specified.
     */
    public static final Locator ASSOCIATION = _createLocator(_BASE + "association");

    /**
     * The core concept of occurrence; the generic class to which all 
     * occurrences belong unless otherwise specified.
     */
    public static final Locator OCCURRENCE = _createLocator(_BASE + "occurrence");

    /**
     * Used to indicate that a variant can be used for sorting purposes.
     * Used as variant theme.
     */
    public static final Locator SORT = _createLocator(_BASE + "sort");

    /**
     * Used to indicate that a variant can be used for displaying purposes.
     * Used as variant theme.
     */
    public static final Locator DISPLAY = _createLocator(_BASE + "display");

}

