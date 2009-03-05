/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.ILiteralAware;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.IScoped;
import org.tinytim.internal.api.IVariant;
import org.tinytim.voc.Namespace;
import org.tinytim.voc.TMDM;
import org.tinytim.voc.XSD;

import org.tmapi.core.Association;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;
import org.tmapi.index.TypeInstanceIndex;

/**
 * {@link TopicMapWriter} implementation that is able to serialize topic maps
 * into a 
 * <a href="http://www.isotopicmaps.org/ctm/">Compact Topic Maps (CTM) 1.0</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class CTMTopicMapWriter implements TopicMapWriter {

    private static final Logger LOG = Logger.getLogger(CTMTopicMapWriter.class.getName());

    private static final Pattern _ID_PATTERN = Pattern.compile("[A-Za-z_](\\.*[\\-A-Za-z_0-9])*");
    private final Writer _out;
//    private final String _baseIRI;
    private final String _encoding;
    private Topic _defaultNameType;
    //TODO: Add setters/getters
    private boolean _exportIIDs = false;
    private boolean _prettify = true;
    private String _title;
    private String _author;
    private String _license;
    private String _comment;
    private final Comparator<Topic> _topicComparator;
    private final Comparator<Association> _assocComparator;
    private final Comparator<Occurrence> _occComparator;
    private final Comparator<Name> _nameComparator;
    private final Comparator<Set<Topic>> _scopeComparator;
    private final Comparator<Locator> _locComparator;
    private final Comparator<Set<Locator>> _locSetComparator;
    private final Comparator<Role> _roleComparator;
    private final Comparator<Variant> _variantComparator;
    private final Map<Topic, TopicReference> _topic2Reference;


    public CTMTopicMapWriter(final OutputStream out) throws IOException {
        this(out, "utf-8");
    }

    public CTMTopicMapWriter(final OutputStream out, final String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding), encoding);
    }

    private CTMTopicMapWriter(final Writer writer, final String encoding) {
        _out = writer;
//        if (baseIRI == null) {
//            throw new IllegalArgumentException("The base IRI must not be null");
//        }
//        _baseIRI = baseIRI;
        if (encoding == null) {
            throw new IllegalArgumentException("The encoding must not be null");
        }
        _encoding = encoding;
        _topic2Reference = new HashMap<Topic, TopicReference>(200);
        _scopeComparator = new ScopeComparator();
        _locSetComparator = new LocatorSetComparator();
        _locComparator = new LocatorComparator();
        _topicComparator = new TopicComparator();
        _assocComparator = new AssociationComparator();
        _occComparator = new OccurrenceComparator();
        _nameComparator = new NameComparator();
        _roleComparator = new RoleComparator();
        _variantComparator = new VariantComparator();
    }

    /**
     * Sets the title of the topic map which appears at the top of the file.
     *
     * @param title The title of the topic map.
     */
    public void setTitle(String title) {
        _title = title;
    }

    /**
     * Returns the title of the topic map.
     *
     * @return The title or <tt>null</tt> if no title was set.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the author which appears at the top of the file.
     *
     * @param author The author.
     */
    public void setAuthor(String author) {
        _author = author;
    }

    /**
     * Returns the author.
     *
     * @return The author or <tt>null</tt> if no author was set.
     */
    public String getAuthor() {
        return _author;
    }

    /**
     * Sets the license which should appear on top of the file.
     * <p>
     * The license of the topic map. This could be a name or an IRI or both, i.e.
     * "Creative Commons-License <http://creativecommons.org/licenses/by-nc-sa/3.0/>".
     * </p>
     *
     * @param license The license.
     */
    public void setLicense(String license) {
        _license = license;
    }

    /**
     * Returns the license.
     *
     * @return The license or <tt>null</tt> if no license was set.
     */
    public String getLicense() {
        return _license;
    }

    /**
     * Sets a file comment.
     * <p>
     * The comment could describe the topic map, or provide an additional 
     * copyright notice, or SVN/CVS keywords etc.
     * </p>
     *
     * @param comment The comment.
     */
    public void setComment(String comment) {
        _comment = comment;
    }

    /**
     * Returns the comment.
     *
     * @return The comment or <tt>null</tt> if no comment was set.
     */
    public String getComment() {
        return _comment;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    @Override
    public void write(TopicMap topicMap) throws IOException {
        _defaultNameType = topicMap.getTopicBySubjectIdentifier(TMDM.TOPIC_NAME);
        _out.write("%encoding \"" + _encoding + "\"");
        _newline();
        _out.write("%version 1.0");
        _writeFileHeader();
        _out.write(")#");
        _newline();
        _newline();
        _writeSection("Prefixes");
        _out.write("%prefix xsd <" + Namespace.XSD + ">");
        _newline();
        Collection<Topic> topics = new ArrayList<Topic>(topicMap.getTopics());
        final boolean removeDefaultNameType = _defaultNameType != null
                && _defaultNameType.getSubjectIdentifiers().size() == 1
                && _defaultNameType.getSubjectLocators().size() == 0
                && _defaultNameType.getTypes().size() == 0
                && _defaultNameType.getNames().size() == 0
                && _defaultNameType.getOccurrences().size() == 0
                && _defaultNameType.getRolesPlayed().size() == 0
                && _defaultNameType.getReified() == null;
        if (removeDefaultNameType) {
            topics.remove(_defaultNameType);
        }
        if (topicMap.getReifier() != null) {
            // Special handling of the tm reifier to avoid an additional 
            // whitespace character in front of the ~
            Topic reifier = topicMap.getReifier();
            _writeSection("Topic Map");
            _out.write("~ ");
            _writeTopicRef(reifier);
            _newline();
            _writeTopic(reifier);
            topics.remove(reifier);
        }
        TypeInstanceIndex tiIdx = ((IIndexManagerAware) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _writeSection("ONTOLOGY");
        _writeOntologyTypes(tiIdx.getTopicTypes(), topics, "Topic Types");
        _writeOntologyTypes(tiIdx.getAssociationTypes(), topics, "Association Types");
        _writeOntologyTypes(tiIdx.getRoleTypes(), topics, "Role Types");
        _writeOntologyTypes(tiIdx.getOccurrenceTypes(), topics, "Occurrence Types");
        Collection<Topic> nameTypes = new ArrayList<Topic>(tiIdx.getNameTypes());
        if (removeDefaultNameType) {
            nameTypes.remove(_defaultNameType);
        }
        _writeOntologyTypes(nameTypes, topics, "Name Types");
        tiIdx.close();
        _newline();
        _writeSection("INSTANCES");
        _writeSection("Topics");
        _writeTopics(topics);
        Collection<Association> assocs = new ArrayList<Association>(topicMap.getAssociations());
        if (!assocs.isEmpty()) {
            Association[] assocArray = assocs.toArray(new Association[assocs.size()]);
            _writeSection("Associations");
            Arrays.sort(assocArray, _assocComparator);
            for (Association assoc: assocArray) {
                _writeAssociation(assoc);
            }
        }
        _newline();
        _out.write("# Thanks for using tinyTiM -- http://tinytim.sourceforge.net/ :)");
        _newline();
        _out.flush();
    }

    private void _writeFileHeader() throws IOException {
        _newline();
        _newline();
        _out.write("#(");
        _newline();
        if (_title != null) {
            _out.write("Title:   " + _title);
            _newline();
        }
        if (_author != null) {
            _out.write("Author:  " + _author);
            _newline();
        }
        if (_license != null) {
            _out.write("License: " + _license);
            _newline();
        }
        if (_comment != null) {
            _newline();
            _out.write(_comment);
            _newline();
        }
        _newline();
        _out.write("Generated by tinyTiM -- http://tinytim.sourceforge.net/");
        _newline();
        _newline();
    }

    /**
     * If <tt>topics</tt> is not empty, the topics will be removed from 
     * <tt>allTopics</tt> and written out under the specified section <tt>title</tt>. 
     *
     * @param topics
     * @param allTopics
     * @param title
     * @throws IOException
     */
    private void _writeOntologyTypes(Collection<Topic> topics, Collection<Topic> allTopics, String title) throws IOException {
        if (topics.isEmpty()) {
            return;
        }
        allTopics.removeAll(topics);
        _writeSection(title);
        _writeTopics(topics);
    }

    /**
     * Sorts the specified collection and serializes the topics.
     *
     * @param topics An unordered collection of topics.
     * @throws IOException If an error occurs.
     */
    private void _writeTopics(Collection<Topic> topics) throws IOException {
        Topic[] topicArray = topics.toArray(new Topic[topics.size()]);
        Arrays.sort(topicArray, _topicComparator);
        for (Topic topic: topicArray) {
            _writeTopic(topic);
        }
    }

    private void _writeTopic(Topic topic) throws IOException {
        final TopicReference mainIdentity = _getTopicReference(topic);
//        if ((ref.type == TopicReference.ID
//                || ref.type == TopicReference.IID)
//                && _hasNoCharacteristics(topic)) {
//            return;
//        }
        _newline();
        boolean wantSemicolon = false;
        _writeTopicRef(mainIdentity);
        _out.write(' ');
        for (Topic type: topic.getTypes()) {
            _writeTypeInstance(type, wantSemicolon);
            wantSemicolon = true;
        }
        for (Name name: _getNames(topic)) {
            _writeName((IName) name, wantSemicolon);
            wantSemicolon = true;
        }
        for (Occurrence occ: _getOccurrences(topic)) {
            _writeOccurrence((IOccurrence) occ, wantSemicolon);
            wantSemicolon = true;
        }
        for (TopicReference sid: _getSubjectIdentifiers(topic)) {
            _writeTopicRef(sid, wantSemicolon);
            wantSemicolon = true;
        }
        for (TopicReference slo: _getSubjectLocators(topic)) {
            _writeTopicRef(slo, wantSemicolon);
            wantSemicolon = true;
        }
        if (_exportIIDs) {
            TopicReference[] iids = _getItemIdentifiers(topic);
            if ((mainIdentity.type == TopicReference.ID
                    || mainIdentity.type == TopicReference.IID) 
                    && iids.length == 1) {
                //TODO
            }
            else {
                for (TopicReference iid: iids) {
                    _writeTopicRef(iid, wantSemicolon);
                    wantSemicolon = true;
                }
            }
        }
        if (wantSemicolon) {
            _out.write(' ');
        }
        _out.write(".");
        _newline();
    }

//    private boolean _hasNoCharacteristics(Topic topic) {
//        return topic.getTypes().isEmpty()
//                && topic.getNames().isEmpty()
//                && topic.getOccurrences().isEmpty()
//                && !(_exportIIDs || topic.getItemIdentifiers().isEmpty());
//    }

    private TopicReference[] _getSubjectIdentifiers(Topic topic) {
        return _getLocators(topic, TopicReference.SID, topic.getSubjectIdentifiers());
    }

    private TopicReference[] _getSubjectLocators(Topic topic) {
        return _getLocators(topic, TopicReference.SLO, topic.getSubjectLocators());
    }

    private TopicReference[] _getItemIdentifiers(Topic topic) {
        return _getLocators(topic, TopicReference.IID, topic.getItemIdentifiers());
    }

    private TopicReference[] _getLocators(Topic topic, int kind, Set<Locator> locs) {
        if (locs.isEmpty()) {
            return new TopicReference[0];
        }
        Collection<TopicReference> refs = new ArrayList<TopicReference>(locs.size());
        if (kind == TopicReference.SID) {
            for (Locator loc: locs) {
                refs.add(TopicReference.createSubjectIdentifier(loc.toExternalForm()));
            }
        }
        else if (kind == TopicReference.IID) {
            for (Locator loc: locs) {
                refs.add(TopicReference.createItemIdentifier(loc.toExternalForm()));
            }
        }
        else if (kind == TopicReference.SLO) {
            for (Locator loc: locs) {
                refs.add(TopicReference.createSubjectLocator(loc.toExternalForm()));
            }
        }
        refs.remove(_getTopicReference(topic));
        TopicReference[] refArray = refs.toArray(new TopicReference[refs.size()]);
        //Arrays.sort(locArray, _locComparator);
        return refArray;
    }

    private Name[] _getNames(Topic topic) {
        Set<Name> names_ = topic.getNames();
        Name[] names = names_.toArray(new Name[names_.size()]);
        Arrays.sort(names, _nameComparator);
        return names;
    }

    /**
     * 
     *
     * @param topic
     * @return
     */
    private Occurrence[] _getOccurrences(Topic topic) {
        Set<Occurrence> occs_ = topic.getOccurrences();
        Occurrence[] occs = occs_.toArray(new Occurrence[occs_.size()]);
        Arrays.sort(occs, _occComparator);
        return occs;
    }

    private void _writeTypeInstance(Topic type, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("isa ");
        _writeTopicRef(type);
    }

//    private void _writeSupertypeSubtype(Topic supertype, boolean wantSemicolon) throws IOException {
//        _writeSemicolon(wantSemicolon);
//        _out.write("ako ");
//        _writeTopicRef(supertype);
//    }

    private void _writeOccurrence(IOccurrence occ, boolean wantSemicolon)  throws IOException {
        _writeSemicolon(wantSemicolon);
        _writeTopicRef(occ.getType());
        _out.write(": ");
        _writeLiteral(occ.getLiteral());
        _writeScope(occ);
        _writeReifier(occ);
    }

    private void _writeName(IName name, boolean wantSemicolon)  throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("- ");
        Topic type = name.getType();
        if (!type.equals(_defaultNameType)) {
            _writeTopicRef(type);
            _out.write(": ");
        }
        _writeString(name.getValue());
        _writeScope(name);
        _writeReifier(name);
        Variant[] variants = name.getVariants().toArray(new Variant[0]);
        Arrays.sort(variants, _variantComparator);
        for (Variant variant: variants) {
            _writeVariant((IVariant) variant);
        }
    }

    private void _writeVariant(IVariant variant) throws IOException {
        _out.write(" (");
        _writeLiteral(variant.getLiteral());
        _writeScope(variant);
        _out.write(')');
    }

    private void _writeAssociation(Association assoc) throws IOException {
        _newline();
        _writeTopicRef(assoc.getType());
        _out.write('(');
        Role[] roles = assoc.getRoles().toArray(new Role[0]);
        Arrays.sort(roles, _roleComparator);
        for (Role role: roles) {
            _writeTopicRef(role.getType());
            _out.write(": ");
            _writeTopicRef(role.getPlayer());
            if (role.getReifier() != null) {
                _out.write(" #( If you found a reason why a role should be reified, write us )# ");
                _writeReifier(role);
            }
        }
        _out.write(')');
        _writeScope((IScoped) assoc);
        _writeReifier(assoc);
        _newline();
    }

    private void _writeSemicolon(boolean wantSemicolon) throws IOException {
        if (wantSemicolon) {
            _out.write(';');
            _newline();
            if (_prettify) {
                _out.write("    ");
            }
        }
    }

    /**
     * 
     *
     * @param scoped
     * @throws IOException
     */
    private void _writeScope(IScoped scoped) throws IOException {
        IScope scope = scoped.getScopeObject(); 
        if (!scope.isUnconstrained()) {
            Topic[] themes = scope.asSet().toArray(new Topic[scope.size()]);
            Arrays.sort(themes, _topicComparator);
            _out.write(" @");
            boolean wantComma = false;
            for (Topic theme: themes) {
                if (wantComma) {
                    _out.write(", ");
                }
                _writeTopicRef(theme);
                wantComma = true;
            }
        }
    }

    /**
     * 
     *
     * @param topic
     * @throws IOException
     */
    private void _writeTopicRef(Topic topic) throws IOException {
        _writeTopicRef(_getTopicReference(topic));
    }

    private void _writeTopicRef(TopicReference topicRef) throws IOException {
        _writeTopicRef(topicRef, false);
    }

    private void _writeTopicRef(TopicReference topicRef, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        switch (topicRef.type) {
            case TopicReference.ID:
                _out.write(topicRef.reference);
                return;
            case TopicReference.IID:
                _out.write('^');
                break;
            case TopicReference.SLO:
                _out.write("= ");
                break;
            case TopicReference.SID:
                break;
            default:
                throw new RuntimeException("Internal error: Cannot match topic reference type " + topicRef.type);
        }
        _writeLocator(topicRef.reference);
    }

    private void _writeString(String string) throws IOException {
        //TODO: Escape
        _out.write('"');
        _out.write(string);
        _out.write('"');
    }

    private void _writeLocator(String reference) throws IOException {
        _out.write("<" + reference + ">");
    }

    private TopicReference _getTopicReference(Topic topic) {
        TopicReference ref = _topic2Reference.get(topic);
        if (ref == null) {
            final boolean hasIIds = !(_exportIIDs || topic.getItemIdentifiers().isEmpty());
            final boolean hasSids = !topic.getSubjectIdentifiers().isEmpty();
            final boolean hasSlos = !topic.getSubjectLocators().isEmpty();
            if (!hasIIds) {
                if (hasSids) {
                    ref = hasSlos ? null : TopicReference.createSubjectIdentifier(topic.getSubjectIdentifiers().iterator().next().toExternalForm());
                }
                else if (hasSlos) {
                    ref = TopicReference.createSubjectLocator(topic.getSubjectLocators().iterator().next().toExternalForm());
                }
            }
            if (ref == null) {
                if (topic.getItemIdentifiers().size() == 1) {
                    final String iid = topic.getItemIdentifiers().iterator().next().toExternalForm();
                    int idx = iid.lastIndexOf('#');
                    if (idx > 0) {
                        String id = iid.substring(idx + 1);
                        ref = _isValidId(id) ? TopicReference.createId(id)
                                             : TopicReference.createItemIdentifier("#" + id);
                    }
                    else {
                        ref = TopicReference.createItemIdentifier(iid);
                    }
                }
            }
            if (ref == null) {
                String iri = null;
                for (Locator iid: topic.getItemIdentifiers()) {
                    String addr = iid.getReference();
                    int idx = addr.lastIndexOf('#');
                    if (idx < 0) {
                        continue;
                    }
                    else {
                        String id = addr.substring(idx+1);
                        iri = _isValidId(id) ? id : null; 
                    }
                }
                if (iri == null) {
                    iri = "id-" + topic.getId();
                }
                ref = TopicReference.createId(iri);
            }
            _topic2Reference.put(topic, ref);
        }
        return ref;
    }

    private boolean _isValidId(String id) {
        return _ID_PATTERN.matcher(id).matches();
    }

    /**
     * Writes a literal.
     * 
     * If the datatype is xsd:anyURI or xsd:string, the datatype is omitted.
     * If the datatype is natively supported by CTM (like xsd:integer, xsd:decimal)
     * the quotes and the datatype are omitted. 
     *
     * @param lit The literal to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeLiteral(ILiteral lit) throws IOException {
        final Locator datatype = lit.getDatatype();
        final String value = lit.getValue();
        if (XSD.ANY_URI.equals(datatype)) {
            _writeLocator(value);
        }
        else if (!XSD.STRING.equals(datatype) 
                    && _isNativelySupported(lit)) {
            _out.write(value);
        }
        else {
            _writeString(value);
            if (!XSD.STRING.equals(datatype)) {
                _out.write("^^");
                String datatypeIRI = datatype.toExternalForm(); 
                if (datatypeIRI.startsWith(Namespace.XSD)) {
                    _out.write("xsd:");
                    _out.write(datatypeIRI.substring(datatypeIRI.lastIndexOf('#')+1));
                }
                else {
                    _writeLocator(datatypeIRI);
                }
            }
        }
    }

    private boolean _isNativelySupported(ILiteral literal) {
        Locator datatype = literal.getDatatype();
        return XSD.STRING.equals(datatype)
                || XSD.ANY_URI.equals(datatype)
                || XSD.DECIMAL.equals(datatype)
                || XSD.INTEGER.equals(datatype)
                || XSD.DATE.equals(datatype)
                || XSD.DATE_TIME.equals(datatype)
                || (XSD.DOUBLE.equals(datatype) 
                        && ("INF".equals(literal.getValue())) 
                            || "-INF".equals(literal.getValue()));
    }

    /**
     * Writes the reifier if <tt>reifiable</tt> is reified.
     *
     * @param reifiable The reifiable construct.
     * @throws IOException If an error occurs.
     */
    private void _writeReifier(Reifiable reifiable) throws IOException {
        Topic reifier = reifiable.getReifier();
        if (reifier == null) {
            return;
        }
        _out.write(" ~ ");
        _writeTopicRef(reifier);
    }

    private void _newline() throws IOException {
        _out.write('\n');
    }

    private void _writeSection(String name) throws IOException {
        _newline();
        _newline();
        _out.write("#-- " + name);
        _newline();
    }

    /**
     * Writes a warning msg to the log.
     * 
     * This method is used to inform the user that the serialized topic map
     * is not valid.
     *
     * @param msg The warning message.
     */
    private static void _reportInvalid(final String msg) {
        LOG.warning("Invalid CTM: '" + msg + "'");
    }



    private static final class TopicReference {
        static final int 
            ID = 0,
            SID = 1,
            SLO = 2,
            IID = 3;
        final int type;
        final String reference;

        private TopicReference(int type, String reference) {
            this.type = type;
            this.reference = reference;
        }

        public static TopicReference createId(String reference) {
            return new TopicReference(ID, reference);
        }
        
        public static TopicReference createSubjectIdentifier(String reference) {
            return new TopicReference(SID, reference);
        }

        public static TopicReference createSubjectLocator(String reference) {
            return new TopicReference(SLO, reference);
        }

        public static TopicReference createItemIdentifier(String reference) {
            return new TopicReference(IID, reference);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TopicReference)) {
                return false;
            }
            TopicReference other = (TopicReference) obj;
            return (type == other.type && reference.equals(other.reference)); 
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return type + reference.hashCode();
        }

    }


    /*
     * Comparators.
     */

    /**
     * Topic comparator.
     * - Topics with less types are considered less than others with types.
     * - Topics with less subject identifiers are considered less than others with sids.
     * - Topics with less subject locators are considered less than other with slos
     * - Topics with less item identifiers are less than others with iids
     * 
     */
    private final class TopicComparator implements Comparator<Topic> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Topic o1, Topic o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = o1.getTypes().size() - o2.getTypes().size();
            if (res == 0) {
                res = _locSetComparator.compare(o1.getSubjectIdentifiers(), o2.getSubjectIdentifiers());
                if (res == 0) {
                    res = _locSetComparator.compare(o1.getSubjectLocators(), o2.getSubjectLocators());
                    if (res == 0) {
                        res = _locSetComparator.compare(o1.getItemIdentifiers(), o2.getItemIdentifiers());
                    }
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
        /**
         * Extracts the reifier of the reifiable Topic Maps constructs and 
         * compares them.
         *
         * @param o1 The first reifiable Topic Maps construct.
         * @param o2 The second reifiable Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int compareReifier(Reifiable o1, Reifiable o2) {
            Topic reifier1 = o1.getReifier();
            Topic reifier2 = o2.getReifier();
            int res = 0;
            if (reifier1 == null) {
                res = reifier2 == null ? 0 : -1;
            }
            else if (reifier2 == null) {
                res = 1;
            }
            return res != 0 ? res : _topicComparator.compare(reifier1, reifier2);
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
        int compareValueDatatype(DatatypeAware o1, DatatypeAware o2) {
            ILiteral lit1 = ((ILiteralAware) o1).getLiteral();
            ILiteral lit2 = ((ILiteralAware) o2).getLiteral();
            int res = 0;
            if (_isNativelySupported(lit1)) {
                res = _isNativelySupported(lit2) ? 0 : -1;
            }
            else if (_isNativelySupported(lit2)) {
                res = 1;
            }
            if (res == 0) {
                res = compareString(lit1.getDatatype().getReference(), lit2.getDatatype().getReference());
                if (res == 0) {
                    res = compareString(lit1.getValue(), lit2.getValue());
                }
            }
            return res;
        }
    }

    /**
     * Association comparator.
     * 
     */
    private final class AssociationComparator extends AbstractComparator<Association> {

        private Comparator<Set<Role>> _roleSetComparator;

        AssociationComparator() {
            //_roleSetComparator = new RoleSetComparator();
        }

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Association o1, Association o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                //res = _roleSetComparator.compare(o1.getRoles(), o2.getRoles());
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
    private class RoleComparator extends AbstractComparator<Role> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Role o1, Role o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = _topicComparator.compare(o1.getPlayer(), o2.getPlayer());
            }
            return res;
        }
    }
    
    /**
     * Occurrence comparator.
     * - Occs in the UCS are less than ones with a special scope.
     * - Occs which are not reified are less than ones which are reified.
     */
    private final class OccurrenceComparator extends AbstractDatatypeAwareComparator<Occurrence> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Occurrence o1, Occurrence o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = compareScope(o1, o2);
                if (res == 0) {
                    res = compareReifier(o1, o2);
                    if (res == 0) {
                        res = compareValueDatatype(o1, o2);
                    }
                }
            }
            return res;
        }
        
    }

    /**
     * Name comparator.
     * - Names with the default name type are less than names with a non-standard type.
     * - Names in the UCS are less than ones with a special scope.
     * - Names with no variants are less than ones with variants
     * - Names which are not reified are less than ones which are reified.
     */
    private final class NameComparator extends AbstractComparator<Name> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Name o1, Name o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = compareScope(o1, o2);
                if (res == 0) {
                    res = o1.getVariants().size() - o2.getVariants().size();
                    if (res == 0) {
                        res = compareReifier(o1, o2);
                        if (res == 0) {
                            res = compareString(o1.getValue(), o2.getValue());
                        }
                    }
                }
            }
            return res;
        }

        /* (non-Javadoc)
         * @see org.tinytim.mio.CTMTopicMapWriter.AbstractComparator#compareType(org.tmapi.core.Typed, org.tmapi.core.Typed)
         */
        @Override
        int compareType(Typed o1, Typed o2) {
            Topic type1 = o1.getType();
            Topic type2 = o2.getType();
            int res = 0;
            if (type1.equals(_defaultNameType)) {
                res = type2.equals(type1) ? 0 : -1;
            }
            else if (type2.equals(_defaultNameType)) {
                res = 1;
            }
            return res != 0 ? res : super.compareType(o1, o2);
        }

    }

    /**
     * Variant comparator.
     * - Variants with a lesser scope size are less.
     * - Variants which are not reified are less than ones which are reified.
     */
    private final class VariantComparator extends AbstractDatatypeAwareComparator<Variant> {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Variant o1, Variant o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareScope(o1, o2);
            if (res == 0) {
                res = compareReifier(o1, o2);
                if (res == 0) {
                    res = compareValueDatatype(o1, o2);
                }
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

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
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
     * Compares {@link org.tmapi.core.Locator}s.
     */
    private final class LocatorComparator implements Comparator<Locator> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Locator o1, Locator o2) {
            if (o1 == o2) {
                return 0;
            }
            return o1.getReference().compareTo(o2.getReference());
        }
        
    }

    /**
     * Comparator for sets of {@link org.tmapi.core.Locator}s. 
     */
    private final class LocatorSetComparator extends AbstractSetComparator<Locator> {

        /* (non-Javadoc)
         * @see org.tinytim.mio.CTMTopicMapWriter.AbstractSetComparator#compareContent(java.util.Set, java.util.Set, int)
         */
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

}
