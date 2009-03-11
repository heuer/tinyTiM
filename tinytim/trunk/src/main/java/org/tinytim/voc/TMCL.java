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
    
    public static final Locator TOPIC_TYPE = _createLocator(_BASE + "topictype");

    public static final Locator ASSOCIATION_TYPE = _createLocator(_BASE + "associationtype");

    public static final Locator ROLE_TYPE = _createLocator(_BASE + "roletype");

    public static final Locator OCCURRENCE_TYPE = _createLocator(_BASE + "occurrencetype");

    public static final Locator NAME_TYPE = _createLocator(_BASE + "nametype");

    public static final Locator SCOPE_TYPE = _createLocator(_BASE + "scopetype");


    // Role types

    public static final Locator TOPIC_TYPE_ROLE = _createLocator(_BASE + "topictype-role");

    public static final Locator ASSOCIATION_TYPE_ROLE = _createLocator(_BASE + "associationtype-role");

    public static final Locator ROLE_TYPE_ROLE = _createLocator(_BASE + "roletype-role");

    public static final Locator OTHERROLE_TYPE_ROLE = _createLocator(_BASE + "roletype-role");

    public static final Locator OCCURRENCE_TYPE_ROLE = _createLocator(_BASE + "occurrencetype-role");

    public static final Locator NAME_TYPE_ROLE = _createLocator(_BASE + "nametype-role");

    public static final Locator SCOPE_TYPE_ROLE = _createLocator(_BASE + "scopetype-role");

    public static final Locator CONSTRAINT_ROLE = _createLocator(_BASE + "constraint-role");


    // Model topics
    public static final Locator CONSTRAINT = _createLocator(_BASE + "constraint");

    public static final Locator VALIDATION_EXPRESSION = _createLocator(_BASE + "validation-expression");

    public static final Locator APPLIES_TO = _createLocator(_BASE + "applies-to");

    public static final Locator CARD_MIN = _createLocator(_BASE + "card-min");

    public static final Locator CARD_MAX = _createLocator(_BASE + "card-max");

    //TODO: TMCL uses sometimes "regexp" and sometimes "reg-exp"
    public static final Locator REGEXP = _createLocator(_BASE + "reg-exp");

    public static final Locator DATATYPE = _createLocator(_BASE + "datatype");


    // Constraint types
    public static final Locator TOPIC_TYPE_CONSTRAINT = _createLocator(_BASE + "topictype-constraint");

    public static final Locator ASSOCIATION_TYPE_CONSTRAINT = _createLocator(_BASE + "associationtype-constraint");

    public static final Locator ROLE_TYPE_CONSTRAINT = _createLocator(_BASE + "roletype-constraint");

    public static final Locator OCCURRENCE_TYPE_CONSTRAINT = _createLocator(_BASE + "occurrencetype-constraint");

    public static final Locator NAME_TYPE_CONSTRAINT = _createLocator(_BASE + "nametype-constraint");

    public static final Locator ABSTRACT_TOPIC_TYPE_CONSTRAINT = _createLocator(_BASE + "abstract-topictype-constraint");
    
    public static final Locator EXCLUSIVE_INSTANCE = _createLocator(_BASE + "exclusive-instance");

    public static final Locator SUBJECT_IDENTIFIER_CONSTRAINT = _createLocator(_BASE + "subjectidentifier-constraint");

    public static final Locator SUBJECT_LOCATOR_CONSTRAINT = _createLocator(_BASE + "subjectlocator-constraint");

    public static final Locator NAME_CONSTRAINT = _createLocator(_BASE + "topicname-constraint");

    public static final Locator NAME_TYPE_SCOPE_CONSTRAINT = _createLocator(_BASE + "nametypescope-constraint");

    public static final Locator OCCURRENCE_TYPE_SCOPE_CONSTRAINT = _createLocator(_BASE + "occurrencetypescope-constraint");

    public static final Locator OCCURRENCE_DATATYPE_CONSTRAINT = _createLocator(_BASE + "occurrencedatatype-constraint");

    public static final Locator ASSOCIATION_TYPE_SCOPE_CONSTRAINT = _createLocator(_BASE + "associationtypescope-constraint");

    public static final Locator ASSOCIATION_ROLE_CONSTRAINT = _createLocator(_BASE + "associationrole-constraint");

    public static final Locator ROLE_PLAYER_CONSTRAINT = _createLocator(_BASE + "roleplayer-constraint");

    public static final Locator OTHERROLE_CONSTRAINT = _createLocator(_BASE + "otherrole-constraint");

    public static final Locator TOPIC_OCCURRENCE_CONSTRAINT = _createLocator(_BASE + "topicoccurrence-constraint");

    public static final Locator UNIQUE_OCCURRENCE_CONSTRAINT = _createLocator(_BASE + "uniqueoccurrence-constraint");

}
