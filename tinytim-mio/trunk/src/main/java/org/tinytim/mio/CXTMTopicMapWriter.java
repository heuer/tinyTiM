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

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.utils.CollectionFactory;
import org.tinytim.utils.DuplicateRemovalUtils;
import org.tinytim.voc.TMDM;
import org.tinytim.voc.XSD;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;
import org.tmapi.index.TypeInstanceIndex;

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
 * <a href="http://www.isotopicmaps.org/cxtm/">CXTM draft dtd. 2008-05-15</a>,
 * the output may change in the future.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class CXTMTopicMapWriter implements TopicMapWriter {

    private static final Logger LOG = Logger.getLogger(CXTMTopicMapWriter.class.getName());

    private static final Role[] _EMPTY_ROLES = new Role[0];

    private final AttributesImpl _attrs;

    private Topic _type;
    private Topic _instance;
    private Topic _typeInstance;

    private final XMLC14NWriter _out;
    private final String _normBase;
    private final Map<Construct, Integer> _construct2Id;
    private final Map<Topic, List<Role>> _topic2Roles;
    private final Map<Locator, String> _locator2Norm;
    private final Map<Association, Role[]> _assoc2Roles;

    private final Comparator<Topic> _topicComparator;
    private final Comparator<Association> _assocComparator;
    private final Comparator<Role> _roleComparator;
    private final Comparator<Occurrence> _occComparator;
    private final Comparator<Name> _nameComparator;
    private final Comparator<Variant> _variantComparator;
    private final Comparator<Set<Locator>> _locSetComparator;
    private final Comparator<Locator> _locComparator;
    private final Comparator<Set<Topic>> _scopeComparator;

    /**
     * Creates a canonicalizer.
     *
     * @param out The stream the CXTM is written onto.
     * @param baseLocator The base locator which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public CXTMTopicMapWriter(OutputStream out, String baseLocator) throws IOException {
        if (baseLocator == null) {
            throw new IllegalArgumentException("The base locator must not be null");
        }
        _out = new XMLC14NWriter(out);
        _attrs = new AttributesImpl();
        _normBase = _normalizeBaseLocator(baseLocator);
        _construct2Id = CollectionFactory.createIdentityMap();
        _locator2Norm = CollectionFactory.createIdentityMap();
        _assoc2Roles = CollectionFactory.createIdentityMap();
        _topic2Roles = CollectionFactory.createIdentityMap();
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
     * Serializes the specified <tt>topicMap</tt> into the CXTM format.
     * <p>
     * <em>CAUTION</em>: This method MAY modify the topic map since duplicate 
     * Topic Maps constructs (if any) are removed in advance.
     * </p>
     * 
     * @param topicMap The topic map to serialize.
     * @throws IOException If an error occurs.
     */
    public void write(TopicMap topicMap) throws IOException {
        DuplicateRemovalUtils.removeDuplicates(topicMap);
        TypeInstanceIndex typeInstanceIndex = ((IIndexManagerAware)topicMap).getIndexManager().getTypeInstanceIndex();
        if (!typeInstanceIndex.isAutoUpdated()) {
            typeInstanceIndex.reindex();
        }
        Topic[] topics = _fetchTopics(topicMap, typeInstanceIndex);
        Association[] assocs = _fetchAssociations(topicMap, typeInstanceIndex);
        typeInstanceIndex.close();
        _createIndex(topics, assocs);
        _out.startDocument();
        _attrs.clear();
        _addReifier(_attrs, topicMap);
        _out.startElement("topicMap", _attrs);
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
        _attrs.clear();
        _construct2Id.clear();
        _topic2Roles.clear();
        _locator2Norm.clear();
        _assoc2Roles.clear();
    }

    /**
     * Returns an unsorted array of topics which should be included into
     * the output.
     * <p>
     * This method may return more topics than {@link TopicMap#getTopics()}
     * since this method creates virtual topics to model type-instance
     * relationships properly.
     * </p>
     * 
     * @param topicMap The topic map from which the topic should be serialized.
     * @param idx A (upto date) type instance index.
     * @return All topics which must be included into the output.
     */
    private Topic[] _fetchTopics(final TopicMap topicMap, final TypeInstanceIndex idx) {
        Collection<Topic> types = idx.getTopicTypes();
        if (types.isEmpty()) {
            Set<Topic> topics = topicMap.getTopics();
            return topics.toArray(new Topic[topics.size()]);
        }
        else {
            List<Topic> topics = CollectionFactory.createList(topicMap.getTopics());
            _typeInstance = _getTopicBySubjectIdentifier(topicMap, topics, TMDM.TYPE_INSTANCE);
            _type = _getTopicBySubjectIdentifier(topicMap, topics, TMDM.TYPE);
            _instance = _getTopicBySubjectIdentifier(topicMap, topics, TMDM.INSTANCE);
            return topics.toArray(new Topic[topics.size()]);
        }
    }

    /**
     * Returns a topic by its subject identifier. If the topic is null, a 
     * {@link TypeInstanceTopic} is created, added to the <tt>topics</tt>
     * and returned.
     *
     * @param tm The topic map to fetch the topic from.
     * @param topics A modifiable collection of topics.
     * @param sid The subject identifier.
     * @return A topic with the specified subject identifier.
     */
    private Topic _getTopicBySubjectIdentifier(TopicMap tm, Collection<Topic> topics, Locator sid) {
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
    private Association[] _fetchAssociations(final TopicMap tm, final TypeInstanceIndex idx) {
        Collection<Topic> types = idx.getTopicTypes();
        if (types.isEmpty()) {
            Set<Association> assocs = tm.getAssociations();
            return assocs.toArray(new Association[assocs.size()]);
        }
        else {
            List<Association> assocs = CollectionFactory.createList(tm.getAssociations());
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
    private void _createIndex(Topic[] topics, Association[] assocs) {
        Arrays.sort(topics, _topicComparator);
        Topic topic = null;
        for (int i=0; i < topics.length; i++) {
            topic = topics[i];
            _construct2Id.put(topic, Integer.valueOf(i+1));
        }
        Arrays.sort(assocs, _assocComparator);
        Association assoc = null;
        for (int i=0; i < assocs.length; i++) {
            assoc = assocs[i];
            _construct2Id.put(assoc, Integer.valueOf(i+1));
            Set<Role> roles_ = assoc.getRoles();
            Role[] roles = roles_.toArray(new Role[roles_.size()]);
            Arrays.sort(roles, _roleComparator);
            _assoc2Roles.put(assoc, roles);
            for (int j=0; j < roles.length; j++) {
                _construct2Id.put(roles[j], Integer.valueOf(j+1));
            }
        }
    }

    /**
     * Returns a sorted array of roles of the provided association.
     *
     * @param assoc The association to retrieve the roles from.
     * @return A (maybe empty) sorted array of roles.
     */
    private Role[] _getRoles(final Association assoc) {
        Role[] roles = _assoc2Roles.get(assoc);
        return roles != null ? roles : _EMPTY_ROLES;
    }

    /**
     * Returns a sorted array of names of the provided topic.
     *
     * @param topic The topic to retrieve the names from.
     * @return A (maybe empty) sorted array of names.
     */
    private Name[] _getNames(final Topic topic) {
        Set<Name> names_ = topic.getNames();
        Name[] names = names_.toArray(new Name[names_.size()]);
        Arrays.sort(names, _nameComparator);
        return names;
    }

    /**
     * Returs a sorted array of variants of the provided name.
     *
     * @param name The name to retrieve the variants from.
     * @return A (maybe empty) sorted array of variants.
     */
    private Variant[] _getVariants(final Name name) {
        Set<Variant> variants_ = name.getVariants();
        Variant[] variants = variants_.toArray(new Variant[variants_.size()]);
        Arrays.sort(variants, _variantComparator);
        return variants;
    }

    /**
     * Returns a sorted array of occurrences of the provided topic.
     *
     * @param topic The topic to retrieve the occurrences from.
     * @return A (maybe emtpy) sorted array of occurrences.
     */
    private Occurrence[] _getOccurrences(final Topic topic) {
        Set<Occurrence> occs_ = topic.getOccurrences();
        Occurrence[] occs = occs_.toArray(new Occurrence[occs_.size()]);
        Arrays.sort(occs, _occComparator);
        return occs;
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
    private int _indexOf(final Construct tmo) {
        return _construct2Id.get(tmo).intValue();
    }

    /**
     * Serializes the <tt>topic</tt>.
     *
     * @param topic The topic to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeTopic(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "number", "", "CDATA", Integer.toString(_indexOf(topic)));
        _out.startElement("topic", _attrs);
        _out.newline();
        _writeLocatorSet("subjectIdentifiers", topic.getSubjectIdentifiers());
        _writeLocatorSet("subjectLocators", topic.getSubjectLocators());
        _writeItemIdentifiers(topic);
        Name[] names = _getNames(topic);
        for (int i=0; i < names.length; i++) {
            _writeName(names[i], i+1);
        }
        Occurrence[] occs = _getOccurrences(topic);
        for (int i=0; i < occs.length; i++) {
            _writeOccurrence(occs[i], i+1);
        }
        List<Role> roles_ = CollectionFactory.createList(topic.getRolesPlayed());
        List<Role> alienRoles = _topic2Roles.get(topic);
        if (alienRoles != null) {
            roles_.addAll(alienRoles);
        }
        Role[] roles = roles_.toArray(new Role[roles_.size()]);
        Arrays.sort(roles, _roleComparator);
        StringBuilder sb = new StringBuilder(20);
        for (int i=0; i < roles.length; i++) {
            sb.append("association.")
                .append(_indexOf(roles[i].getParent()))
                .append(".role.")
                .append(_indexOf(roles[i]));
            _attrs.clear();
            _attrs.addAttribute("", "ref", "", "CDATA", sb.toString());
            _out.startElement("rolePlayed", _attrs);
            _out.endElement("rolePlayed");
            _out.newline();
            sb.setLength(0);
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
    private void _writeAssociation(final Association assoc) throws IOException {
        _out.startElement("association", _attributes(assoc, _indexOf(assoc)));
        _out.newline();
        _writeType(assoc);
        for (Role role: _getRoles(assoc)) {
            _out.startElement("role", _attributes(role, _indexOf(role)));
            _out.newline();
            _out.startElement("player", _topicRef(role.getPlayer()));
            _out.endElement("player");
            _out.newline();
            _writeType(role);
            _writeItemIdentifiers(role);
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
     * @param pos The position of the occurrence within the parent container.
     * @throws IOException If an error occurs.
     */
    private void _writeOccurrence(final Occurrence occ, int pos) throws IOException {
        _out.startElement("occurrence", _attributes(occ, pos));
        _out.newline();
        _writeDatatyped(occ);
        _writeType(occ);
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
    private void _writeDatatyped(final DatatypeAware obj) throws IOException {
        final String value = XSD.ANY_URI.equals(obj.getDatatype()) 
                                        ? _normalizeLocator(obj.locatorValue())
                                        : obj.getValue();
        _out.startElement("value");
        _out.characters(value);
        _out.endElement("value");
        _out.newline();
        _out.startElement("datatype");
        _out.characters(obj.getDatatype().getReference());
        _out.endElement("datatype");
        _out.newline();
    }

    /**
     * Serializes a topic name.
     *
     * @param name The name to serialize.
     * @param pos The position of the name within the parent container.
     * @throws IOException If an error occurs.
     */
    private void _writeName(final Name name, int pos) throws IOException {
        _out.startElement("name", _attributes(name, pos));
        _out.newline();
        _out.startElement("value");
        _out.characters(name.getValue());
        _out.endElement("value");
        _out.newline();
        _writeType(name);
        _writeScope(name);
        Variant[] variants = _getVariants(name);
        Variant variant = null;
        for (int i=0; i<variants.length; i++) {
            variant = variants[i];
            _out.startElement("variant", _attributes(variant, i+1));
            _out.newline();
            _writeDatatyped(variant);
            _writeScope(variant);
            _writeItemIdentifiers(variant);
            _out.endElement("variant");
            _out.newline();
        }
        _writeItemIdentifiers(name);
        _out.endElement("name");
        _out.newline();
    }

    /**
     * Serializes the type of a typed Topic Maps construct.
     *
     * @param typed The typed Topic Maps construct from which the type should be
     *                  serialized.
     * @throws IOException If an error occurs.
     */
    private void _writeType(final Typed typed) throws IOException {
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
    private void _writeScope(final Scoped scoped) throws IOException {
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
    private void _writeLocator(final Locator loc) throws IOException {
        _out.startElement("locator");
        _out.characters(_normalizeLocator(loc));
        _out.endElement("locator");
        _out.newline();
    }

    /**
     * Serializes the item identifiers of the specified Topic Maps construct.
     *
     * @param tmo The Topic Maps construct to take the item identifiers from.
     * @throws IOException If an error occurs.
     */
    private void _writeItemIdentifiers(final Construct tmo) throws IOException {
        _writeLocatorSet("itemIdentifiers", tmo.getItemIdentifiers());
    }

    /**
     * Serializes the <tt>locators</tt> using the <tt>localName</tt> as
     * element name.
     * <p>
     * If the set of <tt>locators</tt> is empty, this method does nothing.
     * </p>
     * 
     * @param localName The element's name.
     * @param locators The locators to serialize.
     * @throws IOException If an error occurs. 
     */
    private void _writeLocatorSet(final String localName, final Set<Locator> locators) throws IOException {
        if (locators.isEmpty()) {
            return;
        }
        Locator[] locs = locators.toArray(new Locator[locators.size()]);
        Arrays.sort(locs, _locComparator);
        _out.startElement(localName);
        _out.newline();
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
    private Attributes _topicRef(final Topic topic) {
        if (topic == null) {
            _reportInvalid("The topic reference is null");
            return XMLC14NWriter.EMPTY_ATTRS;
        }
        _attrs.clear();
        _attrs.addAttribute("", "topicref", "", "CDATA", Integer.toString(_indexOf(topic)));
        return _attrs;
    }

    /**
     * Returns attributes which contain the reifier (if any) and the number
     * of the provided Topic Maps construct (not a topic).
     *
     * @param reifiable The Topic Maps construct.
     * @param pos The position of the reifiable within the parent container.
     * @return Attributes which contain a reference to the reifier (if any) and
     *          the number of the provided Topic Maps construct.
     */
    private Attributes _attributes(final Reifiable reifiable, int pos) {
        _attrs.clear();
        _addReifier(_attrs, reifiable);
        _attrs.addAttribute("", "number", "", "CDATA", Integer.toString(pos));
        return _attrs;
    }

    /**
     * Adds a reference to the reifier of the Topic Maps construct to the 
     * provided attributes. If the Topic Maps construct has no reifier, the
     * provided attributes are not modified.
     *
     * @param attrs The attributes.
     * @param reifiable The reifiable Topic Maps construct.
     */
    private void _addReifier(final AttributesImpl attrs, final Reifiable reifiable) {
        Topic reifier = reifiable.getReifier();
        if (reifier != null) {
            attrs.addAttribute("", "reifier", "", "CDATA", Integer.toString(_indexOf(reifier)));
        }
    }

    /**
     * Normalizes the locator according to CXTM 3.19.
     *
     * @param locator The locator to normalize.
     * @return A normalized representation of the locator.
     */
    private String _normalizeLocator(final Locator locator) {
        String normLoc = _locator2Norm.get(locator);
        if (normLoc != null) {
            return normLoc;
        }
        normLoc = locator.getReference();
        if (normLoc.startsWith(_normBase)) {
            normLoc = normLoc.substring(_normBase.length());
        }
        else {
            int i = 0;
            int slashPos = -1;
            final int max = Math.min(_normBase.length(), normLoc.length());
            while(i < max && _normBase.charAt(i) == normLoc.charAt(i)) {
                if (_normBase.charAt(i) == '/') {
                    slashPos = i;
                }
                i++;
            }
            if (slashPos > -1) {
                normLoc = normLoc.substring(slashPos);
            }
        }
        if (normLoc.charAt(0) == '/') {
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
    private static String _normalizeBaseLocator(final String baseLocator) {
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
    private static void _reportInvalid(final String msg) {
        LOG.warning("Invalid CXTM: '" + msg + "'");
    }


    /*
     * Comparators.
     */

    private final class TopicComparator implements Comparator<Topic> {

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
                    res = _locSetComparator.compare(o1.getItemIdentifiers(), o2.getItemIdentifiers());
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
        int compareType(Typed o1, Typed o2) {
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
        int compareScope(Scoped o1, Scoped o2) {
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
        int _compareValueDatatype(DatatypeAware o1, DatatypeAware o2) {
            int res = compareString(o1.getValue(), o2.getValue());
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

        private Comparator<Set<Role>> _roleSetComparator;

        AssociationComparator() {
            _roleSetComparator = new RoleSetComparator();
        }

        public int compare(Association o1, Association o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = _roleSetComparator.compare(o1.getRoles(), o2.getRoles());
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
    private class RoleIgnoreParentComparator extends AbstractComparator<Role> {

        public int compare(Role o1, Role o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = _topicComparator.compare(o1.getPlayer(), o2.getPlayer());
            if (res == 0) {
                res = compareType(o1, o2);
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

        public int compare(Role o1, Role o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = super.compare(o1, o2);
            if (res == 0) {
                res = _assocComparator.compare(o1.getParent(), o2.getParent());
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
            int res = _compareValueDatatype(o1, o2);
            if (res == 0) {
                res = compareType(o1, o2);
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
    private final class NameComparator extends AbstractComparator<Name> {

        public int compare(Name o1, Name o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareString(o1.getValue(), o2.getValue());
            if (res == 0) {
                res = compareType(o1, o2);
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
            int res = _compareValueDatatype(o1, o2);
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
    private final class RoleSetComparator extends AbstractSetComparator<Role> {

        private RoleIgnoreParentComparator _roleCmp; 

        RoleSetComparator() {
            _roleCmp = new RoleIgnoreParentComparator();
        }

        @Override
        int compareContent(Set<Role> o1, Set<Role> o2,
                int size) {
            int res = 0;
            Role[] roles1 = o1.toArray(new Role[size]);
            Role[] roles2 = o2.toArray(new Role[size]);
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
            return _normalizeLocator(o1).compareTo(_normalizeLocator(o2));
        }
        
    }


    /*
     * Helper classes to treat type-instance relationships, modelled as property
     * of a topic, as associations. 
     */

    private final class TypeInstanceTopic implements Topic {

        private final Set<Locator> _sids;

        TypeInstanceTopic(Locator sid) {
            _sids = Collections.singleton(sid);
        }

        public Set<Locator> getSubjectIdentifiers() {
            return _sids;
        }

        public void addItemIdentifier(Locator arg0) { }
        public void addSubjectIdentifier(Locator arg0) {}
        public void addSubjectLocator(Locator arg0) {}
        public void addType(Topic arg0) {}
        public Set<Occurrence> getOccurrences() { return Collections.emptySet(); }
        public Reifiable getReified() { return null; }
        public Set<Role> getRolesPlayed() { return Collections.emptySet(); }
        public Set<Locator> getSubjectLocators() { return Collections.emptySet(); }
        public Set<Name> getNames() { return Collections.emptySet(); }
        public Set<Topic> getTypes() { return null; }
        public void mergeIn(Topic arg0) { }
        public void remove() throws TopicInUseException { }
        public void removeSubjectIdentifier(Locator arg0) { }
        public void removeSubjectLocator(Locator arg0) { }
        public void removeType(Topic arg0) { }
        public String getId() { return null; }
        public Set<Locator> getItemIdentifiers() { return Collections.emptySet(); }
        public TopicMap getTopicMap() { return null; }
        public void removeItemIdentifier(Locator arg0) { }
        public Name createName(String value, Collection<Topic> scope) { return null; }
        public Name createName(String value, Topic... scope) {return null;}
        public Name createName(Topic type, String value, Collection<Topic> scope) { return null; }
        public Name createName(Topic type, String value, Topic... scope) { return null; }
        public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> scope) { return null;}
        public Occurrence createOccurrence(Topic type, Locator value, Topic... scope) {return null;}
        public Occurrence createOccurrence(Topic type, String value, Collection<Topic> scope) { return null; }
        public Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> scope) { return null; }
        public Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... scope) { return null; }
        public Occurrence createOccurrence(Topic type, String value, Topic... scope) { return null; }
        public Set<Name> getNames(Topic type) { return null; }
        public Set<Occurrence> getOccurrences(Topic type) { return null;}
        public TopicMap getParent() { return null; }
        public Set<Role> getRolesPlayed(Topic type, Topic assocType) { return null; }
        public Set<Role> getRolesPlayed(Topic type) { return null; }
        
    }

    /**
     * Used to represent type-instance relationships which are modelled as
     * [type] property of topics.
     */
    private final class TypeInstanceAssociation implements Association {

        final Set<Role> _roles; 

        TypeInstanceAssociation(Topic type, Topic instance) {
            Role typeRole = new TypeInstanceRole(this, _type, type);
            Role instanceRole = new TypeInstanceRole(this, _instance, instance);
            _roles = new TypeInstanceRoleSet(typeRole, instanceRole);
        }

        public Set<Role> getRoles() {
            return _roles;
        }

        public Topic getType() {
            return _typeInstance;
        }

        public Set<Topic> getRoleTypes() { return null; }
        public Set<Role> getRoles(Topic type) { return null; }
        public void setReifier(Topic reifier) { }
        public void addItemIdentifier(Locator itemIdentifier) { }
        public Set<Locator> getItemIdentifiers() { return Collections.emptySet(); }
        public TopicMap getParent() { return null; }
        public void removeItemIdentifier(Locator itemIdentifier) { }
        public Role createRole(Topic arg0, Topic arg1) { return null; }
        public Topic getReifier() { return null; }
        public void remove() {}
        public void setType(Topic arg0) {}
        public void addTheme(Topic arg0) {}
        public Set<Topic> getScope() { return Collections.emptySet(); }
        public void removeTheme(Topic arg0) {}
        public String getId() { return null; }
        public TopicMap getTopicMap() { return null; }
    }

    /**
     * Immutable association role.
     */
    private class TypeInstanceRole implements Role {
        private final Topic _type;
        private final Topic _player;
        private final Association _parent;

        TypeInstanceRole(Association parent, Topic type, Topic player) {
            _type = type;
            _player = player;
            _parent = parent;
            List<Role> roles = _topic2Roles.get(player);
            if (roles == null) {
                roles = CollectionFactory.createList();
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
        public Association getParent() { return _parent; }
        public void removeItemIdentifier(Locator itemIdentifier) { }
        public Association getAssociation() { return _parent; }
        public Topic getReifier() { return null; }
        public void remove() {}
        public void setPlayer(Topic arg0) {}
        public void setType(Topic arg0) {}
        public String getId() { return null; }
        public TopicMap getTopicMap() { return null; }
    }

    /**
     * Immutable 'set' of two roles.
     */
    private static class TypeInstanceRoleSet extends AbstractSet<Role> {

        private final Role _role1;
        private final Role _role2;

        TypeInstanceRoleSet(Role role1, Role role2) {
            _role1 = role1;
            _role2 = role2;
        }

        @Override
        public Iterator<Role> iterator() {
            return new TypeInstanceRoleSetIterator();
        }

        @Override
        public int size() {
            return 2;
        }

        private class TypeInstanceRoleSetIterator implements Iterator<Role> {

            private int _idx;

            public boolean hasNext() {
                return _idx < 2;
            }

            public Role next() {
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
