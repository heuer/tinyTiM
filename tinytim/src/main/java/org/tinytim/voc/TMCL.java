/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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
 * Constants for TMCL PSIs.
 * <p>
 * CAUTION: These PSIs are not stable yet.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hannes Niederhausen
 * @version $Rev$ - $Date$
 */
public final class TMCL extends Vocabulary {

    private TMCL() {
        // noop.
    }

    private static final String _BASE = Namespace.TMCL;

    /**
     * Core concept of a constraint.
     * 
     * Used as constraint (topic) type.
     */
    public static final Locator CONSTRAINT = _createLocator(_BASE + "constraint");

    /*
     *  Topic types
     *  
     *  c.f. 6. TMCL Declarations
     */
    
    /**
     * Indicates that a topic may be used as a topic type.
     *
     * Used as topic type.
     */
    public static final Locator TOPIC_TYPE = _createLocator(_BASE + "topic-type");

    /**
     * Indicates that a topic may be used as association type.
     *
     * Used as topic type.
     */
    public static final Locator ASSOCIATION_TYPE = _createLocator(_BASE + "association-type");

    /**
     * Indicates that a topic may be used as role type.
     *
     * Used as topic type.
     */
    public static final Locator ROLE_TYPE = _createLocator(_BASE + "role-type");

    /**
     * Indicates that a topic may be used as occurrence type.
     *
     * Used as topic type.
     */
    public static final Locator OCCURRENCE_TYPE = _createLocator(_BASE + "occurrence-type");

    /**
     * Indicates that a topic may be used as name type.
     *
     * Used as topic type.
     */
    public static final Locator NAME_TYPE = _createLocator(_BASE + "name-type");

    /**
     * The tmcl:overlap-declaration is used to declare that the sets of 
     * instances of two or more topic types are non-disjoint (that is, that 
     * they may overlap). 
     * The default is that the instance sets of different topic types are 
     * disjoint.
     * 
     * Used as topic type.
     */
    public static final Locator OVERLAP_DECLARATION = _createLocator(_BASE + "overlap-declaration");

    @Deprecated
    //FIXME: What's the replacement for this PSI?
    public static final Locator SCOPE_TYPE = _createLocator(_BASE + "scope-type");
    
    
    /*
     * Constraint types
     * c.f. 7 TMCL Constraint Types
     */
    
    /**
     * The tmcl:abstract-constraint provides a way to express that a given topic 
     * type must not have any direct instances
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.2 Abstract Topic Type Constraint
     */
    public static final Locator ABSTRACT_CONSTRAINT = _createLocator(_BASE + "abstract-constraint");

    /**
     * Use {@link #ABSTRACT_CONSTRAINT}
     */
    @Deprecated
    public static final Locator ABSTRACT_TOPIC_TYPE_CONSTRAINT = ABSTRACT_CONSTRAINT;
    
    /**
     * 
     * 
     * Used as association type.
     * 
     * See 7.2 Abstract Topic Type Constraint
     */
    public static final Locator CONSTRAINED_TOPIC_TYPE = _createLocator(_BASE + "constrained-topic-type");

    /**
     * A subject identifier constraint provides a way to constrain the subject 
     * identifiers of instances of a given topic type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.3 Subject Identifier Constraint
     */
    public static final Locator SUBJECT_IDENTIFIER_CONSTRAINT = _createLocator(_BASE + "subject-identifier-constraint");

    /**
     * A subject locator constraint provides a way to constrain the subject 
     * locators of instances of a given topic type. 
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.4 Subject Locator Constraint
     */
    public static final Locator SUBJECT_LOCATOR_CONSTRAINT = _createLocator(_BASE + "subject-locator-constraint");
    
    
    /**
     * A topic name constraint provides a way to constrain the type and 
     * cardinality of topic names for instances of a given topic type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.5 Topic Name Constraint
     */
    public static final Locator TOPIC_NAME_CONSTRAINT = _createLocator(_BASE + "topic-name-constraint");

    /**
     * A topic occurrence constraint defines a way to constrain the type and 
     * cardinality of occurrences connected to a topic of a given type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.6 Topic Occurrence Constraint
     */
    public static final Locator TOPIC_OCCURRENCE_CONSTRAINT = _createLocator(_BASE + "topic-occurrence-constraint");

    /**
     * A topic role constraint constrains the types of roles topics of a given 
     * type can play in associations of a given type. 
     * It can also be seen as constraining the types of topics which may play 
     * roles of a given type in associations of a given type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.7 Topic Role Constraint
     */
    public static final Locator TOPIC_ROLE_CONSTRAINT = _createLocator(_BASE + "topic-role-constraint");

    /**
     * Use {@link #TOPIC_ROLE_CONSTRAINT}.
     */
    @Deprecated
    public static final Locator ROLE_PLAYER_CONSTRAINT = TOPIC_ROLE_CONSTRAINT;

    /**
     * Constrains the types of topics which may appear in the scope of a name, 
     * occurrence, or association of a particular type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.8 Scope Constraint.
     */
    public static final Locator SCOPE_CONSTRAINT = _createLocator(_BASE + "scope-constraint");

    /**
     * Constrains whether or not names, occurrence, and associations of a given 
     * type may be reified, and if so, what the type of the reifying topic must 
     * be.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.9 Reifier Constraint
     */
    public static final Locator REIFIER_CONSTRAINT = _createLocator(_BASE + "reifier-constraint");

    /**
     * Constrains what types of statements topics of a given type may reify.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.10 Topic Reifies Constraint
     */
    public static final Locator TOPIC_REIFIES_CONSTRAINT = _createLocator(_BASE + "topic-reifies-constraint");

