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
package org.tinytim.core;

/**
 * Provides constants.
 * 
 * This interface is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
interface IConstant {
    /**
     * Initial size of the {@link TopicMapImpl} topic set
     */
    public static final int TM_TOPIC_SIZE = 100;
    /**
     * Initial size of the {@link TopicMapImpl} association set
     */
    public static final int TM_ASSOCIATION_SIZE = 100;
    /**
     * Initial size of the {@link ConstructImpl} item identifier set.
     */
    public static final int CONSTRUCT_IID_SIZE = 4;
    /**
     * Initial size of the {@link TopicImpl} subject identifier set.
     */
    public static final int TOPIC_SID_SIZE = 4;
    /**
     * Initial size of the {@link TopicImpl} subject locator set.
     */
    public static final int TOPIC_SLO_SIZE = 2;
    /**
     * Initial size of the {@link TopicImpl} types set.
     */
    public static final int TOPIC_TYPE_SIZE = 2;
    /**
     * Initial size of the {@link TopicImpl} name set.
     */
    public static final int TOPIC_NAME_SIZE = 2;
    /**
     * Initial size of the {@link TopicImpl} occurrence set.
     */
    public static final int TOPIC_OCCURRENCE_SIZE = 2;
    /**
     * Initial size of the {@link TopicImpl} roles-played set.
     */
    public static final int TOPIC_ROLE_SIZE = 2;
    /**
     * Initial size of the {@link AssociationImpl} roles set.
     */
    public static final int ASSOC_ROLE_SIZE = 2;
    /**
     * Initial size of the {@link NameImpl} variants set.
     */
    public static final int NAME_VARIANT_SIZE = 2;
    /**
     * Initial size of the {@link IdentityManager} id->construct map.
     */
    public static final int IDENTITY_ID2CONSTRUCT_SIZE = 200;
    /**
     * Initial size of the {@link IdentityManager} item identifier->construct map.
     */
    public static final int IDENTITY_IID2CONSTRUCT_SIZE = 50;
    /**
     * Initial size of the {@link IdentityManager} subject identifier->topic map.
     */
    public static final int IDENTITY_SID2TOPIC_SIZE = 50;
    /**
     * Initial size of the {@link IdentityManager} subject locator -> topic map.
     */
    public static final int IDENTITY_SLO2TOPIC_SIZE = 20;
    /**
     * Initial size of the {@link Literal}'s IRI registry. 
     */
    public static final int LITERAL_IRI_SIZE = 100;
    /**
     * Initial size of the {@link Literal}'s String registry. 
     */
    public static final int LITERAL_STRING_SIZE = 50;
    /**
     * Initial size of the {@link Literal}'s literal (!= String/IRI) registry. 
     */
    public static final int LITERAL_OTHER_SIZE = 50;
    /**
     * Initial size of the {@link Scope}'s scope registry. 
     */
    public static final int SCOPE_SCOPES_SIZE = 10;
    /**
     * Initial size of the {@link TopicMapSystemImpl} topic map map.
     */
    public static final int SYSTEM_TM_SIZE = 4;
}
