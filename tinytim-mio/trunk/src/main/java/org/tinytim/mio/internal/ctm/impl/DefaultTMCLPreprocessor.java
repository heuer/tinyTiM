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
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.tinytim.mio.internal.ctm.ITMCLPreprocessor;
import org.tinytim.mio.internal.ctm.ITemplate;
import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IOccurrence;
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
 * @version $Rev:$ - $Date:$
 */
public class DefaultTMCLPreprocessor implements ITMCLPreprocessor {

    private static final Logger LOG = Logger.getLogger(DefaultTMCLPreprocessor.class.getName());

    private final Map<Topic, Collection<ITemplate>> _topic2Templates;

    private Topic _topicType;

    private Topic _appliesTo;

    private Topic _constraintRole;

    private Topic _cardMin;

    private Topic _cardMax;

    private Topic _datatype;

    private Topic _regEx;

    private Topic _topicTypeRole;

    private Topic _scopeTypeRole;

    private Topic _assocTypeRole;


    public DefaultTMCLPreprocessor() {
        _topic2Templates = new HashMap<Topic, Collection<ITemplate>>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.internal.ctm.ITMCLPreprocessor#getSuppressableSubjectIdentifiers()
     */
    @Override
    public Set<Locator> getSuppressableSubjectIdentifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.internal.ctm.ITMCLPreprocessor#getTopicToTemplatesMapping()
     */
    @Override
    public Map<Topic, Collection<ITemplate>> getTopicToTemplatesMapping() {
        return  _topic2Templates;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.internal.ctm.ITMCLPreprocessor#process(org.tmapi.core.TopicMap, java.util.Collection, java.util.Collection)
     */
    @Override
    public void process(TopicMap topicMap, Collection<Topic> topics,
            Collection<Association> assocs) {
        _init(topicMap);
        TypeInstanceIndex tiIdx = ((IIndexManagerAware) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _processAbstractTopicConstraints(topicMap, tiIdx, topics, assocs);
        _processSubjectIdentifierConstraints(topicMap, tiIdx, topics, assocs);
        _processSubjectLocatorConstraints(topicMap, tiIdx, topics, assocs);
        _processAssociationTypeScopeConstraints(topicMap, tiIdx, topics, assocs);
        _processRoleConstraints(topicMap, tiIdx, topics, assocs);
        _processPlayerConstraints(topicMap, tiIdx, topics, assocs);
        _processOccurrenceConstraints(topicMap, tiIdx, topics, assocs);
        _processOccurrenceTypeScopeConstraints(topicMap, tiIdx, topics, assocs);
        _processNameConstraints(topicMap, tiIdx, topics, assocs);
        _processNameTypeScopeConstraints(topicMap, tiIdx, topics, assocs);
    }

    private Collection<Topic> _getConstraintInstances(TopicMap topicMap, TypeInstanceIndex tiIdx, Locator subjectIdentifier) {
        final Topic constraintType = topicMap.getTopicBySubjectIdentifier(subjectIdentifier);
        if (constraintType == null) {
            return Collections.emptySet();
        }
        return tiIdx.getTopics(constraintType);
    }

    private Collection<Topic> _getTopicTypeRolePlayers(Topic constraint, Collection<Association> assocs) {
        return _getPlayers(constraint, _constraintRole, _topicTypeRole, assocs);
    }

    private Collection<Topic> _getPlayers(Topic constraint, Topic constraintRoleType, Topic otherRoleType, Collection<Association> assocs) {
        Collection<Topic> result = new ArrayList<Topic>();
        for (Role role: constraint.getRolesPlayed(constraintRoleType, _appliesTo)) {
            Association assoc = role.getParent();
            if (!_isBinary(assoc)) {
                continue;
            }
            for (Role otherRole: assoc.getRoles(otherRoleType)) {
                assocs.remove(assoc);
                result.add(otherRole.getPlayer());
            }
        }
        return result;
    }

    private void _processAbstractTopicConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.ABSTRACT_TOPIC_TYPE_CONSTRAINT)) {
            _processAbstractTopicConstraint(constraint, topics, assocs);
        }
    }

