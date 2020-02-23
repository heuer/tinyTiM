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
package org.tinytim.mio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tinytim.core.Scope;
import org.tinytim.core.value.Literal;
import org.tinytim.internal.api.IAssociation;
import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.MergeUtils;
import org.tinytim.internal.utils.SignatureGenerator;
import org.tinytim.utils.TypeInstanceConverter;
import org.tinytim.voc.TMDM;

import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;

/**
 * Implementation of a {@link com.semagia.mio.IMapHandler} for tinyTiM.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 267 $ - $Date: 2009-02-24 14:56:47 +0100 (Di, 24 Feb 2009) $
 */
public final class TinyTimMapInputHandler extends AbstractHamsterMapHandler<Topic> {

    private final ITopicMap _tm;
    private final List<DelayedRoleEvents> _delayedRoleEvents;

    public TinyTimMapInputHandler(TopicMap topicMap) {
        if (topicMap == null) {
            throw new IllegalArgumentException("The topic map must not be null");
        }
        _tm = (ITopicMap) topicMap;
        _delayedRoleEvents = new ArrayList<DelayedRoleEvents>();
    }

    /**
     * Returns the underlying topic map.
     *
     * @return The topic map this handler operates upon.
     */
    TopicMap getTopicMap() {
        return _tm;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createAssociation(Topic type, Collection<Topic> scope,
            Topic reifier, Collection<String> iids, Collection<IRole<Topic>> roles)
            throws MIOException {
        IAssociation assoc = (IAssociation) _tm.createAssociation(type, _scope(scope));
        for (com.semagia.mio.helpers.HamsterHandler.IRole<Topic> r: roles) {
            Role role = assoc.createRole(r.getType(), r.getPlayer());
            if (r.getReifier() != null || !r.getItemIdentifiers().isEmpty()) {
                _delayedRoleEvents.add(new DelayedRoleEvents(role, r.getReifier(), r.getItemIdentifiers()));
            }
        }
        _applyReifier(assoc, reifier);
        _applyItemIdentifiers(assoc, iids);
        if (!_delayedRoleEvents.isEmpty()) {
            for (DelayedRoleEvents evt: _delayedRoleEvents) {
                _applyReifier(evt.role, evt.reifier);
                _applyItemIdentifiers((IConstruct) evt.role, evt.iids);
            }
            _delayedRoleEvents.clear();
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createName(
            Topic parent,
            Topic type,
            String value,
            Collection<Topic> scope,
            Topic reifier,
            Collection<String> iids,
            Collection<com.semagia.mio.helpers.HamsterHandler.IVariant<Topic>> variants)
            throws MIOException {
        IName name = ((ITopic) parent).createName(_nameType(type), _asLiteral(value), _scope(scope));
        _applyReifier(name, reifier);
        _applyItemIdentifiers(name, iids);
        final Set<Topic> nameScope = name.getScope();
        for (com.semagia.mio.helpers.HamsterHandler.IVariant<Topic> v: variants) {
            if (nameScope.containsAll(v.getScope())) {
                throw new MIOException("The variant's scope is not a superset of the name's scope");
            }
            org.tinytim.internal.api.IVariant variant = name.createVariant(_asLiteral(v.getValue(), v.getDatatype()), _scope(v.getScope()));
            _applyReifier(variant, v.getReifier());
            _applyItemIdentifiers(variant, v.getItemIdentifiers());
        }
    }

    /**
     * 
     *
     * @param type
     * @return
     */
    private Topic _nameType(Topic type) {
        return type == null ? _tm.createTopicBySubjectIdentifier(TMDM.TOPIC_NAME) 
                            : type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
     */
    @Override
    protected void createOccurrence(Topic parent, Topic type, String value,
            String datatype, Collection<Topic> scope, Topic reifier,
            Collection<String> iids) throws MIOException {
        IOccurrence occ = ((ITopic) parent).createOccurrence(type, _asLiteral(value, datatype), _scope(scope));
        _applyReifier(occ, reifier);
        _applyItemIdentifiers(occ, iids);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
     */
    @Override
    protected Topic createTopicByItemIdentifier(String iid)
            throws MIOException {
        return _tm.createTopicByItemIdentifier(_createLocator(iid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectIdentifier(java.lang.String)
     */
    @Override
    protected Topic createTopicBySubjectIdentifier(String sid)
            throws MIOException {
        return _tm.createTopicBySubjectIdentifier(_createLocator(sid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectLocator(java.lang.String)
     */
    @Override
    protected Topic createTopicBySubjectLocator(String slo)
            throws MIOException {
        return _tm.createTopicBySubjectLocator(_createLocator(slo));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapItemIdentifier(java.lang.String)
     */
    @Override
    protected void handleTopicMapItemIdentifier(String iid)
            throws MIOException {
        _tm.addItemIdentifier(_createLocator(iid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapReifier(java.lang.Object)
     */
    @Override
    protected void handleTopicMapReifier(Topic reifier) throws MIOException {
        _tm.setReifier(reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void handleTypeInstance(Topic instance, Topic type)
            throws MIOException {
        instance.addType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        super.endTopicMap();
        TypeInstanceConverter.convertAssociationsToTypes(_tm);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    public void handleSubjectIdentifier(Topic topic, String subjectIdentifier) throws MIOException {
        Locator sid = _tm.createLocator(subjectIdentifier);
        Topic existing = _tm.getTopicBySubjectIdentifier(sid);
        if (existing != null && !(existing.equals(topic))) {
            _merge(existing, topic);
        }
        else {
            IConstruct tmo = (IConstruct) _tm.getConstructByItemIdentifier(sid);
            if (tmo != null && tmo.isTopic() && !tmo.equals(topic)) {
                _merge((Topic)tmo, topic);
            }
        }
        topic.addSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
     */
    @Override
    public void handleSubjectLocator(Topic topic, String subjectLocator) throws MIOException {
        Locator slo = _tm.createLocator(subjectLocator);
        Topic existing = _tm.getTopicBySubjectLocator(slo);
        if (existing != null && !(existing.equals(topic))) {
            _merge(existing, topic);
        }
        topic.addSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    public void handleItemIdentifier(Topic topic, String itemIdentifier) throws MIOException {
        Locator iid = _tm.createLocator(itemIdentifier);
        IConstruct existing = (IConstruct) _tm.getConstructByItemIdentifier(iid);
        if (existing != null && existing.isTopic() && !existing.equals(topic)) {
            _merge((Topic)existing, topic);
        }
        else {
            Topic existingTopic = _tm.getTopicBySubjectIdentifier(iid);
            if (existingTopic != null && !existingTopic.equals(topic)) {
                _merge(existingTopic, topic);
            }
        }
        topic.addItemIdentifier(iid);
    }

    private static boolean _areMergable(IConstruct a, IConstruct b) {
        boolean res = a.getClass().equals(b.getClass()) 
                        && SignatureGenerator.generateSignature(a) == SignatureGenerator.generateSignature(b);
        if (res && a.isRole()) {
            res = SignatureGenerator.generateSignature((IConstruct) a.getParent()) == SignatureGenerator.generateSignature((IConstruct) b.getParent());
        }
        if (res && a.isVariant()) {
            Name parentA = (Name) a.getParent();
            Name parentB = (Name) b.getParent();
            res = parentA.getParent().equals(parentB.getParent())
                    && SignatureGenerator.generateSignature(parentA) == SignatureGenerator.generateSignature(parentB);
        }
        return res;
    }

    private static void _merge(Reifiable source, Reifiable target) {
        MergeUtils.handleExistingConstruct(source, target);
        IConstruct isource = (IConstruct) source;
        if (isource.isRole()) {
            IAssociation sourceParent = (IAssociation) source.getParent();
            IAssociation targetParent = (IAssociation) target.getParent();
            MergeUtils.handleExistingConstruct(sourceParent, targetParent);
            MergeUtils.moveRoles(sourceParent, targetParent);
            if (!sourceParent.equals(targetParent)) {
                sourceParent.remove();
            }
        }
        else {
            if (isource.isAssociation()) {
                MergeUtils.moveRoleCharacteristics((IAssociation) source, (IAssociation) target);
            }
            else if (isource.isName()) {
                MergeUtils.moveVariants((Name) source, (Name) target);
            }
            source.remove();
        }
    }

    /**
     * Merges the <tt>source</tt> topic with the <tt>target</tt>.
     * 
     * Further, this method ensures that the construct stack stays valid: If
     * the <tt>source</tt> is part of the stack, it is replaced with 
     * <tt>target</tt>.
     *
     * @param source The source topic (will be removed).
     * @param target The target topic.
     */
    private void _merge(Topic source, Topic target) {
        target.mergeIn(source);
        super.notifyMerge(source, target);
    }

    private Locator _createLocator(String reference) {
        return _tm.createLocator(reference);
    }

    private void _applyItemIdentifiers(IConstruct reifiable, Collection<String> iids) throws MIOException {
        for (String itemIdentifier: iids) {
            Locator iid = _createLocator(itemIdentifier);
            try {
                reifiable.addItemIdentifier(iid);
            }
            catch (IdentityConstraintException ex) {
                final IConstruct existing = (IConstruct) ex.getExisting();
                if (_areMergable(reifiable, existing)) {
                    _merge((Reifiable) existing, (Reifiable) reifiable);
                }
                else {
                    throw new MIOException(ex);
                }
            }
        }
    }

    private static IScope _scope(Collection<Topic> scope) {
        return scope == null ? Scope.UCS : Scope.create(scope);
    }

    private void _applyReifier(Reifiable reifiable, Topic reifier) throws MIOException {
        if (reifier == null) {
            return;
        }
        try {
            reifiable.setReifier(reifier);
        }
        catch (ModelConstraintException ex) {
            IConstruct existing = (IConstruct) reifier.getReified();
            if (reifiable.equals(existing)) {
                return;
            }
            if (_areMergable((IConstruct) reifiable, existing)) {
                _merge((Reifiable) existing, reifiable);
            }
            else {
                throw new MIOException(ex);
            }
        }
    }

    private static ILiteral _asLiteral(String value) {
        return Literal.create(value);
    }

    private static ILiteral _asLiteral(String value, String datatype) {
        return Literal.create(value, datatype);
    }

    private static final class DelayedRoleEvents {
        final Role role;
        final Topic reifier;
        final Collection<String> iids;
        public DelayedRoleEvents(Role role, Topic reifier, Collection<String> iids) {
            this.role = role;
            this.reifier = reifier;
            this.iids = iids;
        }
    }
}