    /**
     * Constrains the number of roles of a particular type that may appear in 
     * associations of a given type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.11 Association Role Constraint
     */
    public static final Locator ASSOCIATION_ROLE_CONSTRAINT = _createLocator(_BASE + "association-role-constraint");

    /**
     * Provides a way to restrict which combinations of topic types are allowed 
     * to appear in associations of a certain type together.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.12 Role Combination Constraint
     */
    public static final Locator ROLE_COMBINATION_CONSTRAINT = _createLocator(_BASE + "role-combination-constraint");

    /**
     * Provides a way to constrain the allowed datatype of an occurrence of a 
     * given type.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.13 Occurrence Data Type Constraint.
     */
    public static final Locator OCCURRENCE_DATATYPE_CONSTRAINT = _createLocator(_BASE + "occurrence-datatype-constraint");

    /**
     * Provides a way to require all names or occurrences of a given type to 
     * have different values.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.14 Unique Value Constraint
     */
    public static final Locator UNIQUE_VALUE_CONSTRAINT = _createLocator(_BASE + "unique-value-constraint");

    /**
     * Provides a mechanism for requiring that all values of a given name or 
     * occurrence type must match a given regular expression.
     * 
     * Used as constraint (topic) type.
     * 
     * See 7.15 Regular Expression Constraint
     */
    public static final Locator REGULAR_EXPRESSION_CONSTRAINT = _createLocator(_BASE + "regular-expression-constraint");

    
    // Role types
    /**
     * 
     * Used as role type.
     */
    public static final Locator ALLOWS = _createLocator(_BASE + "allows");
    
    /**
     * 
     * Used as role type.
     */
    public static final Locator ALLOWED = _createLocator(_BASE + "allowed");
    
    /**
     * 
     * Used as role type.
     */
    public static final Locator CONSTRAINS = _createLocator(_BASE + "constrains");
    
    /**
     *
     * Used as role type.
     */
    public static final Locator CONSTRAINED = _createLocator(_BASE + "constrained");
    
    /**
     * 
     * Used as role type.
     */
    public static final Locator CONTAINER = _createLocator(_BASE + "container");
    
    /**
     * 
     * Used as role type.
     */
    public static final Locator CONTAINEE = _createLocator(_BASE + "containee");
    


    /*
     *  Association types 
     */

    /**
     * 
     * Used as association type.
     */
    public static final Locator CONSTRAINED_STATEMENT = _createLocator(_BASE + "constrained-statement");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator CONSTRAINED_ROLE = _createLocator(_BASE + "constrained-role");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator OVERLAPS = _createLocator(_BASE + "overlaps");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator ALLOWED_SCOPE = _createLocator(_BASE + "allowed-scope");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator ALLOWED_REIFIER = _createLocator(_BASE + "allowed-reifier");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator OTHER_CONSTRAINED_TOPIC_TYPE = _createLocator(_BASE + "other-constrained-topic-type");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator OTHER_CONSTRAINED_ROLE = _createLocator(_BASE + "other-constrained-role");
    
    /**
     * 
     * Used as association type.
     */
    public static final Locator BELONGS_TO_SCHEMA = _createLocator(_BASE + "belongs-to-schema");
    
    // Model topics
    
    public static final Locator VALIDATION_EXPRESSION = _createLocator(_BASE + "validation-expression");

    /**
     * Indicates a minimum cardinality. 
     * 
     * Used as occurrence type.
     */
    public static final Locator CARD_MIN = _createLocator(_BASE + "card-min");

    /**
     * Indicates a maximum cardinality. 
     * 
     * Used as occurrence type.
     */
    public static final Locator CARD_MAX = _createLocator(_BASE + "card-max");

    /**
     * Used to define a regular expression.
     * 
     * Used as occurrence type.
     */
    public static final Locator REGEXP = _createLocator(_BASE + "regexp");

    /**
     * Used to define the datatype (an IRI) of an occurrence or variant.
     * 
     * Used as occurrence type.
     */
    public static final Locator DATATYPE = _createLocator(_BASE + "datatype");

    /*
     * 10 Schema Documentation.
     */
    
    /**
     * 
     * Used as topic type.
     * 
     * See 10.2 The Schema Topic
     */
    public static final Locator SCHEMA = _createLocator(_BASE + "schema");
    
    /**
     * Used to attach some identifier of the schema's version to the schema 
     * topic.
     * 
     * Used as occurrence type.
     * See 10.2 The Schema Topic
     */
    public static final Locator VERSION = _createLocator(_BASE + "version");
    
    
    /*
     * 10.3 Documentation Occurrences
     */
    
    /**
     * Used to attach a textual description of a TMCL construct to it inside 
     * the topic map.
     * 
     * Used as occurrence type.
     * 
     * See 10.3 Documentation Occurrences
     */
    public static final Locator DESCRIPTION = _createLocator(_BASE + "description");
    
    /**
     * Used to attach any textual information to a TMCL construct inside the 
     * topic map.
     * 
     * Used as occurrence type.
     * 
     * See 10.3 Documentation Occurrences
     */
    public static final Locator COMMENT = _createLocator(_BASE + "comment");
    
    /**
     * Used to attach a to a TMCL construct a reference to any kind of external 
     * information about that construct.
     * 
     * Used as occurrence type.
     * 
     * See 10.3 Documentation Occurrences
     */
    public static final Locator SEE_ALSO = _createLocator(_BASE + "see-also");

}