    private void _processAbstractTopicConstraint(Topic constraint,
            Collection<Topic> topics, Collection<Association> assocs) {
        ITemplate tpl = new DefaultTemplate("isAbstract");
        Collection<Topic> players = _getTopicTypeRolePlayers(constraint, assocs);
        for (Topic player: players) {
            _registerTemplate(player, tpl);
        }
        if (players.size() == constraint.getRolesPlayed().size()
                && constraint.getOccurrences().isEmpty()
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private void _processSubjectIdentifierConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        _processLocatorConstraints(topicMap, tiIdx, "has-subjectidentifier", TMCL.SUBJECT_IDENTIFIER_CONSTRAINT, topics, assocs);
    }

    private void _processSubjectLocatorConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        _processLocatorConstraints(topicMap, tiIdx, "has-subjectlocator", TMCL.SUBJECT_LOCATOR_CONSTRAINT, topics, assocs);
    }

    private void _processLocatorConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, String templateName, Locator constraintSID, Collection<Topic> topics,
            Collection<Association> assocs) {
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, constraintSID)) {
            _processLocatorConstraint(constraint, templateName, constraintSID, topics, assocs);
        }
    }

    private void _processLocatorConstraint(Topic constraint, 
            String templateName, Locator constraintSID, Collection<Topic> topics, 
            Collection<Association> assocs) {
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        ILiteral regEx = _getRegEx(constraint);
        //TODO: Default values?
        if (cardMin == null || cardMax == null || regEx == null) {
            return;
        }
        DefaultTemplate tpl = new DefaultTemplate(templateName);
        tpl.addParameter(cardMin);
        tpl.addParameter(cardMax);
        tpl.addParameter(regEx);
        Collection<Topic> players = _getTopicTypeRolePlayers(constraint, assocs);
        for (Topic player: players) {
            _registerTemplate(player, tpl);
        }
        if (players.size() == constraint.getRolesPlayed().size()
                && constraint.getOccurrences().size() == 3
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private void _processAssociationTypeScopeConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.ASSOCIATION_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.ASSOCIATION_TYPE_SCOPE_CONSTRAINT)) {
            _processTypeScopeConstraint(constraint, "has-association-scope", type, topics, assocs);
        }
    }

    private void _processRoleConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.ROLE_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.ASSOCIATION_ROLE_CONSTRAINT)) {
            _processRoleConstraint(constraint, type, topics, assocs);
        }
    }

    //TODO: Merge with {@link #_processTypeScopeConstraint}
    private void _processRoleConstraint(Topic constraint, Topic type, Collection<Topic> topics,
            Collection<Association> assocs) {
        Topic topic = _getRolePlayer(constraint, type, assocs);
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        if (topic == null || cardMin == null || cardMax == null) {
            return;
        }
        DefaultTemplate tpl = new DefaultTemplate("has-role");
        tpl.addParameter(topic);
        tpl.addParameter(cardMin);
        tpl.addParameter(cardMax);
        Collection<Topic> players = _getPlayers(constraint, _constraintRole, _assocTypeRole, assocs);
        for (Topic player: players) {
            _registerTemplate(player, tpl);
        }
        if (players.size() * 2 == constraint.getRolesPlayed().size()
                && constraint.getOccurrences().size() == 2
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private void _processPlayerConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.ROLE_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.ASSOCIATION_ROLE_CONSTRAINT)) {
            _processRoleConstraint(constraint, type, topics, assocs);
        }
    }

    private void _processOccurrenceConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.OCCURRENCE_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.TOPIC_OCCURRENCE_CONSTRAINT)) {
            _processHasTopicChildConstraint(constraint, "has-occurrence", type, topics, assocs);
        }
    }

    private void _processOccurrenceTypeScopeConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.OCCURRENCE_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.TOPIC_OCCURRENCE_TYPE)) {
            _processTypeScopeConstraint(constraint, "has-occurrence-scope", type, topics, assocs);
        }
    }

    private void _processTypeScopeConstraint(Topic constraint, String templateName, Topic type,
            Collection<Topic> topics, Collection<Association> assocs) {
        Topic topic = _getRolePlayer(constraint, type, assocs);
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        if (topic == null || cardMin == null || cardMax == null) {
            return;
        }
        DefaultTemplate tpl = new DefaultTemplate(templateName);
        tpl.addParameter(topic);
        tpl.addParameter(cardMin);
        tpl.addParameter(cardMax);
        Collection<Topic> players = _getPlayers(constraint, _constraintRole, _scopeTypeRole, assocs);
        for (Topic player: players) {
            _registerTemplate(player, tpl);
        }
        if (players.size() * 2 == constraint.getRolesPlayed().size()
                && constraint.getOccurrences().size() == 2
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private void _processNameConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.NAME_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.NAME_TYPE)) {
            _processHasTopicChildConstraint(constraint, "has-name", type, topics, assocs);
        }
    }

    private void _processHasTopicChildConstraint(Topic constraint, String templateName, 
            Topic type, Collection<Topic> topics, Collection<Association> assocs) {
        Topic topic = _getRolePlayer(constraint, type, assocs);
        ILiteral cardMin = _getCardMin(constraint);
        ILiteral cardMax = _getCardMax(constraint);
        ILiteral regEx = _getRegEx(constraint);
        if (topic == null || cardMin == null || cardMax == null || regEx == null) {
            return;
        }
        DefaultTemplate tpl = new DefaultTemplate(templateName);
        tpl.addParameter(topic);
        tpl.addParameter(cardMin);
        tpl.addParameter(cardMax);
        tpl.addParameter(regEx);
        Collection<Topic> players = _getTopicTypeRolePlayers(constraint, assocs);
        for (Topic player: players) {
            _registerTemplate(player, tpl);
        }
        if (players.size() * 2 == constraint.getRolesPlayed().size()
                && constraint.getOccurrences().size() == 3
                && constraint.getNames().isEmpty()
                && constraint.getTypes().size() == 1
                && constraint.getSubjectLocators().isEmpty()
                && constraint.getSubjectIdentifiers().isEmpty()) {
            topics.remove(constraint);
        }
    }

    private void _processNameTypeScopeConstraints(TopicMap topicMap,
            TypeInstanceIndex tiIdx, Collection<Topic> topics,
            Collection<Association> assocs) {
        final Topic type = topicMap.getTopicBySubjectIdentifier(TMCL.NAME_TYPE_ROLE);
        if (type == null) {
            return;
        }
        for (Topic constraint: _getConstraintInstances(topicMap, tiIdx, TMCL.NAME_TYPE_SCOPE_CONSTRAINT)) {
            _processTypeScopeConstraint(constraint, "has-name-scope", type, topics, assocs);
        }
    }

    private Topic _getRolePlayer(Topic constraint, Topic roleType, Collection<Association> assocs) {
        Collection<Topic> players = _getPlayers(constraint, _constraintRole, roleType, assocs);
        return players.isEmpty() ? null : players.iterator().next();
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
        _appliesTo = topicMap.getTopicBySubjectIdentifier(TMCL.APPLIES_TO);
        _constraintRole = topicMap.getTopicBySubjectIdentifier(TMCL.CONSTRAINT_ROLE);
        _cardMin = topicMap.getTopicBySubjectIdentifier(TMCL.CARD_MIN);
        _cardMax = topicMap.getTopicBySubjectIdentifier(TMCL.CARD_MAX);
        _datatype = topicMap.getTopicBySubjectIdentifier(TMCL.DATATYPE);
        _regEx = topicMap.getTopicBySubjectIdentifier(TMCL.REGEXP);
        _topicTypeRole = topicMap.getTopicBySubjectIdentifier(TMCL.TOPIC_TYPE_ROLE);
        _scopeTypeRole = topicMap.getTopicBySubjectIdentifier(TMCL.SCOPE_TYPE_ROLE);
        _assocTypeRole = topicMap.getTopicBySubjectIdentifier(TMCL.ASSOCIATION_TYPE_ROLE);
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
