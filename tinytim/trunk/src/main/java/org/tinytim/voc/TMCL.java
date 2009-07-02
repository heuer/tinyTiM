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
 * These PSIs are not stable yet.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TMCL extends Vocabulary {

    private TMCL() {
        // noop.
    }

    private static final String _BASE = Namespace.TMCL;

    // Topic types
    
    public static final Locator TOPIC_TYPE = _createLocator(_BASE + "topic-type");

    public static final Locator ASSOCIATION_TYPE = _createLocator(_BASE + "association-type");

    public static final Locator ROLE_TYPE = _createLocator(_BASE + "role-type");

    public static final Locator OCCURRENCE_TYPE = _createLocator(_BASE + "occurrence-type");

    public static final Locator NAME_TYPE = _createLocator(_BASE + "name-type");

    public static final Locator SCOPE_TYPE = _createLocator(_BASE + "scope-type");


    // Role types
    public static final Locator ALLOWS = _createLocator(_BASE + "allows");
    
    public static final Locator ALLOWED = _createLocator(_BASE + "allowed");
    
    public static final Locator CONSTRAINS = _createLocator(_BASE + "constrains");
    
    public static final Locator CONSTRAINED = _createLocator(_BASE + "constrained");

    // Association types - applies-to is no more 
    public static final Locator CONSTRAINED_TOPIC_TYPE = _createLocator(_BASE + "constrained-topic-type");
   
    public static final Locator CONSTRAINED_STATEMENT = _createLocator(_BASE + "constrained-statement");
    
    public static final Locator CONSTRAINED_ROLE = _createLocator(_BASE + "constrained-role");
    
    public static final Locator OVERLAPS = _createLocator(_BASE + "overlaps");
    
    public static final Locator ALLOWED_SCOPE = _createLocator(_BASE + "allowed-scope");
    
    public static final Locator ALLOWED_REIFIER = _createLocator(_BASE + "allowed-reifier");
    
    public static final Locator OTHER_CONSTRAINED_TOPIC_TYPE = _createLocator(_BASE + "other-constrained-topic-type");
    
    public static final Locator OTHER_CONSTRAINED_ROLE = _createLocator(_BASE + "other-constrained-role");
    
    public static final Locator BELONGS_TO_SCHEMA = _createLocator(_BASE + "belongs-to-schema");
    
    // Model topics
    
    public static final Locator SCHEMA = _createLocator(_BASE + "schema");
    
    public static final Locator CONSTRAINT = _createLocator(_BASE + "constraint");

    public static final Locator VALIDATION_EXPRESSION = _createLocator(_BASE + "validation-expression");

    public static final Locator CARD_MIN = _createLocator(_BASE + "card-min");

    public static final Locator CARD_MAX = _createLocator(_BASE + "card-max");

    public static final Locator REGEXP = _createLocator(_BASE + "regexp");

    public static final Locator DATATYPE = _createLocator(_BASE + "datatype");
    
    public static final Locator VERSION = _createLocator(_BASE + "version");

    public static final Locator DESCRIPTION = _createLocator(_BASE + "description");
    
    public static final Locator COMMENT = _createLocator(_BASE + "comment");
    
    public static final Locator SEE_ALSO = _createLocator(_BASE + "see-also");

    // Constraint types
    
    public static final Locator ABSTRACT_TOPIC_TYPE_CONSTRAINT = _createLocator(_BASE + "abstract-constraint");
    
    public static final Locator OVERLAP_DECLARATION = _createLocator(_BASE + "overlap-declaration");
    
    public static final Locator SUBJECT_IDENTIFIER_CONSTRAINT = _createLocator(_BASE + "subject-identifier-constraint");

    public static final Locator SUBJECT_LOCATOR_CONSTRAINT = _createLocator(_BASE + "subject-locator-constraint");
    
    public static final Locator TOPIC_NAME_CONSTRAINT = _createLocator(_BASE + "topic-name-constraint");    
    
    public static final Locator TOPIC_OCCURRENCE_CONSTRAINT = _createLocator(_BASE + "topic-occurrence-constraint");
   
    public static final Locator ROLE_PLAYER_CONSTRAINT = _createLocator(_BASE + "role-player-constraint");
    
    public static final Locator SCOPE_CONSTRAINT = _createLocator(_BASE + "scope-constraint");
    
    public static final Locator REIFIER_CONSTRAINT = _createLocator(_BASE + "reifier-constraint");

    public static final Locator ASSOCIATION_ROLE_CONSTRAINT = _createLocator(_BASE + "association-role-constraint");
    
    public static final Locator OTHER_ROLE_CONSTRAINT = _createLocator(_BASE + "other-role-constraint");
    
    public static final Locator OCCURRENCE_DATATYPE_CONSTRAINT = _createLocator(_BASE + "occurrence-datatype-constraint");

    public static final Locator UNIQUE_VALUE_CONSTRAINT = _createLocator(_BASE + "unique-value-constraint");
    
    public static final Locator REGULAR_EXPRESSION_CONSTRAINT = _createLocator(_BASE + "regular-expression-constraint");
}
