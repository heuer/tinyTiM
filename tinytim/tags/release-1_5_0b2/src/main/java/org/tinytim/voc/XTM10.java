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
package org.tinytim.voc;

import org.tinytim.IRI;
import org.tmapi.core.Locator;

/**
 * Constants for XTM 1.0 PSIs.
 * 
 * The XTM 1.0 PSIs are outdated and have no relevance for the 
 * Topic Maps Data Model. These constants are provided for (de-)serializing
 * topic maps which depend on the XTM 1.0 "model" but they should not be
 * used for new topic maps, use {@link TMDM}.
 *
 * Copied from the Semagia MIO project.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class XTM10 {

    private static final String _BASE = "http://www.topicmaps.org/xtm/1.0/core.xtm#";

    /**
     * The core concept of class-instance; the class of association that 
     * represents class-instance relationships between topics, and that is 
     * semantically equivalent to the use of <instanceOf> subelements.
     */
    public static final Locator CLASS_INSTANCE = new IRI(_BASE + "class-instance");

    /**
     * The core concept of class; the role of class as played by one of the 
     * members of a class-instance association.
     */
    public static final Locator CLASS = new IRI(_BASE + "class");

    /**
     * The core concept of instance; the role of instance as played by one of 
     * the members of a class-instance association.
     */
    public static final Locator INSTANCE = new IRI(_BASE + "instance");

    /**
     * The core concept of superclass-subclass; the class of association that 
     * represents superclass-subclass relationships between topics.
     */
    public static final Locator SUPERCLASS_SUBCLASS = new IRI(_BASE + "superclass-subclass");

    /**
     * The core concept of superclass; the role of superclass as played by one 
     * of the members of a superclass-subclass association.
     */
    public static final Locator SUPERCLASS = new IRI(_BASE + "superclass");

    /**
     * The core concept of subclass; the role of subclass as played by one of 
     * the members of a superclass-subclass association.
     */
    public static final Locator SUBCLASS = new IRI(_BASE + "subclass");

    /**
     * The core concept of association; the generic class to which all 
     * associations belong unless otherwise specified.
     */
    public static final Locator ASSOCIATION = new IRI(_BASE + "association");

    /**
     * The core concept of occurrence; the generic class to which all 
     * occurrences belong unless otherwise specified.
     */
    public static final Locator OCCURRENCE = new IRI(_BASE + "occurrence");

    /**
     * Used to indicate that a variant can be used for sorting purposes.
     * Used as variant theme.
     */
    public static final Locator SORT = new IRI(_BASE + "sort");

    /**
     * Used to indicate that a variant can be used for displaying purposes.
     * Used as variant theme.
     */
    public static final Locator DISPLAY = new IRI(_BASE + "display");

}

