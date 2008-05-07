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
package org.tinytim.cxtm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import org.tinytim.DuplicateRemovalUtils;
import org.tinytim.IConstruct;
import org.tinytim.IDatatypeAwareConstruct;
import org.tinytim.IReifiable;
import org.tinytim.ITyped;
import org.tinytim.TopicImpl;
import org.tinytim.TopicMapImpl;
import org.tinytim.index.ITypeInstanceIndex;
import org.tinytim.voc.TMDM;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.DuplicateSourceLocatorException;
import org.tmapi.core.Locator;
import org.tmapi.core.MergeException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Occurrence;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Provides serialization of topic maps into Canonical XTM (CXTM).
 * <p>
 * CXTM is a format that guarantees that two equivalent Topic Maps Data Model 
 * instances [ISO/IEC 13250-2] will always produce byte-by-byte identical 
 * serializations, and that non-equivalent instances will always produce 
 * different serializations.
 * </p>
 * <p>
 * See <a href="http://www.isotopicmaps.org/cxtm/">http://www.isotopicmaps.org/cxtm/</a>
 * for details.
 * </p>
 * <p>
 * <em>CAUTION</em>: This class implements the 
 * <a href="http://www.isotopicmaps.org/cxtm/2008-04-14/">CXTM draft dtd. 2008-04-14</a>,
 * the output may change in the future.
 * </p>
 * <p>
 * The canonicalizer IS NOT a generic TMAPI-compatible implementation. It 
 * requires tinyTiM. The canonicalizer requires that the property 
 * {@link org.tinytim.Property#XTM10_REIFICATION} is set to <tt>false</tt> and
 * that the property {@link org.tinytim.Property#INHERIT_NAME_SCOPE} is enabled
 * (set to <tt>true</tt>).
 * </p>
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Canonicalizer {

    private static final Logger LOG = Logger.getLogger(Canonicalizer.class.getName());

    private static final String _XSD_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

    private static final AssociationRole[] _EMPTY_ROLES = new AssociationRole[0];
    private static final Occurrence[] _EMPTY_OCCS = new Occurrence[0];
    private static final TopicName[] _EMPTY_NAMES = new TopicName[0];
    private static final Variant[] _EMPTY_VARIANTS = new Variant[0];

    private Topic _type;
    private Topic _instance;
    private Topic _typeInstance;

    private CXTMWriter _out;
    private final String _normBase;
    private Map<TopicMapObject, Integer> _construct2Id;
    private Map<Topic, List<AssociationRole>> _topic2Roles;
    private Map<String, String> _locator2Norm;

    private Comparator<Topic> _topicComparator;
    private Comparator<Association> _assocComparator;
    private Comparator<AssociationRole> _roleComparator;
    private Comparator<Occurrence> _occComparator;
    private Comparator<TopicName> _nameComparator;
    private Comparator<Variant> _variantComparator;
    private Comparator<Set<Locator>> _locSetComparator;
    private Comparator<Locator> _locComparator;
    private Comparator<Set<Topic>> _scopeComparator;

    private Map<Topic, TopicName[]> _topic2Names;
    private Map<Topic, Occurrence[]> _topic2Occs;
    private Map<TopicName, Variant[]> _name2Variants;
    private Map<Association, AssociationRole[]> _assoc2Roles;

    /**
     * Creates a canonicalizer.
     *
     * @param out The stream the CXTM is written onto.
     * @param baseLocator The base locator which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public Canonicalizer(OutputStream out, String baseLocator) throws IOException {
        if (baseLocator == null) {
            throw new IllegalArgumentException("The base locator must not be null");
        }
        _out = new CXTMWriter(out);
        _normBase = _normalizeBaseLocator(baseLocator);
        _topicComparator = new TopicComparator();
        _assocComparator = new AssociationComparator();
        _roleComparator = new RoleComparator();
        _occComparator = new OccurrenceComparator();
        _nameComparator = new NameComparator();
        _variantComparator = new VariantComparator();
        _locSetComparator = new LocatorSetComparator();
        _locComparator = new LocatorComparator();
        _scopeComparator = new ScopeComparator();
    }

    /**
     * Serializes the specified <code>topicMap</code> into the CXTM format.
     * <p>
     * <em>CAUTION</em>: This method MAY modify the topic map since duplicate 
     * Topic Maps constructs (if any) are removed in advance.
     * </p>
     * <p>
     * The topic map's base locator 
     * ({@link org.tmapi.core.TopicMap#getBaseLocator()}) is ignored.
     * </p>
     * 
     * @param topicMap The topic map to serialize.
     * @throws IOException If an error occurs.
     */
    public void write(TopicMap topicMap) throws IOException {
        DuplicateRemovalUtils.removeDuplicates(topicMap);
        _construct2Id = new IdentityHashMap<TopicMapObject, Integer>();
        _locator2Norm = new HashMap<String, String>();
        _assoc2Roles = new IdentityHashMap<Association, AssociationRole[]>();
        _topic2Roles = new IdentityHashMap<Topic, List<AssociationRole>>();
        _topic2Occs = new IdentityHashMap<Topic, Occurrence[]>();
        _topic2Names = new IdentityHashMap<Topic, TopicName[]>();
        _name2Variants = new IdentityHashMap<TopicName, Variant[]>();
        ITypeInstanceIndex typeInstanceIndex = ((TopicMapImpl) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIndex.isAutoUpdated()) {
            typeInstanceIndex.reindex();
        }
        Topic[] topics = _fetchTopics(topicMap, typeInstanceIndex);
        Association[] assocs = _fetchAssociations(topicMap, typeInstanceIndex);
        typeInstanceIndex.close();
        _createIndex(topics, assocs);
        _out.startDocument();
        AttributesImpl attrs = new AttributesImpl();
        _addReifier(attrs, (IReifiable)topicMap);
        _out.startElement("topicMap", attrs);
        _out.newline();
        _writeItemIdentifiers(topicMap);
        for (Topic topic: topics) {
            _writeTopic(topic);
        }
        for (Association assoc: assocs) {
            _writeAssociation(assoc);
        }
        _out.endElement("topicMap");
        _out.newline();
        _out.endDocument();
        _out = null;
        _construct2Id = null;
        _locator2Norm = null;
        _assoc2Roles = null;
        _topic2Roles = null;
        _topic2Occs = null;
        _topic2Names = null;
        _name2Variants = null;
    }

    /**
     * Returns an unsorted array of topics which should be included into
     * the output.
     * 
     * This method may return more topics than {@link TopicMap#getTopics()}
     * since this method creates virtual topics to model type-instance
     * relationships properly.
     *
     * @param topicMap The topic map from which the topic should be serialized.
     * @param idx A (upto date) type instance index.
     * @return All topics which must be included into the output.
     */
    @SuppressWarnings("unchecked")
    private Topic[] _fetchTopics(TopicMap topicMap, ITypeInstanceIndex idx) {
        Collection<Topic> types = idx.getTopicTypes();
        if (types.isEmpty()) {
            Set<Topic> topics = topicMap.getTopics();
            return topics.toArray(new Topic[topics.size()]);
        }
        else {
            List<Topic> topics = new ArrayList<Topic>(topicMap.getTopics());
            TopicMapImpl tm = (TopicMapImpl) topicMap;
            _typeInstance = _getTopicBySubjectIdentifier(tm, topics, TMDM.TYPE_INSTANCE);
            _type = _getTopicBySubjectIdentifier(tm, topics, TMDM.TYPE);
            _instance = _getTopicBySubjectIdentifier(tm, topics, TMDM.INSTANCE);
            return topics.toArray(new Topic[topics.size()]);
        }
    }

    /**
     * Returns a topic by its subject identifier. If the topic is null, a 
     * {@link TypeInstanceTopic} is created, added to the <code>topics</code>
     * and returned.
     *
     * @param tm The topic map to fetch the topic from.
     * @param topics A modifiable collection of topics.
     * @param sid The subject identifier.
     * @return A topic with the specified subject identifier.
     */
    private Topic _getTopicBySubjectIdentifier(TopicMapImpl tm, Collection<Topic> topics, Locator sid) {
        Topic topic = tm.getTopicBySubjectIdentifier(sid);
        if (topic == null) {
            topic = new TypeInstanceTopic(sid);
            topics.add(topic);
        }
        return topic;
    }

    /**
     * Returns an unsorted array of associations which should be serialized.
     * 
     * This method may return more association than {@link TopicMap#getAssociations()}
     * since this method may create virtual associations which are used to
     * model type-instance relationships properly.
     *
     * @param tm The topic map from which the associations should be serialized.
     * @param idx A (upto date) type instance index.
     * @return An unsorted array of associations which must be included into the output.
     */
    @SuppressWarnings("unchecked")
    private Association[] _fetchAssociations(TopicMap tm, ITypeInstanceIndex idx) {
        Collection<Topic> types = idx.getTopicTypes();
        if (types.isEmpty()) {
            Set<Association> assocs = tm.getAssociations();
            return assocs.toArray(new Association[assocs.size()]);
        }
        else {
            List<Association> assocs = new ArrayList<Association>(tm.getAssociations());
            for (Topic type: types) {
                for (Topic instance: idx.getTopics(type)) {
                    assocs.add(new TypeInstanceAssociation(type, instance));
                }
            }
            return assocs.toArray(new Association[assocs.size()]);
        }
    }

    /**
     * Creates the index on which the canonicalizer operates.
     * 
     * As sideeffect, the provided topic and association arrays get sorted.
     *
     * @param topics An array of topics.
     * @param assocs An array of associations.
     */
    @SuppressWarnings("unchecked")
    private void _createIndex(Topic[] topics, Association[] assocs) {
        Arrays.sort(topics, _topicComparator);
        Topic topic = null;
        for (int i=0; i < topics.length; i++) {
            topic = topics[i];
            _construct2Id.put(topic, new Integer(i+1));
            Set<Occurrence> occs_ = topic.getOccurrences();
            Occurrence[] occs = occs_.toArray(new Occurrence[occs_.size()]);
            Arrays.sort(occs, _occComparator);
            _topic2Occs.put(topic, occs);
            for (int j=0; j < occs.length; j++) {
                _construct2Id.put(occs[j], new Integer(j+1));
            }
            Set<TopicName> names_ = topic.getTopicNames();
            TopicName[] names = names_.toArray(new TopicName[names_.size()]);
            Arrays.sort(names, _nameComparator);
            _topic2Names.put(topic, names);
            for (int j=0; j < names.length; j++) {
                TopicName name = names[j];
                _construct2Id.put(name, new Integer(j+1));
                Set<Variant> variants_ = name.getVariants();
                Variant[] variants = variants_.toArray(new Variant[variants_.size()]);
                Arrays.sort(variants, _variantComparator);
                _name2Variants.put(name, variants);
                for (int k=0; k < variants.length; k++) {
                    _construct2Id.put(variants[k], new Integer(k+1));
                }
            }
        }
        Arrays.sort(assocs, _assocComparator);
        Association assoc = null;
        for (int i=0; i < assocs.length; i++) {
            assoc = assocs[i];
            _construct2Id.put(assoc, new Integer(i+1));
            Set<AssociationRole> roles_ = assoc.getAssociationRoles();
            AssociationRole[] roles = roles_.toArray(new AssociationRole[roles_.size()]);
            Arrays.sort(roles, _roleComparator);
            _assoc2Roles.put(assoc, roles);
            for (int j=0; j < roles.length; j++) {
                _construct2Id.put(roles[j], new Integer(j+1));
            }
        }
    }

    /**
     * Returns a sorted array of roles of the provided association.
     *
     * @param assoc The association to retrieve the roles from.
     * @return A (maybe empty) sorted array of roles.
     */
    private AssociationRole[] _getRoles(Association assoc) {
        AssociationRole[] roles = _assoc2Roles.get(assoc);
        return roles != null ? roles : _EMPTY_ROLES;
    }

    /**
     * Returns a sorted array of names of the provided topic.
     *
     * @param topic The topic to retrieve the names from.
     * @return A (maybe empty) sorted array of names.
     */
    private TopicName[] _getNames(Topic topic) {
        TopicName[] names = _topic2Names.get(topic);
        return names != null ? names : _EMPTY_NAMES;
    }

    /**
     * Returs a sorted array of variants of the provided name.
     *
     * @param name The name to retrieve the variants from.
     * @return A (maybe empty) sorted array of variants.
     */
    private Variant[] _getVariants(TopicName name) {
        Variant[] variants = _name2Variants.get(name);
        return variants != null ? variants : _EMPTY_VARIANTS;
    }

    /**
     * Returns a sorted array of occurrences of the provided topic.
     *
     * @param topic The topic to retrieve the occurrences from.
     * @return A (maybe emtpy) sorted array of occurrences.
     */
    private Occurrence[] _getOccurrences(Topic topic) {
        Occurrence[] occs = _topic2Occs.get(topic);
        return occs != null ? occs : _EMPTY_OCCS;
    }

    /**
     * Returns the index of the provided Topic Maps construct.
     * 
     * The "index" is <cite>"[...] the string encoding of the position of this 
     * information item in the canonically ordered list of the values from 
     * that set".</cite> (CXTM 3.20 Constructing the number attribute).
     *
     * @param tmo The Topic Maps construct to return the index of.
     * @return The index of the Topic Maps construct.
     */
    private String _indexOf(TopicMapObject tmo) {
        return _construct2Id.get(tmo).toString();
    }

    /**
     * Serializes the <code>topic</code>.
     *
     * @param topic The topic to serialize.
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void _writeTopic(Topic topic) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        _addReified(attrs, topic);
        attrs.addAttribute("", "number", null, null, _indexOf(topic));
        _out.startElement("topic", attrs);
        _out.newline();
        _writeLocatorSet("subjectIdentifiers", topic.getSubjectIdentifiers());
        _writeLocatorSet("subjectLocators", topic.getSubjectLocators());
        _writeItemIdentifiers(topic);
        for (TopicName name: _getNames(topic)) {
            _writeName(name);
        }
        for (Occurrence occ: _getOccurrences(topic)) {
            _writeOccurrence(occ);
        }
        Set<AssociationRole> roles_ = new HashSet<AssociationRole>(topic.getRolesPlayed());
        List<AssociationRole> alienRoles = _topic2Roles.get(topic);
        if (alienRoles != null) {
            roles_.addAll(alienRoles);
        }
        AssociationRole[] roles = roles_.toArray(new AssociationRole[roles_.size()]);
        Arrays.sort(roles, _roleComparator);
        AttributesImpl roleAttrs = new AttributesImpl();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < roles.length; i++) {
            sb.append("association.")
                .append(_indexOf(roles[i].getAssociation()))
                .append(".role.")
                .append(_indexOf(roles[i]));
            roleAttrs.addAttribute("", "ref", null, null, sb.toString());
            _out.startElement("rolePlayed", roleAttrs);
            _out.endElement("rolePlayed");
            _out.newline();
            sb.setLength(0);
            roleAttrs.clear();
        }
        _out.endElement("topic");
        _out.newline();
    }

    /**
     * Serializes an association.
     *
     * @param assoc The association to serialize.
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void _writeAssociation(Association assoc) throws IOException {
        _out.startElement("association", _attributes(assoc));
        _out.newline();
        _writeType((ITyped) assoc);
        for (AssociationRole role: _getRoles(assoc)) {
            _out.startElement("role", _attributes(role));
            _out.newline();
            _out.startElement("player", _topicRef(role.getPlayer()));
            _out.endElement("player");
            _out.newline();
            _writeType((ITyped) role);
            _out.endElement("role");
            _out.newline();
        }
        _writeScope(assoc);
        _writeItemIdentifiers(assoc);
        _out.endElement("association");
        _out.newline();
    }

    /**
     * Serializes an occurrence.
     *
     * @param occ The occurrence to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeOccurrence(Occurrence occ) throws IOException {
        _out.startElement("occurrence", _attributes(occ));
        _out.newline();
        _writeDatatyped((IDatatypeAwareConstruct) occ);
        _writeType((ITyped) occ);
        _writeScope(occ);
        _writeItemIdentifiers(occ);
        _out.endElement("occurrence");
        _out.newline();
    }

    /**
     * Writes the value/datatype pair of an occurrence or variant.
     *
     * @param obj The construct to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeDatatyped(IDatatypeAwareConstruct obj) throws IOException {
        String value = obj.getValue2();
        String datatype = obj.getDatatype().getReference();
        if (_XSD_ANY_URI.equals(datatype)) {
            value = _normalizeLocator(value);
        }
        _out.startElement("value");
        _out.characters(value);
        _out.endElement("value");
        _out.newline();
        _out.startElement("datatype");
        _out.characters(_normalizeLocator(datatype));
        _out.endElement("datatype");
        _out.newline();
    }

    /**
     * Serializes a topic name.
     *
     * @param name The name to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeName(TopicName name) throws IOException {
        _out.startElement("topicName", _attributes(name));
        _out.newline();
        _out.startElement("value");
        _out.characters(name.getValue());
        _out.endElement("value");
        _out.newline();
        _writeType((ITyped) name);
        _writeScope(name);
        for (Variant variant: _getVariants(name)) {
            _out.startElement("variant", _attributes(variant));
            _out.newline();
            _writeDatatyped((IDatatypeAwareConstruct) variant);
            _writeScope(variant);
            _writeItemIdentifiers(variant);
            _out.endElement("variant");
            _out.newline();
        }
        _writeItemIdentifiers(name);
        _out.endElement("topicName");
        _out.newline();
    }

    /**
     * Serializes the type of a typed Topic Maps construct.
     *
     * @param typed The typed Topic Maps construct from which the type should be
     *                  serialized.
     * @throws IOException If an error occurs.
     */
    private void _writeType(ITyped typed) throws IOException {
        Topic type = typed.getType();
        if (type == null) {
            _reportInvalid("The type of " + typed + " is null");
        }
        _out.startElement("type", _topicRef(typed.getType()));
        _out.endElement("type");
        _out.newline();
    }

    /**
     * Serializes the scope of a scoped Topic Maps construct.
     * 
     * If the scope is unconstrained, this method does nothing.
     *
     * @param scoped The scoped Topic Maps construct.
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void _writeScope(ScopedObject scoped) throws IOException {
        Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return;
        }
        _out.startElement("scope");
        _out.newline();
        Topic[] themes = scope.toArray(new Topic[scope.size()]);
        Arrays.sort(themes, _topicComparator);
        for (int i=0; i < themes.length; i++) {
            _out.startElement("scopingTopic", _topicRef(themes[i]));
            _out.endElement("scopingTopic");
            _out.newline();
        }
        _out.endElement("scope");
        _out.newline();
    }

    /**
     * Serializes a locator.
     * 
     * A normalized locator value is created which is serialized.
     *
     * @param loc The locator to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeLocator(Locator loc) throws IOException {
        _out.startElement("locator");
        _out.characters(_normalizeLocator(loc.getReference()));
        _out.endElement("locator");
        _out.newline();
    }

    /**
     * Serializes the item identifiers of the specified Topic Maps construct.
     *
     * @param tmo The Topic Maps construct to take the item identifiers from.
     * @throws IOException If an error occurs.
     */
    @SuppressWarnings("unchecked")
    private void _writeItemIdentifiers(TopicMapObject tmo) throws IOException {
        _writeLocatorSet("itemIdentifiers", tmo.getSourceLocators());
    }

    /**
     * Serializes the <code>locators</code> using the <code>localName</code> as
     * element name.
     * 
     * If the set of <code>locators</code> is empty, this method does nothing.
     *
     * @param localName The element's name.
     * @param locators The locators to serialize.
     * @throws IOException If an error occurs. 
     */
    private void _writeLocatorSet(String localName, Set<Locator> locators) throws IOException {
        if (locators.isEmpty()) {
            return;
        }
        Locator[] locs = locators.toArray(new Locator[locators.size()]);
        Arrays.sort(locs, _locComparator);
        _out.startElement(localName);
        for (int i=0; i < locs.length; i++) {
            _writeLocator(locs[i]);
        }
        _out.endElement(localName);
        _out.newline();
    }

    /**
     * Returns attributes which contains a reference to the provided topic.
     *
     * @param topic The topic to which the reference should point to.
     * @return Attributes with a topic reference.
     */
    private Attributes _topicRef(Topic topic) {
        if (topic == null) {
            _reportInvalid("The topic reference is null");
            return CXTMWriter.EMPTY_ATTRS;
        }
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "topicref", null, null, _indexOf(topic));
        return attrs;
    }

    /**
     * Returns attributes which contain the reifier (if any) and the number
     * of the provided Topic Maps construct (not a topic).
     *
     * @param reifiable The Topic Maps construct.
     * @return Attributes which contain a reference to the reifier (if any) and
     *          the number of the provided Topic Maps construct.
     */
    private Attributes _attributes(TopicMapObject reifiable) {
        AttributesImpl attrs = new AttributesImpl();
        _addReifier(attrs, (IReifiable)reifiable);
        attrs.addAttribute("", "number", null, null, _indexOf(reifiable));
        return attrs;
    }

    /**
     * Adds a reference to the reifier of the Topic Maps construct to the 
     * provided attributes. If the Topic Maps construct has no reifier, the
     * provided attributes are not modified.
     *
     * @param attrs The attributes.
     * @param reifiable The reifiable Topic Maps construct.
     */
    private void _addReifier(AttributesImpl attrs, IReifiable reifiable) {
        Topic reifier = reifiable.getReifier();
        if (reifier != null) {
            attrs.addAttribute("", "reifier", null, null, _indexOf(reifier));
        }
    }

    /**
     * Adds a reference to the Topic Maps construct which is reified by the
     * provided topic.
     * 
     * If the topic reifies no Topic Maps construct, the attributes are not
     * modified.
     *
     * @param attrs The attributes to add the reference to.
     * @param topic The topic.
     */
    private void _addReified(AttributesImpl attrs, Topic topic) {
        if (topic instanceof TypeInstanceTopic) {
            return;
        }
        IReifiable reifiable = ((TopicImpl) topic).getReifiedConstruct();
        if (reifiable == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (reifiable instanceof TopicMap) {
            sb.append("topicMap");
        }
        else if (reifiable instanceof Association) {
            sb.append("association.")
                .append(_indexOf(reifiable));
        }
        else if (reifiable instanceof AssociationRole) {
            sb.append("association.")
                .append(_indexOf(reifiable.getParent()))
                .append(".role.")
                .append(_indexOf(reifiable));
        }
        else {
            sb.append("topic.");
            final IConstruct parent = reifiable.getParent();
            if (reifiable instanceof Occurrence) {
                sb.append(_indexOf(parent))
                    .append(".occurrence.");
            }
            else if (reifiable instanceof TopicName) {
                sb.append(_indexOf(parent))
                    .append(".name.");
            }
            else if (reifiable instanceof Variant) {
                sb.append(_indexOf(parent.getParent()))
                    .append(".name.")
                    .append(_indexOf(parent))
                    .append(".variant.");
            }
            sb.append(_indexOf(reifiable));
        }
        attrs.addAttribute("", "reifier", null, null, sb.toString());
    }

    /**
     * Normalizes the locator according to CXTM 3.19.
     *
     * @param locator The locator to normalize.
     * @return A normalized representation of the locator.
     */
    private String _normalizeLocator(String locator) {
        String normLoc = _locator2Norm.get(locator);
        if (normLoc != null) {
            return normLoc;
        }
        normLoc = locator;
        if (locator.startsWith(_normBase)) {
            normLoc = locator.substring(_normBase.length());
        }
        else {
            int i = 0;
            int slashPos = -1;
            final int max = _normBase.length() < locator.length() ? _normBase.length()
                                                                   : locator.length();
            while(i < max && _normBase.charAt(i) == locator.charAt(i)) {
                if (_normBase.charAt(i) == '/') {
                    slashPos = i;
                }
                i++;
            }
            if (slashPos > -1) {
                normLoc = locator.substring(slashPos);
            }
        }
        if (normLoc.startsWith("/")) {
            normLoc = normLoc.substring(1);
        }
        _locator2Norm.put(locator, normLoc);
        return normLoc;
    }

    /**
     * Normalizes the base locator according to the following procedure 
     * (CXTM 3.19 - 1.):
     * <cite>[...] the base locator with any fragment identifier and query 
     * removed and any trailing "/" character removed.[...]</cite>
     *
     * @param baseLocator
     * @return
     */
    private static String _normalizeBaseLocator(String baseLocator) {
        String loc = baseLocator;
        int i = loc.indexOf('#');
        if (i > 0) {
            loc = loc.substring(0, i);
        }
        i = loc.indexOf('?');
        if (i > 0) {
            loc = loc.substring(0, i);
        }
        if (loc.endsWith("/")) {
            loc = loc.substring(0, loc.length()-1);
        }
        return loc;
    }

    /**
     * Writes a warning msg to the log.
     * 
     * This method is used to inform the user that the serialized topic map
     * is not valid acc. to CXTM.
     *
     * @param msg The warning message.
     */
    private static void _reportInvalid(String msg) {
        LOG.warning("Invalid CXTM: '" + msg + "'");
    }


    /*
     * Comparators.
     */

    private final class TopicComparator implements Comparator<Topic> {

        @SuppressWarnings("unchecked")
        public int compare(Topic o1, Topic o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 != null && o2 == null) {
                _reportInvalid("Comparing topics where one topic is null");
                return +1;
            }
            else if (o1 == null && o2 != null) {
                _reportInvalid("Comparing topics where one topic is null");
                return -1;
            }
            int res = _locSetComparator.compare(o1.getSubjectIdentifiers(), o2.getSubjectIdentifiers());
            if (res == 0) {
                res = _locSetComparator.compare(o1.getSubjectLocators(), o2.getSubjectLocators());
                if (res == 0) {
                    res = _locSetComparator.compare(o1.getSourceLocators(), o2.getSourceLocators());
                }
            }
            return res;
        }
    }

    /**
     * Abstract comparator that provides some utility methods which handle common 
     * comparisons.
     */
    private abstract class AbstractComparator<T> implements Comparator<T> {
        int compareString(String o1, String o2) {
            if (o1 == null && o2 != null) {
                _reportInvalid("The first string value is null");
                return -1;
            }
            if (o1 != null && o2 == null) {
                _reportInvalid("The second string value is null");
                return +1;
            }
            return o1.compareTo(o2);
        }
        /**
         * Extracts the type of the typed Topic Maps constructs and compares
         * the topics.
         *
         * @param o1 The first typed Topic Maps construct.
         * @param o2 The second typed Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int compareType(ITyped o1, ITyped o2) {
            return _topicComparator.compare(o1.getType(), o2.getType());
        }
        /**
         * Extracts the scope of the scoped Topic Maps constructs and compares
         * them.
         *
         * @param o1 The first scoped Topic Maps construct.
         * @param o2 The second scoped Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        @SuppressWarnings("unchecked")
        int compareScope(ScopedObject o1, ScopedObject o2) {
            return _scopeComparator.compare(o1.getScope(), o2.getScope());
        }
    }

    /**
     * Enhances the {@link AbstractComparator} with a method to compare the
     * value and datatype of an occurrence or variant.
     */
    private abstract class AbstractDatatypeAwareComparator<T> extends AbstractComparator<T> {
        /**
         * Compares the value and datatype of the occurrences / variants.
         *
         * @param o1 The first occurrence / variant.
         * @param o2 The second occurrence / variant.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int _compareValueDatatype(IDatatypeAwareConstruct o1, IDatatypeAwareConstruct o2) {
            int res = compareString(o1.getValue2(), o2.getValue2());
            if (res == 0) {
                res = compareString(o1.getDatatype().getReference(), o2.getDatatype().getReference());
            }
            return res;
        }
    }

    /**
     * Canonical sort order:
     * 1. [type]
     * 2. [roles]
     * 3. [scope]
     * 4. [parent]
     */
    private final class AssociationComparator extends AbstractComparator<Association> {

        private Comparator<Set<AssociationRole>> _roleSetComparator;

        AssociationComparator() {
            _roleSetComparator = new RoleSetComparator();
        }

        @SuppressWarnings("unchecked")
        public int compare(Association o1, Association o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType((ITyped) o1, (ITyped) o2);
            if (res == 0) {
                res = _roleSetComparator.compare(o1.getAssociationRoles(), o2.getAssociationRoles());
                if (res == 0) {
                    res = compareScope(o1, o2);
                }
            }
            return res;
        }
    }

    /**
     * Role comparator which ignores the parent association. This comparator
     * is meant to be used for roles where the parent is known to be equal or
     * unequal.
     */
    private class RoleIgnoreParentComparator extends AbstractComparator<AssociationRole> {

        public int compare(AssociationRole o1, AssociationRole o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = _topicComparator.compare(o1.getPlayer(), o2.getPlayer());
            if (res == 0) {
                res = compareType((ITyped) o1, (ITyped) o2);
            }
            return res;
        }
    }

    /**
     * Canonical sort order:
     * 1. [player]
     * 2. [type]
     * 3. [parent]
     */
    private final class RoleComparator extends RoleIgnoreParentComparator {

        public int compare(AssociationRole o1, AssociationRole o2) {
            int res = super.compare(o1, o2);
            if (res == 0) {
                res = _assocComparator.compare(o1.getAssociation(), o2.getAssociation());
            }
            return res;
        }
    }

    /**
     * Canonical sort order:
     * 1. [value]
     * 2. [datatype]
     * 3. [type]
     * 4. [scope]
     * 5. [parent]
     */
    private final class OccurrenceComparator extends AbstractDatatypeAwareComparator<Occurrence> {

        public int compare(Occurrence o1, Occurrence o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = _compareValueDatatype((IDatatypeAwareConstruct) o1, (IDatatypeAwareConstruct) o2);
            if (res == 0) {
                res = compareType((ITyped) o1, (ITyped) o2);
                if (res == 0) {
                    res = compareScope(o1, o2);
                }
            }
            return res;
        }
        
    }

    /**
     * Canonical sort order:
     * 1. [value]
     * 2. [type]
     * 3. [scope]
     * 4. [parent]
     */
    private final class NameComparator extends AbstractComparator<TopicName> {

        public int compare(TopicName o1, TopicName o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareString(o1.getValue(), o2.getValue());
            if (res == 0) {
                res = compareType((ITyped) o1, (ITyped) o2);
                if (res == 0) {
                    res = compareScope(o1, o2);
                }
            }
            return res;
        }
    }

    /**
     * Canonical sort order:
     * 1. [value]
     * 2. [datatype]
     * 3. [scope]
     * 4. [parent]
     */
    private final class VariantComparator extends AbstractDatatypeAwareComparator<Variant> {

        public int compare(Variant o1, Variant o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = _compareValueDatatype((IDatatypeAwareConstruct) o1, (IDatatypeAwareConstruct) o2);
            if (res == 0) {
                res = compareScope(o1, o2);
            }
            return res;
        }
    }

    /**
     * Comparator which compares the size of the provided set.
     * 
     * Iff the size of the sets are equal, another comparison method is used
     * to compare the content of the sets.
     */
    private abstract class AbstractSetComparator<T> implements Comparator<Set<T>> {

        public int compare(Set<T> o1, Set<T> o2) {
            int s1 = o1.size();
            int s2 = o2.size();
            int res = s1 - s2;
            if (res == 0) {
                res = compareContent(o1, o2, s1);
            }
            return res;
        }

        /**
         * Called iff the size of the sets is equal.
         * 
         * This method is used to compare the content of the sets.
         *
         * @param o1 The first set.
         * @param o2 The second set.
         * @param size The size of the set(s).
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        abstract int compareContent(Set<T> o1, Set<T> o2, int size);
    }

    /**
     * Compares role sets. The parent of the roles is ignored! 
     */
    private final class RoleSetComparator extends AbstractSetComparator<AssociationRole> {

        private RoleIgnoreParentComparator _roleCmp; 

        RoleSetComparator() {
            _roleCmp = new RoleIgnoreParentComparator();
        }

        @Override
        int compareContent(Set<AssociationRole> o1, Set<AssociationRole> o2,
                int size) {
            int res = 0;
            AssociationRole[] roles1 = o1.toArray(new AssociationRole[size]);
            AssociationRole[] roles2 = o2.toArray(new AssociationRole[size]);
            Arrays.sort(roles1, _roleCmp);
            Arrays.sort(roles2, _roleCmp);
            for (int i=0; i < size && res == 0; i++) {
                res = _roleCmp.compare(roles1[i], roles2[i]);
            }
            return res;
        }
        
    }

    /**
     * Compares the scope of two scoped Topic Maps constructs.
     */
    private final class ScopeComparator extends AbstractSetComparator<Topic> {

        @Override
        int compareContent(Set<Topic> o1, Set<Topic> o2, int size) {
            int res = 0 ;
            Topic[] topics1 = o1.toArray(new Topic[size]);
            Topic[] topics2 = o2.toArray(new Topic[size]);
            Arrays.sort(topics1, _topicComparator);
            Arrays.sort(topics2, _topicComparator);
            for (int i=0; i < size && res == 0; i++) {
                res = _topicComparator.compare(topics1[i], topics2[i]);
            }
            return res;
        }

    }

    /**
     * Comparator for sets of {@link org.tmapi.core.Locator}s. 
     */
    private final class LocatorSetComparator extends AbstractSetComparator<Locator> {

        @Override
        int compareContent(Set<Locator> o1, Set<Locator> o2, int size) {
            int res = 0;
            Locator[] locs1 = o1.toArray(new Locator[size]);
            Locator[] locs2 = o2.toArray(new Locator[size]);
            Arrays.sort(locs1, _locComparator);
            Arrays.sort(locs2, _locComparator);
            for (int i=0; i < size && res == 0; i++) {
                res = _locComparator.compare(locs1[i], locs2[i]);
            }
            return res;
        }
    }

    /**
     * Compares {@link org.tmapi.core.Locator}s.
     */
    private final class LocatorComparator implements Comparator<Locator> {

        public int compare(Locator o1, Locator o2) {
            if (o1 == o2) {
                return 0;
            }
            return _normalizeLocator(o1.getReference()).compareTo(_normalizeLocator(o2.getReference()));
        }
        
    }


    /*
     * Helper classes to treat type-instance relationships, modelled as property
     * of a topic, as associations. 
     */

    @SuppressWarnings("unchecked")
    private final class TypeInstanceTopic implements Topic {

        private final Set<Locator> _sids;

        TypeInstanceTopic(Locator sid) {
            _sids = Collections.singleton(sid);
        }

        public Set<Locator> getSubjectIdentifiers() {
            return _sids;
        }

        public void addSourceLocator(Locator arg0) throws DuplicateSourceLocatorException, MergeException { }
        public void addSubjectIdentifier(Locator arg0) throws MergeException {}
        public void addSubjectLocator(Locator arg0) throws MergeException, ModelConstraintException {}
        public void addType(Topic arg0) {}
        public Occurrence createOccurrence(String arg0, Topic arg1, Collection arg2) { return null; }
        public Occurrence createOccurrence(Locator arg0, Topic arg1, Collection arg2) { return null; }
        public TopicName createTopicName(String arg0, Collection arg1) throws MergeException { return null; }
        public TopicName createTopicName(String arg0, Topic arg1, Collection arg2) throws UnsupportedOperationException, MergeException { return null; }
        public Set getOccurrences() { return Collections.emptySet(); }
        public Set getReified() { return null; }
        public Set getRolesPlayed() { return Collections.emptySet(); }
        public Set getSubjectLocators() { return Collections.emptySet(); }
        public Set getTopicNames() { return Collections.emptySet(); }
        public Set getTypes() { return null; }
        public void mergeIn(Topic arg0) throws MergeException { }
        public void remove() throws TopicInUseException { }
        public void removeSubjectIdentifier(Locator arg0) { }
        public void removeSubjectLocator(Locator arg0) { }
        public void removeType(Topic arg0) { }
        public String getObjectId() { return null; }
        public Set getSourceLocators() { return Collections.emptySet(); }
        public TopicMap getTopicMap() { return null; }
        public void removeSourceLocator(Locator arg0) { }
    }

    /**
     * Used to represent type-instance relationships which are modelled as
     * [type] property of topics.
     */
    @SuppressWarnings("unchecked")
    private final class TypeInstanceAssociation implements Association, IReifiable, ITyped {

        final Set<AssociationRole> _roles; 

        TypeInstanceAssociation(Topic type, Topic instance) {
            AssociationRole typeRole = new TypeInstanceRole(this, _type, type);
            AssociationRole instanceRole = new TypeInstanceRole(this, _instance, instance);
            _roles = new TypeInstanceRoleSet(typeRole, instanceRole);
        }

        public Set<AssociationRole> getAssociationRoles() {
            return _roles;
        }

        public Topic getType() {
            return _typeInstance;
        }

        public void setReifier(Topic reifier) { }
        public void addItemIdentifier(Locator itemIdentifier) { }
        public Set<Locator> getItemIdentifiers() { return Collections.emptySet(); }
        public IConstruct getParent() { return null; }
        public void removeItemIdentifier(Locator itemIdentifier) { }
        public AssociationRole createAssociationRole(Topic arg0, Topic arg1) { return null; }
        public Topic getReifier() { return null; }
        public void remove() throws TMAPIException {}
        public void setType(Topic arg0) {}
        public void addScopingTopic(Topic arg0) {}
        public Set getScope() { return Collections.emptySet(); }
        public void removeScopingTopic(Topic arg0) {}
        public void addSourceLocator(Locator arg0) throws DuplicateSourceLocatorException {}
        public String getObjectId() { return null; }
        public Set getSourceLocators() { return Collections.emptySet(); }
        public TopicMap getTopicMap() { return null; }
        public void removeSourceLocator(Locator arg0) {}
    }

    /**
     * Immutable association role.
     */
    @SuppressWarnings("unchecked")
    private class TypeInstanceRole implements AssociationRole , IReifiable, ITyped {
        private final Topic _type;
        private final Topic _player;
        private final Association _parent;

        TypeInstanceRole(Association parent, Topic type, Topic player) {
            _type = type;
            _player = player;
            _parent = parent;
            List<AssociationRole> roles = _topic2Roles.get(player);
            if (roles == null) {
                roles = new ArrayList<AssociationRole>();
                _topic2Roles.put(player, roles);
            }
            roles.add(this);
        }

        public Topic getType() {
            return _type;
        }

        public Topic getPlayer() {
            return _player;
        }

        public void setReifier(Topic reifier) { }
        public void addItemIdentifier(Locator itemIdentifier) { }
        public Set<Locator> getItemIdentifiers() { return Collections.emptySet(); }
        public IConstruct getParent() { return (IConstruct) _parent; }
        public void removeItemIdentifier(Locator itemIdentifier) { }
        public Association getAssociation() { return _parent; }
        public Topic getReifier() { return null; }
        public void remove() throws TMAPIException {}
        public void setPlayer(Topic arg0) {}
        public void setType(Topic arg0) {}
        public void addSourceLocator(Locator arg0) throws DuplicateSourceLocatorException {}
        public String getObjectId() { return null; }
        public Set getSourceLocators() { return Collections.emptySet(); }
        public TopicMap getTopicMap() { return null; }
        public void removeSourceLocator(Locator arg0) {}
    }

    /**
     * Immutable 'set' of two roles.
     */
    private static class TypeInstanceRoleSet extends AbstractSet<AssociationRole> {

        private final AssociationRole _role1;
        private final AssociationRole _role2;

        TypeInstanceRoleSet(AssociationRole role1, AssociationRole role2) {
            _role1 = role1;
            _role2 = role2;
        }

        @Override
        public Iterator<AssociationRole> iterator() {
            return new TypeInstanceRoleSetIterator();
        }

        @Override
        public int size() {
            return 2;
        }

        private class TypeInstanceRoleSetIterator implements Iterator<AssociationRole> {

            private int _idx;

            public boolean hasNext() {
                return _idx < 2;
            }

            public AssociationRole next() {
                if (_idx > 1) {
                    throw new NoSuchElementException();
                }
                return 0 == _idx++ ? _role1 : _role2; 
            }

            public void remove() {
                new UnsupportedOperationException();
            }
        }
    }

}
