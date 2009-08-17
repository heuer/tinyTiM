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
package org.tinytim.mio.internal.ctm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tinytim.core.value.Literal;
import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.mio.internal.ctm.ITMCLPreprocessor;
import org.tinytim.mio.internal.ctm.ITemplate;
import org.tinytim.voc.TMCL;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TypeInstanceIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hannes Niederhausen
 * @version $Rev$ - $Date$
 */
public class DefaultTMCLPreprocessor implements ITMCLPreprocessor {

    private final Map<Topic, Collection<ITemplate>> _topic2Templates;

    private Topic _constrains;

    private Topic _constrained;

    private Topic _container;

    private Topic _containee;

    private Topic _constrained_topic_type;

    private Topic _other_constrained_topic_type;

    private Topic _constrained_statement;

    private Topic _constrained_role;

    private Topic _belongs_to_schema;

    private Topic _other_constrained_role;

    private Topic _overlaps;

    private Topic _allowed_scope;

    private Topic _allowed_reifier;

    private Topic _allowed;

    private Topic _allows;

    private Topic _cardMin;

    private Topic _cardMax;

    private Topic _datatype;

    private Topic _regEx;

    private ILiteral _cardMinDefault;

    private ILiteral _cardMaxDefault;

    private ILiteral _regExDefault;

    public DefaultTMCLPreprocessor() {
        _topic2Templates = new HashMap<Topic, Collection<ITemplate>>();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.tinytim.mio.internal.ctm.ITMCLPreprocessor#
     * getSuppressableSubjectIdentifiers()
     */
    @Override
    public Set<Locator> getSuppressableSubjectIdentifiers() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tinytim.mio.internal.ctm.ITMCLPreprocessor#getTopicToTemplatesMapping
     * ()
     */
    @Override
    public Map<Topic, Collection<ITemplate>> getTopicToTemplatesMapping() {
        return _topic2Templates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tinytim.mio.internal.ctm.ITMCLPreprocessor#process(org.tmapi.core
     * .TopicMap, java.util.Collection, java.util.Collection)
     */
    @Override
    public void process(TopicMap topicMap, Collection<Topic> topics,
            Collection<Association> assocs) {
        _init(topicMap);
        TypeInstanceIndex tiIdx = ((IIndexManagerAware) topicMap)
                .getIndexManager().getTypeInstanceIndex();
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _processAbstractTopicConstraints(topicMap, tiIdx, topics, assocs);
        _processUniqueConstraints(topicMap, tiIdx, topics, assocs);
        _processDatatypeConstraints(topicMap, tiIdx, topics, assocs);
        _processRegExConstraints(topicMap, tiIdx, topics, assocs);
        _processSubjectIdentifierConstraints(topicMap, tiIdx, topics, assocs);
        _processSubjectLocatorConstraints(topicMap, tiIdx, topics, assocs);
        _processOverlapConstraints(topicMap, tiIdx, topics, assocs);
        _processAssociationRoleConstraints(topicMap, tiIdx, topics, assocs);
        _processRolePlayerConstraints(topicMap, tiIdx, topics, assocs);
        _processRoleCombinationConstraints(topicMap, tiIdx, topics, assocs);
        _processOccurrenceConstraints(topicMap, tiIdx, topics, assocs);
        _processNameConstraints(topicMap, tiIdx, topics, assocs);
        _processScopeConstraints(topicMap, tiIdx, topics, assocs);
        _processReifierConstraints(topicMap, tiIdx, topics, assocs);
        _processTopicReifiesConstraints(topicMap, tiIdx, topics, assocs);
        _processBelongsToSchema(topicMap, tiIdx, topics, assocs);
    }

    private Collection<Topic> _getConstraintInstances(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Locator subjectIdentifier) {
        final Topic constraintType = topicMap
                .getTopicBySubjectIdentifier(subjectIdentifier);
        if (constraintType == null) {
            return Collections.emptySet();
        }
        return tiIdx.getTopics(constraintType);
    }

    private Topic _getConstrainedTopicTypePlayer(Topic constraint,
            Collection<Association> assocs) {
        for (Role role : constraint.getRolesPlayed(_constrains,
                _constrained_topic_type)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            for (Role otherRole : assoc.getRoles(_constrained)) {
                assocs.remove(assoc);
                return otherRole.getPlayer();
            }
        }
        return null;
    }

    private Topic _getConstrainedRolePlayer(Topic constraint,
            Collection<Association> assocs) {
        for (Role role : constraint.getRolesPlayed(_constrains,
                _constrained_role)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            for (Role otherRole : assoc.getRoles(_constrained)) {
                assocs.remove(assoc);
                return otherRole.getPlayer();
            }
        }
        return null;
    }

    private Topic _getOtherConstrainedTopicTypePlayer(Topic constraint,
            Collection<Association> assocs) {
        for (Role role : constraint.getRolesPlayed(_constrains,
                _other_constrained_topic_type)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            for (Role otherRole : assoc.getRoles(_constrained)) {
                assocs.remove(assoc);
                return otherRole.getPlayer();
            }
        }
        return null;
    }

    private Topic _getOtherConstrainedRolePlayer(Topic constraint,
            Collection<Association> assocs) {
        for (Role role : constraint.getRolesPlayed(_constrains,
                _other_constrained_role)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            for (Role otherRole : assoc.getRoles(_constrained)) {
                assocs.remove(assoc);
                return otherRole.getPlayer();
            }
        }
        return null;
    }

    private Topic _getConstrainedStatementPlayer(Topic constraint,
            Collection<Association> assocs) {
        Topic player = null;
        for (Role role : constraint.getRolesPlayed(_constrains,
                _constrained_statement)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }

            player = assoc.getRoles(_constrained).iterator().next().getPlayer();
            assocs.remove(assoc);
        }
        return player;
    }

