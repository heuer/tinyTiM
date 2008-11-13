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
package org.tinytim.internal.api;

/**
 * Provides constants.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IConstant {
    /**
     * Initial size of the {@link MemoryTopicMap} topic set
     */
    public static final int TM_TOPIC_SIZE = 100;
    /**
     * Initial size of the {@link MemoryTopicMap} association set
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
     * Initial size of the {@link MemoryTopicMapSystem} topic map map.
     */
    public static final int SYSTEM_TM_SIZE = 4;
}