    private Topic _getAllowedScopePlayer(Topic constraint,
            Collection<Association> assocs) {
        Topic player = null;
        for (Role role : constraint.getRolesPlayed(_allows, _allowed_scope)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }

            player = assoc.getRoles(_allowed).iterator().next().getPlayer();
            assocs.remove(assoc);
        }
        return player;
    }

    private Topic _getAllowedReifierPlayer(Topic constraint,
            Collection<Association> assocs) {
        Topic player = null;
        for (Role role : constraint.getRolesPlayed(_allows, _allowed_reifier)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }

            player = assoc.getRoles(_allowed).iterator().next().getPlayer();
            assocs.remove(assoc);
        }
        return player;
    }

    private void _processAbstractTopicConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.ABSTRACT_TOPIC_TYPE_CONSTRAINT)) {
            _processAbstractTopicConstraint(constraint, topics, assocs);
        }
    }

    private void _processAbstractTopicConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        ITemplate tpl = new DefaultTemplate("isAbstract");
        Topic player = _getConstrainedTopicTypePlayer(constraint, assocs);
        _registerTemplate(player, tpl);
        removeConstraint(constraint, topics, 0);
    }

    private void _processAssociationRoleConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.ASSOCIATION_ROLE_CONSTRAINT)) {
            _processAssociationRoleConstraint(constraint, topics, assocs);
        }

    }

    private void _processAssociationRoleConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic player = _getConstrainedStatementPlayer(constraint, assocs);
        Topic roleType = _getConstrainedRolePlayer(constraint, assocs);

        DefaultTemplate tpl = new DefaultTemplate("has-role");
        tpl.addParameter(roleType);

        int occCounter = _assignCardinality(constraint, tpl, 2);

        _registerTemplate(player, tpl);
        removeConstraint(constraint, topics, occCounter);
    }

    private void _processBelongsToSchema(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic schema : _getConstraintInstances(topicMap, tiIdx,
                TMCL.SCHEMA)) {
            _processBelongsToSchema(schema, topics, assocs);
        }
    }

    private void _processBelongsToSchema(Topic schema,
            Collection<Topic> topics, Collection<Association> assocs) {

        DefaultTemplate tpl = new DefaultTemplate("belongs-to");
        tpl.addParameter(schema);

        for (Role role : schema.getRolesPlayed(_container, _belongs_to_schema)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            Topic construct = assoc.getRoles(_containee).iterator().next()
                    .getPlayer();

            _registerTemplate(construct, tpl);
        }
    }

    private void _processReifierConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.REIFIER_CONSTRAINT)) {
            _processReifierConstraint(constraint, topics, assocs);
        }

    }
    
    private void _processReifierConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic reifiableType = _getConstrainedStatementPlayer(constraint, assocs);
        Topic reifierType = _getAllowedReifierPlayer(constraint, assocs);
    
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        DefaultTemplate tpl = null;
        if (cardMin.getValue().equals(cardMax.getValue())) {
            if (cardMin.getValue().equals("0")) {
                tpl = new DefaultTemplate("cannot-have-reifier");
            }
            else {
                tpl = new DefaultTemplate("must-have-reifier");
                tpl.addParameter(reifierType);
            }
        }
        else {
            tpl = new DefaultTemplate("may-have-reifier");
            tpl.addParameter(reifierType);
    
        }
    
        _registerTemplate(reifiableType, tpl);
        removeConstraint(constraint, topics, 2);
    }

    private void _processTopicReifiesConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.TOPIC_REIFIES_CONSTRAINT)) {
            _processTopicReifiesConstraint(constraint, topics, assocs);
        }

    }
    
    private void _processTopicReifiesConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic reifiableType = _getConstrainedStatementPlayer(constraint, assocs);
        Topic reifierType = _getConstrainedTopicTypePlayer(constraint, assocs);

        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        DefaultTemplate tpl = null;
        if (cardMin.getValue().equals(cardMax.getValue())) {
            if (cardMin.getValue().equals("0")) {
                tpl = new DefaultTemplate("cannot-reify");
            }
            else {
                tpl = new DefaultTemplate("must-reify");
                tpl.addParameter(reifiableType);
            }
        }
        else {
            tpl = new DefaultTemplate("may");
            tpl.addParameter(reifiableType);

        }

        _registerTemplate(reifierType, tpl);
        removeConstraint(constraint, topics, 2);
    }

    private void _processRolePlayerConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.TOPIC_ROLE_CONSTRAINT)) {
            _processRolePlayerConstraint(constraint, topics, assocs);
        }

    }

    private void _processRolePlayerConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic type = _getConstrainedTopicTypePlayer(constraint, assocs);
        Topic assocType = _getConstrainedStatementPlayer(constraint, assocs);
        Topic roleType = _getConstrainedRolePlayer(constraint, assocs);

        DefaultTemplate tpl = new DefaultTemplate("plays-role");
        tpl.addParameter(roleType);
        tpl.addParameter(assocType);
        int occCounter = _assignCardinality(constraint, tpl, 2);

        _registerTemplate(type, tpl);
        removeConstraint(constraint, topics, occCounter);
    }

    private void _processRoleCombinationConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.ROLE_COMBINATION_CONSTRAINT)) {
            _processRoleCombinationConstraint(constraint, topics, assocs);
        }

    }

    private void _processRoleCombinationConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic type = _getConstrainedTopicTypePlayer(constraint, assocs);
        Topic assocType = _getConstrainedStatementPlayer(constraint, assocs);
        Topic roleType = _getConstrainedRolePlayer(constraint, assocs);
        Topic otherRole = _getOtherConstrainedRolePlayer(constraint, assocs);
        Topic otherPlayer = _getOtherConstrainedTopicTypePlayer(constraint,
                assocs);

        DefaultTemplate tpl = new DefaultTemplate("role-combination");
        tpl.addParameter(roleType);
        tpl.addParameter(type);
        tpl.addParameter(otherRole);
        tpl.addParameter(otherPlayer);

        _registerTemplate(assocType, tpl);
        removeConstraint(constraint, topics, 0);
    }

    private void _processUniqueConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.UNIQUE_VALUE_CONSTRAINT)) {
            _processUniqueConstraint(constraint, topics, assocs);
        }
    }

    private void _processUniqueConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        ITemplate tpl = new DefaultTemplate("is-unique");
        Topic player = _getConstrainedStatementPlayer(constraint, assocs);

        _registerTemplate(player, tpl);
        topics.remove(constraint);

    }

    private void _processDatatypeConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.OCCURRENCE_DATATYPE_CONSTRAINT)) {
            _processDatatypeConstraint(constraint, topics, assocs);
        }
    }

    private void _processDatatypeConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {

        ILiteral datatype = _getDatatype(constraint);
        DefaultTemplate tpl = new DefaultTemplate("has-datatype");
        tpl.addParameter(datatype);

        Topic player = _getConstrainedStatementPlayer(constraint, assocs);

        _registerTemplate(player, tpl);

        removeConstraint(constraint, topics, 1);
    }

    private void _processRegExConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.REGULAR_EXPRESSION_CONSTRAINT)) {
            _processRegExConstraint(constraint, topics, assocs);
        }
    }

    private void _processRegExConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {

        ILiteral regExp = _getRegEx(constraint);
        DefaultTemplate tpl = new DefaultTemplate("matches-regexp");
        tpl.addParameter(regExp);

        Topic player = _getConstrainedStatementPlayer(constraint, assocs);

        _registerTemplate(player, tpl);
        // removing instance from topics if it has no further informations
        removeConstraint(constraint, topics, 1);
    }

    private void _processOverlapConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.OVERLAP_DECLARATION)) {
            _processOverlapConstraint(constraint, topics, assocs);
        }

    }

    private void _processOverlapConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {

        List<Topic> players = new ArrayList<Topic>(2);
        for (Role role : constraint.getRolesPlayed(_allows, _overlaps)) {
            Association assoc = role.getParent();
            players.add(assoc.getRoles(_allowed).iterator().next().getPlayer());

            assocs.remove(assoc);
        }
        DefaultTemplate tpl = new DefaultTemplate("overlaps");
        tpl.addParameter(players.get(1));

        _registerTemplate(players.get(0), tpl);
        removeConstraint(constraint, topics, 0);

    }

    private void _processSubjectIdentifierConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        _processLocatorConstraints(topicMap, tiIdx, "has-subject-identifier",
                TMCL.SUBJECT_IDENTIFIER_CONSTRAINT, topics, assocs);
    }

    private void _processSubjectLocatorConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        _processLocatorConstraints(topicMap, tiIdx, "has-subject-locator",
                TMCL.SUBJECT_LOCATOR_CONSTRAINT, topics, assocs);
    }

    private void _processLocatorConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, String templateName,
            Locator constraintSID, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                constraintSID)) {
            _processLocatorConstraint(constraint, templateName, constraintSID,
                    topics, assocs);
        }
    }

    private void _processLocatorConstraint(Topic constraint,
            String templateName, Locator constraintSID,
            Collection<Topic> topics, Collection<Association> assocs) {

        int occCounter = 3;

        ILiteral regEx = _getRegEx(constraint);
        if (regEx == null) {
            regEx = _regExDefault;
            occCounter--;
        }

        DefaultTemplate tpl = new DefaultTemplate(templateName);
        tpl.addParameter(regEx);

        occCounter = _assignCardinality(constraint, tpl, occCounter);

        Topic player = _getConstrainedTopicTypePlayer(constraint, assocs);
        _registerTemplate(player, tpl);
        removeConstraint(constraint, topics, occCounter);
    }

    private void _processScopeConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap
                .getTopicBySubjectIdentifier(TMCL.SCOPE_TYPE);
        if (type == null) {
            return;
        }
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.SCOPE_CONSTRAINT)) {
            _processScopeConstraint(constraint, type, topics, assocs);
        }
    }

    private void _processScopeConstraint(Topic constraint, Topic type,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic scope = _getAllowedScopePlayer(constraint, assocs);

        DefaultTemplate tpl = new DefaultTemplate("has-scope");
        tpl.addParameter(scope);

        int occCounter = _assignCardinality(constraint, tpl, 2);

        Topic player = _getConstrainedStatementPlayer(constraint, assocs);
        _registerTemplate(player, tpl);
        removeConstraint(constraint, topics, occCounter);
    }

    private void _processOccurrenceConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap
                .getTopicBySubjectIdentifier(TMCL.OCCURRENCE_TYPE);
        if (type == null) {
            return;
        }
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.TOPIC_OCCURRENCE_CONSTRAINT)) {
            _processHasTopicChildConstraint(constraint, "has-occurrence", type,
                    topics, assocs);
        }
    }

    private void _processNameConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.NAME_TYPE);
        if (type == null) {
            return;
        }
        for (Topic constraint : _getConstraintInstances(topicMap, tiIdx,
                TMCL.TOPIC_NAME_CONSTRAINT)) {
            _processHasTopicChildConstraint(constraint, "has-name", type,
                    topics, assocs);
        }
    }

    private void _processHasTopicChildConstraint(Topic constraint,
            String templateName, Topic type, Collection<Topic> topics,
            Collection<Association> assocs) {
        Topic topic = _getConstrainedStatementPlayer(constraint, assocs);

        if (topic == null) {
            return;
        }

        DefaultTemplate tpl = new DefaultTemplate(templateName);
        tpl.addParameter(topic);

        int occCounter = _assignCardinality(constraint, tpl, 2);

        Topic player = _getConstrainedTopicTypePlayer(constraint, assocs);

        _registerTemplate(player, tpl);

        removeConstraint(constraint, topics, occCounter);
    }

    private int _assignCardinality(Topic constraint, DefaultTemplate tpl,
            int counter) {
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);

        int occCounter = counter;

        if (cardMin == null) {
            occCounter--;
            cardMin = _cardMinDefault;
        }
        if (cardMax == null) {
            occCounter--;
            cardMax = _cardMaxDefault;
        }
        tpl.addParameter(cardMin);
        tpl.addParameter(cardMax);
        return occCounter;
    }

    /*
     * removing instance from topics if it has no further informations
     */
    private void removeConstraint(Topic constraint, Collection<Topic> topics,
            int expectedOccurrences) {
        if (constraint.getOccurrences().size() == expectedOccurrences
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private ILiteral _getValue(Topic topic, Topic type) {
        if (type == null) {
            return null;
        }
        Collection<Occurrence> occs = topic.getOccurrences(type);
        if (occs.size() != 1) {
            return null;
        }
        return ((IOccurrence) occs.iterator().next()).getLiteral();
    }

    private ILiteral _getCardMin(Topic constraint) {
        return _getValue(constraint, _cardMin);
    }

    private ILiteral _getDatatype(Topic constraint) {
        return _getValue(constraint, _datatype);
    }

    private ILiteral _getCardMax(Topic constraint) {
        return _getValue(constraint, _cardMax);
    }

    private ILiteral _getRegEx(Topic constraint) {
        return _getValue(constraint, _regEx);
    }

    private boolean _isBinary(Association assoc) {
        return assoc.getRoles().size() == 2;
    }

    private void _init(TopicMap topicMap) {
        _overlaps = topicMap.getTopicBySubjectIdentifier(TMCL.OVERLAPS);
        _constrained_statement = topicMap
                .getTopicBySubjectIdentifier(TMCL.CONSTRAINED_STATEMENT);
        _constrained_topic_type = topicMap
                .getTopicBySubjectIdentifier(TMCL.CONSTRAINED_TOPIC_TYPE);
        _constrained_role = topicMap
                .getTopicBySubjectIdentifier(TMCL.CONSTRAINED_ROLE);
        _other_constrained_topic_type = topicMap
                .getTopicBySubjectIdentifier(TMCL.OTHER_CONSTRAINED_TOPIC_TYPE);
        _other_constrained_role = topicMap
                .getTopicBySubjectIdentifier(TMCL.OTHER_CONSTRAINED_ROLE);
        _belongs_to_schema = topicMap
                .getTopicBySubjectIdentifier(TMCL.BELONGS_TO_SCHEMA);

        _allowed_scope = topicMap
                .getTopicBySubjectIdentifier(TMCL.ALLOWED_SCOPE);
        _allowed_reifier = topicMap
                .getTopicBySubjectIdentifier(TMCL.ALLOWED_REIFIER);
        _allowed = topicMap.getTopicBySubjectIdentifier(TMCL.ALLOWED);
        _allows = topicMap.getTopicBySubjectIdentifier(TMCL.ALLOWS);
        _constrained = topicMap.getTopicBySubjectIdentifier(TMCL.CONSTRAINED);
        _constrains = topicMap.getTopicBySubjectIdentifier(TMCL.CONSTRAINS);
        _containee = topicMap.getTopicBySubjectIdentifier(TMCL.CONTAINEE);
        _container = topicMap.getTopicBySubjectIdentifier(TMCL.CONTAINER);

        _cardMin = topicMap.getTopicBySubjectIdentifier(TMCL.CARD_MIN);
        _cardMax = topicMap.getTopicBySubjectIdentifier(TMCL.CARD_MAX);
        _datatype = topicMap.getTopicBySubjectIdentifier(TMCL.DATATYPE);
        _regEx = topicMap.getTopicBySubjectIdentifier(TMCL.REGEXP);

        _cardMinDefault = Literal.create(0);
        _cardMaxDefault = Literal.create("*", "ctm:integer");
        _regExDefault = Literal.create(".*");
    }

    private void _registerTemplate(Topic topic, ITemplate tpl) {
        Collection<ITemplate> templates = _topic2Templates.get(topic);
        if (templates == null) {
            templates = new ArrayList<ITemplate>();
            _topic2Templates.put(topic, templates);
        }
        templates.add(tpl);
    }

}
