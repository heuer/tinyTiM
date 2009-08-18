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
import java.util.HashSet;
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
import org.tinytim.mio.internal.ctm.ITMCLPreprocessor;
import org.tinytim.mio.internal.ctm.ITemplate;
import org.tinytim.mio.internal.ctm.impl.DefaultTMCLPreprocessor;
import org.tinytim.voc.Namespace;
import org.tinytim.voc.TMCL;
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
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * {@link TopicMapWriter} implementation that is able to serialize topic maps
 * into a 
 * <a href="http://www.isotopicmaps.org/ctm/">Compact Topic Maps (CTM) 1.0</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hannes Niederhausen
 * @version $Rev$ - $Date$
 */
public class CTMTopicMapWriter implements TopicMapWriter {

    private static final Logger LOG = Logger.getLogger(CTMTopicMapWriter.class.getName());

    private static final String _ID_START = "[a-zA-Z_]" +
                            "|[\\u00C0-\\u00D6]" +
                            "|[\\u00D8-\\u00F6]" + 
                            "|[\\u00F8-\\u02FF]" +
                            "|[\\u0370-\\u037D]" + 
                            "|[\\u037F-\\u1FFF]" +
                            "|[\\u200C-\\u200D]" + 
                            "|[\\u2070-\\u218F]" +
                            "|[\\u2C00-\\u2FEF]" + 
                            "|[\\u3001-\\uD7FF]" +
                            "|[\\uF900-\\uFDCF]" + 
                            "|[\\uFDF0-\\uFFFD]" + 
                            "|[\\u10000-\\uEFFFF]";
    private static final String _ID_PART = _ID_START + 
                                   "|[\\-\\.0-9]" + 
                                   "|\\u00B7" + 
                                   "|[\\u0300-\\u036F]" + 
                                   "|[\\u203F-\\u2040]";
    private static final String _ID_END = _ID_START + 
                                   "|[\\-0-9]" + 
                                   "|\\u00B7" +  
                                   "|[\\u0300-\\u036F]" + 
                                   "|[\\u203F-\\u2040]";
    private static final Pattern _ID_PATTERN = Pattern.compile(String.format("%s(%s*%s)*", _ID_START, _ID_PART, _ID_END));
    private static final Pattern _LOCAL_PATTERN = Pattern.compile("([0-9]*\\.*[\\-A-Za-z_0-9])*");
    private static final Pattern _IRI_PATTERN = Pattern.compile("[^<>\"\\{\\}\\`\\\\ ]+");
    private static final char[] _TRIPLE_QUOTES = new char[] { '"', '"', '"' };
    private static final Reference[] _EMPTY_REFERENCE_ARRAY = new Reference[0];
    private static final ITemplate[] _EMPTY_TEMPLATE_ARRAY = new ITemplate[0];
    private static final Topic[] _EMPTY_TOPIC_ARRAY = new Topic[0];
    private static final Reference _UNTYPED_REFERENCE = Reference.createId("[untyped]");
    private static final String _TMCL_TEMPLATE = "http://www.topicmaps.org/tmcl/templates.ctm";
    
    private final Writer _out;
    private final String _baseIRI;
    private final String _encoding;
    private Topic _defaultNameType;
    private boolean _exportIIDs;
    private boolean _keepAbsoluteIIDs;
    private String _title;
    private String _author;
    private String _license;
    private String _comment;
    private char[] _indent;
    private final Comparator<Topic> _topicComparator;
    private final Comparator<Topic> _topicIdComparator;
    private final Comparator<Association> _assocComparator;
    private final Comparator<Occurrence> _occComparator;
    private final Comparator<Name> _nameComparator;
    private final Comparator<Set<Topic>> _scopeComparator;
    private final Comparator<Role> _roleComparator;
    private final Comparator<Variant> _variantComparator;
    private final Map<Topic, Reference> _topic2Reference; //TODO: LRU?
    private final Map<String, String> _prefixes;
    private final Set<String> _imports;
    private final Map<Topic, Collection<ITemplate>> _topic2Templates;
    private final Map<Topic, Collection<Topic>> _topic2Supertypes;
    private Reference _lastReference;
    private boolean _tmcl;

    private static enum TypeFilter {
        TOPIC,
        ASSOCIATION,
        ROLE,
        OCCURRENCE,
        NAME,
        SUBJECT
    }

    /**
     * Constructs a new instance using "utf-8" encoding.
     * <p>
     * The base IRI is used to abbreviate IRIs. IRIs with a fragment identifier
     * (like <tt>#my-topic</tt>) are written in an abbreviated from iff they
     * start with the provided base IRI.
     * </p>
     *
     * @param out The stream to write onto.
     * @param baseIRI The base IRI to resolve locators against.
     * @throws IOException In case of an error.
     */
    public CTMTopicMapWriter(final OutputStream out, final String baseIRI) throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Constructs a new instance with the specified encoding.
     * <p>
     * The base IRI is used to abbreviate IRIs. IRIs with a fragment identifier
     * (like <tt>#my-topic</tt>) are written in an abbreviated from iff they
     * start with the provided base IRI.
     * </p>
     *
     * @param out The stream to write onto.
     * @param baseIRI The base IRI to resolve locators against.
     * @param encoding The encoding to use.
     * @throws IOException In case of an error, i.e. if the encoding is unsupported.
     */
    public CTMTopicMapWriter(final OutputStream out, final String baseIRI, final String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding), baseIRI, encoding);
    }

    /**
     * Constructs a new instance.
     *
     * @param writer The writer to use.
     * @param baseIRI The base IRI to resolve locators against.
     * @param encoding The encoding to use.
     */
    private CTMTopicMapWriter(final Writer writer, final String baseIRI, final String encoding) {
        _out = writer;
        if (baseIRI == null) {
            throw new IllegalArgumentException("The base IRI must not be null");
        }
        _baseIRI = baseIRI;
        if (encoding == null) {
            throw new IllegalArgumentException("The encoding must not be null");
        }
        _encoding = encoding;
        _topic2Reference = new HashMap<Topic, Reference>(200);
        _topicComparator = new TopicComparator();
        _scopeComparator = new ScopeComparator();
        _topicIdComparator = new TopicIdComparator();
        _assocComparator = new AssociationComparator();
        _occComparator = new OccurrenceComparator();
        _nameComparator = new NameComparator();
        _roleComparator = new RoleComparator();
        _variantComparator = new VariantComparator();
        _prefixes = new HashMap<String, String>();
        _imports = new HashSet<String>();
        _topic2Templates = new HashMap<Topic, Collection<ITemplate>>();
        _topic2Supertypes = new HashMap<Topic, Collection<Topic>>();
        setIdentation(4);
        setExportItemIdentifiers(false);
        setTMCL(true);
    }

    /**
     * Sets the title of the topic map which appears in the header comment of
     * the file.
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
     * Sets the author which appears in the header comment of the file.
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
     * Sets the license which should appear in the header comment of the file.
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
     * The an additional comment which appears in the header comment of the file.
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

    public void setTMCL(boolean enable) {
        _imports.add(_TMCL_TEMPLATE);
        addPrefix("tmcl", Namespace.TMCL);
        addPrefix("tmdm", Namespace.TMDM_MODEL);
        _tmcl = true;
    }

    /**
     * Adds a prefix to the writer.
     * <p>
     * The writer converts all locators (item identifiers, subject identifiers,
     * subject locators) into QNames which start with the provided 
     * <tt>reference</tt>.
     * </p>
     * <p>
     * I.e. if a prefix "wp" is set to "http://en.wikipedia.org/wiki", a 
     * subject identifier like "http://en.wikipedia.org/wiki/John_Lennon" is 
     * converted into a QName "wp:John_Lennon".
     * </p>
     *
     * @param prefix The prefix to add, an existing prefix with the same name
     *                  will be overridden.
     * @param reference The IRI to which the prefix should be assigned to.
     */
    public void addPrefix(String prefix, String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (!_isValidId(prefix)) {
            throw new IllegalArgumentException("The prefix is an invalid CTM identifier: " + prefix);
        }
        if (reference == null) {
            throw new IllegalArgumentException("The reference must not be null");
        }
        if (!_IRI_PATTERN.matcher(reference).matches()) {
            throw new IllegalArgumentException("The reference is an invalid CTM IRI: " + reference);
        }
        _prefixes.put(prefix, reference);
    }

    /**
     * Removes a prefix mapping.
     *
     * @param prefix The prefix to remove.
     */
    public void removePrefix(String prefix) {
        _prefixes.remove(prefix);
    }

    /**
     * Sets the identation level, by default the identation level is set to 4
     * which means that four whitespace characters are written.
     * <p>
     * If the size is set to <tt>0</tt>, no identation will be done.
     * </p>
     * <p>
     * The identation level indicates how many whitespaces are written in front
     * of a statement within a topic block.
     * </p>
     * <p>Example (identation level = 4):
     * <pre>
     * john isa person;
     *     - "John".
     * </pre>
     * </p>
     * <p>Example (identation level = 0):
     * <pre>
     * paul isa person;
     * - "Paul".
     * </pre>
     * </p>
     *
     * @param level The identation level.
     */
    public void setIdentation(int level) {
        if (_indent == null || _indent.length != level) {
            _indent = new char[level];
            Arrays.fill(_indent, ' ');
        }
    }

    /**
     * Returns the identation level.
     *
     * @return The number of whitespaces which are written in front of a 
     *          statement within a topic block.
     */
    public int getIdentation() {
        return _indent.length;
    }

    /**
     * Indicates if the item identifiers of the topics should be exported.
     * <p>
     * By default, this feature is disabled.
     * </p>
     *
     * @param export <tt>true</tt> to export item identifiers, otherwise <tt>false</tt>.
     */
    public void setExportItemIdentifiers(boolean export) {
        setExportItemIdentifiers(export, export);
    }

    /**
     * Indicates if the item identifiers of a topic are exported.
     *
     * @return <tt>true</tt> if the item identifiers are exported, otherwise <tt>false</tt>.
     */
    public boolean getExportItemIdentifiers() {
        return _exportIIDs;
    }

    // Unsure if this feature should be exposed, keep it private currently
    private void setExportItemIdentifiers(boolean export, boolean keepAbsoluteItemIdentifiers) {
        _exportIIDs = export;
        _keepAbsoluteIIDs = keepAbsoluteItemIdentifiers;
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
        _writeImports();
        _writePrefixes();
        _newline();
        Collection<Topic> topics = new ArrayList<Topic>(topicMap.getTopics());
        Collection<Association> assocs = new ArrayList<Association>(topicMap.getAssociations());
        TypeInstanceIndex tiIdx = ((IIndexManagerAware) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _createSupertypeSubtypeRelationships(tiIdx, topicMap, assocs);
        if (_tmcl) {
            _createTMCLTemplates(topicMap, topics, assocs);
        }
        if (topicMap.getReifier() != null) {
            // Special handling of the tm reifier to avoid an additional 
            // whitespace character in front of the ~
            Topic reifier = topicMap.getReifier();
            _writeSection("Topic Map");
            _out.write("~ ");
            _writeTopicRef(reifier);
            _newline();
            _writeTopic(reifier, false);
            topics.remove(reifier);
        }
        _writeSection("ONTOLOGY");
        Collection<Topic> types = _filter(topicMap, tiIdx.getTopicTypes(), topics, TypeFilter.TOPIC);
        _writeOntologySection(types, topics, "Topic Types");
        types = _filter(topicMap, tiIdx.getAssociationTypes(), topics, TypeFilter.ASSOCIATION);
        _writeOntologySection(types, topics, "Association Types");
        types = _filter(topicMap, tiIdx.getRoleTypes(), topics, TypeFilter.ROLE);
        _writeOntologySection(types, topics, "Role Types");
        types = _filter(topicMap, tiIdx.getOccurrenceTypes(), topics, TypeFilter.OCCURRENCE);
        _writeOntologySection(types, topics, "Occurrence Types");
        types = _filter(topicMap, tiIdx.getNameTypes(), topics, TypeFilter.NAME);
        _writeOntologySection(types, topics, "Name Types");
        tiIdx.close();
        ScopedIndex scopeIdx = ((IIndexManagerAware) topicMap).getIndexManager().getScopedIndex();
        if (!scopeIdx.isAutoUpdated()) {
            scopeIdx.reindex();
        }
        _writeOntologySection(scopeIdx.getAssociationThemes(), topics, "Association Themes");
        _writeOntologySection(scopeIdx.getOccurrenceThemes(), topics, "Occurrence Themes");
        _writeOntologySection(scopeIdx.getNameThemes(), topics, "Name Themes");
        _writeOntologySection(scopeIdx.getVariantThemes(), topics, "Variant Themes");
        scopeIdx.close();
        _newline();
        _writeSection("INSTANCES");
        _writeSection("Topics");
        _writeTopics(topics);
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
        _topic2Reference.clear();
        _topic2Supertypes.clear();
        _topic2Templates.clear();
    }

    private void _createSupertypeSubtypeRelationships(TypeInstanceIndex tiIdx,
            TopicMap tm, Collection<Association> assocs) {
        final Topic supertypeSubtype = tm.getTopicBySubjectIdentifier(TMDM.SUPERTYPE_SUBTYPE);
        final Topic supertype = tm.getTopicBySubjectIdentifier(TMDM.SUPERTYPE);
        final Topic subtype = tm.getTopicBySubjectIdentifier(TMDM.SUBTYPE);
        if (supertypeSubtype == null || supertype == null || subtype == null) {
            return;
        }
        for (Association assoc: tiIdx.getAssociations(supertypeSubtype)) {
            if (!assoc.getScope().isEmpty()) {
                continue;
            }
            if (assoc.getReifier() != null) {
                continue;
            }
            Collection<Role> roles = assoc.getRoles();
            if (roles.size() != 2) {
                continue;
            }
            Topic supertypePlayer = null;
            Topic subtypePlayer = null;
            for (Role role: roles) {
                if (role.getType().equals(supertype)) {
                    supertypePlayer = role.getPlayer();
                }
                else if (role.getType().equals(subtype)) {
                    subtypePlayer = role.getPlayer();
                }
            }
            if (supertypePlayer == null || subtypePlayer == null) {
                continue;
            }
            Collection<Topic> supertypes = _topic2Supertypes.get(subtypePlayer);
            if (supertypes == null) {
                supertypes = new HashSet<Topic>();
                _topic2Supertypes.put(subtypePlayer, supertypes);
            }
            supertypes.add(supertypePlayer);
            assocs.remove(assoc);
        }
    }

    private void _createTMCLTemplates(TopicMap topicMap,
            Collection<Topic> topics, 
            Collection<Association> assocs) {
        ITMCLPreprocessor tmclProcessor = new DefaultTMCLPreprocessor();
        tmclProcessor.process(topicMap, topics, assocs);
        _topic2Templates.putAll(tmclProcessor.getTopicToTemplatesMapping());
    }

    @SuppressWarnings("deprecation")
    private Locator[] _getSubjectIdentifiersToFilter(TypeFilter mode) {
        Locator[] toFilter = new Locator[0]; 
        switch (mode) {
        case TOPIC: {
            if (_tmcl) {
                toFilter = new Locator[] {
                        // Topic types
                        TMCL.TOPIC_TYPE,
                        TMCL.ASSOCIATION_TYPE,
                        TMCL.ROLE_TYPE,
                        TMCL.OCCURRENCE_TYPE,
                        TMCL.NAME_TYPE,
                        TMCL.SCOPE_TYPE,

                        // Model topics
                        TMCL.SCHEMA,
                        TMCL.CONSTRAINT,

                        // Constraint types
                        TMCL.ABSTRACT_TOPIC_TYPE_CONSTRAINT,
                        TMCL.OVERLAP_DECLARATION,
                        TMCL.SUBJECT_IDENTIFIER_CONSTRAINT,
                        TMCL.SUBJECT_LOCATOR_CONSTRAINT,
                        TMCL.TOPIC_NAME_CONSTRAINT,
                        TMCL.TOPIC_OCCURRENCE_CONSTRAINT,
                        TMCL.TOPIC_ROLE_CONSTRAINT, 
                        TMCL.SCOPE_CONSTRAINT,
                        TMCL.REIFIER_CONSTRAINT,
                        TMCL.ASSOCIATION_ROLE_CONSTRAINT,
                        TMCL.ROLE_COMBINATION_CONSTRAINT,
                        TMCL.TOPIC_REIFIES_CONSTRAINT,
                        TMCL.OCCURRENCE_DATATYPE_CONSTRAINT,
                        TMCL.UNIQUE_VALUE_CONSTRAINT,
                        TMCL.REGULAR_EXPRESSION_CONSTRAINT
                };
            }
            break;
        }
        case ASSOCIATION: {
            toFilter = new Locator[] {TMDM.TYPE_INSTANCE, TMDM.SUPERTYPE_SUBTYPE};
            if (_tmcl) {
                toFilter = new Locator[] {
                        toFilter[0],
                        toFilter[1],
                        // Association types - applies-to is no more 
                        TMCL.CONSTRAINED_TOPIC_TYPE,
                        TMCL.CONSTRAINED_STATEMENT, 
                        TMCL.CONSTRAINED_ROLE,
                        TMCL.OVERLAPS, 
                        TMCL.ALLOWED_SCOPE,
                        TMCL.ALLOWED_REIFIER,
                        TMCL.OTHER_CONSTRAINED_TOPIC_TYPE,
                        TMCL.OTHER_CONSTRAINED_ROLE, 
                        TMCL.BELONGS_TO_SCHEMA,
                };
            }
            break;
        }
        case ROLE: {
            toFilter = new Locator[] {TMDM.TYPE, TMDM.INSTANCE, TMDM.SUPERTYPE, TMDM.SUBTYPE};
            if (_tmcl) {
                toFilter = new Locator[] {toFilter[0], toFilter[1], toFilter[2], toFilter[3],
                        // Role types
                        TMCL.ALLOWS, 
                        TMCL.ALLOWED, 
                        TMCL.CONSTRAINS,
                        TMCL.CONSTRAINED, 
                        TMCL.CONTAINER, 
                        TMCL.CONTAINEE
                };
            }
            break;
        }
        case OCCURRENCE: {
            if (_tmcl) {
                toFilter = new Locator[] {
                        TMCL.CARD_MIN, 
                        TMCL.CARD_MAX, 
                        TMCL.DATATYPE,
                        TMCL.REGEXP,
                        TMCL.VALIDATION_EXPRESSION,
                        TMDM.SUBJECT    // the occurrence
                };
            }
            break;
        }
        case NAME: {
            toFilter = new Locator[] {TMDM.TOPIC_NAME};
            break;
        }
        }
        return toFilter;
    }

    private Collection<Topic> _filter(TopicMap topicMap, Collection<Topic> types,
            Collection<Topic> allTopics, TypeFilter mode) {
        return _filter(topicMap, types, allTopics, _getSubjectIdentifiersToFilter(mode));
    }

    private Collection<Topic> _filter(TopicMap topicMap, Collection<Topic> topics, Collection<Topic> allTopics, Locator...subjectIdentifiers) {
        for (Locator loc: subjectIdentifiers) {
            Topic topic = topicMap.getTopicBySubjectIdentifier(loc);
            if (_omitTopic(topic)) {
                topics.remove(topic);
                allTopics.remove(topic);
            }
        }
        return topics;
    }

    /**
     * Indicates if the provided <tt>topic</tt> has just one subject identifier
     * and provides no further properties.
     *
     * @param topic The topic to check.
     * @return <tt>true</tt> if the topic should be omitted, otherwise <tt>false</tt>.
     */
    private boolean _omitTopic(Topic topic) {
        return topic != null
                      && topic.getSubjectIdentifiers().size() == 1
                      && topic.getSubjectLocators().isEmpty()
                      && (!_exportIIDs || topic.getItemIdentifiers().isEmpty())
                      && topic.getTypes().isEmpty()
                      && topic.getNames().isEmpty() 
                      && topic.getOccurrences().isEmpty()
                      && topic.getReified() == null;
    }

    /**
     * Writes the header comment with the optional title, author, license etc.
     * information.
     *
     * @throws IOException In case of an error.
     */
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
        _out.write(")#");
    }

    /**
     * Writes the registered prefixes.
     *
     * @throws IOException In case of an error.
     */
    private void _writePrefixes() throws IOException {
        if (_prefixes.isEmpty()) {
            return;
        }
        _writeSection("Prefixes");
        String[] keys = _prefixes.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String ident: keys) {
            _out.write("%prefix " + ident + " <" + _prefixes.get(ident) + ">");
            _newline();
        }
    }

    /**
     * Writes the registered imports.
     *
     * @throws IOException In case of an error.
     */
    private void _writeImports() throws IOException {
        if (_imports.isEmpty()) {
            return;
        }
        _writeSection("Included Topic Maps");
        String[] imports = _imports.toArray(new String[_imports.size()]);
        Arrays.sort(imports);
        for (String imp: imports) {
            _out.write("%include ");
            _writeLocator(imp);
            _newline();
        }
    }

    /**
     * If <tt>topics</tt> is not empty, the topics will be removed from 
     * <tt>allTopics</tt> and written out under the specified section <tt>title</tt>. 
     *
     * @param topics The topics to serialize.
     * @param allTopics A collection of topics where the <tt>topics</tt> should be removed from.
     * @param title The title of the ontology section.
     * @throws IOException In case of an error.
     */
    private void _writeOntologySection(Collection<Topic> topics, Collection<Topic> allTopics, String title) throws IOException {
        if (topics.isEmpty()) {
            return;
        }
        allTopics.removeAll(topics);
        _writeSection(title);
        _writeTopics(topics);
    }

    /**
     * Sorts the specified collection of topics and serializes it.
     *
     * @param topics An unordered collection of topics.
     * @throws IOException In case of an error.
     */
    private void _writeTopics(Collection<Topic> topics) throws IOException {
        _lastReference = null;
        Topic[] topicArray = topics.toArray(new Topic[topics.size()]);
        Arrays.sort(topicArray, _topicComparator);
        for (Topic topic: topicArray) {
            _writeTopic(topic, true);
        }
    }

    /**
     * Serializes a the specified topic.
     *
     * @param topic The topic to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopic(Topic topic, boolean topicTypeHeader) throws IOException {
        final Reference mainIdentity = _getTopicReference(topic);
        Topic[] types = _getTypes(topic);
        if (topicTypeHeader) {
            if (types.length > 0) {
                Reference ref = _getTopicReference(types[0]);
                if (!ref.equals(_lastReference)) {
                    _writeSection("TT: " + ref);
                    _lastReference = ref;
                }
            }
            else if (_UNTYPED_REFERENCE != _lastReference) {
                _writeSection("TT: " + _UNTYPED_REFERENCE);
                _lastReference = _UNTYPED_REFERENCE;
            }
        }
        boolean wantSemicolon = false;
        _newline();
        _writeTopicRef(mainIdentity);
        _out.write(' ');
        for (Topic type: types) {
            _writeTypeInstance(type, wantSemicolon);
            wantSemicolon = true;
        }
        for (Topic supertype: _getSupertypes(topic)) {
            _writeSupertypeSubtype(supertype, wantSemicolon);
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
        for (Reference sid: _getSubjectIdentifiers(topic)) {
            _writeTopicRef(sid, wantSemicolon);
            wantSemicolon = true;
        }
        for (Reference slo: _getSubjectLocators(topic)) {
            _writeTopicRef(slo, wantSemicolon);
            wantSemicolon = true;
        }
        for (ITemplate tpl: _getTemplates(topic)) {
            _writeTemplate(tpl, wantSemicolon);
            wantSemicolon = true;
        }
        if (_exportIIDs) {
            for (Reference iid: _getItemIdentifiers(topic)) {
                _writeTopicRef(iid, wantSemicolon);
                wantSemicolon = true;
            }
        }
        _out.write('.');
        _newline();
    }

    /**
     * Returns a sorted array of subject identifiers for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is a subject identifier.
     * </p>
     *
     * @param topic The topic to retrieve the subject identifiers from.
     * @return A (maybe empty) sorted array of subject identifiers.
     */
    private Reference[] _getSubjectIdentifiers(Topic topic) {
        return _getLocators(topic, Reference.SID);
    }

    /**
     * Returns a sorted array of subject locators for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is a subject locator.
     * </p>
     *
     * @param topic The topic to retrieve the subject locators from.
     * @return A (maybe empty) sorted array of subject locators.
     */
    private Reference[] _getSubjectLocators(Topic topic) {
        return _getLocators(topic, Reference.SLO);
    }

    /**
     * Returns a sorted array of item identifiers for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is an item identifier.
     * </p>
     *
     * @param topic The topic to retrieve the item identifiers from.
     * @return A (maybe empty) sorted array of item identifiers.
     */
    private Reference[] _getItemIdentifiers(Topic topic) {
        Collection<Locator> iids = topic.getItemIdentifiers();
        if (iids.isEmpty()) {
            return _EMPTY_REFERENCE_ARRAY;
        }
        Collection<Reference> refs = new ArrayList<Reference>(iids.size());
        for (Locator iid: iids) {
            refs.add(Reference.createItemIdentifier(iid));
        }
        Reference mainIdentity = _getTopicReference(topic);
        if (!refs.remove(mainIdentity)
                && mainIdentity.type == Reference.ID) {
            String iri = _baseIRI + "#" + mainIdentity.reference;
            for (Reference r: refs) {
                if (r.reference.equals(iri)) {
                    refs.remove(r);
                    break;
                }
            }
        }
        Reference[] refArray = refs.toArray(new Reference[refs.size()]);
        Arrays.sort(refArray);
        return refArray;
    }

    /**
     * Returns a sorted array of {@link Reference}s which represent the 
     * provided locators.
     * <p>
     * The main identity is not part of the array.
     * </p>
     *
     * @param topic The topic.
     * @param kind Either {@link Reference#SID} or {@link Reference#SLO}.
     * @return A (maybe empty) sorted array.
     */
    private Reference[] _getLocators(Topic topic, int kind) {
        Set<Locator> locs = kind == Reference.SID ? topic.getSubjectIdentifiers()
                                                  : topic.getSubjectLocators();
        if (locs.isEmpty()) {
            return _EMPTY_REFERENCE_ARRAY;
        }
        Collection<Reference> refs = new ArrayList<Reference>(locs.size());
        for (Locator loc: locs) {
            refs.add(new Reference(kind, loc));
        }
        refs.remove(_getTopicReference(topic)); 
        Reference[] refArray = refs.toArray(new Reference[refs.size()]);
        Arrays.sort(refArray);
        return refArray;
    }

    /**
     * Returns a sorted array of types for the specified topic.
     *
     * @param topic The topic to retrieve the types from.
     * @return A sorted array of types.
     */
    private Topic[] _getTypes(Topic topic) {
        Set<Topic> types_ = topic.getTypes();
        Topic[] types = types_.toArray(new Topic[types_.size()]);
        Arrays.sort(types, _topicIdComparator);
        return types;
    }

    /**
     * Returns a sorted array of supertypes for the specified topic.
     *
     * @param topic The topic to retrieve the supertypes from.
     * @return A sorted array of supertypes.
     */
    private Topic[] _getSupertypes(Topic topic) {
        Collection<Topic> supertypes_ = _topic2Supertypes.get(topic);
        if (supertypes_ == null) {
            return _EMPTY_TOPIC_ARRAY;
        }
        Topic[] supertypes = supertypes_.toArray(new Topic[supertypes_.size()]);
        Arrays.sort(supertypes, _topicIdComparator);
        return supertypes;
    }

    /**
     * Returns a sorted array of templates for the specified topic.
     *
     * @param topic The topic to retrieve the templates from.
     * @return A sorted array of templates.
     */
    private ITemplate[] _getTemplates(Topic topic) {
        Collection<ITemplate> templates = _topic2Templates.get(topic);
        if (templates == null) {
            return _EMPTY_TEMPLATE_ARRAY;
        }
        ITemplate[] tplArray = templates.toArray(new ITemplate[templates.size()]);
        Arrays.sort(tplArray);
        return tplArray;
    }

    /**
     * Returns a sorted array of names for the specified topic.
     *
     * @param topic The topic to retrieve the names from.
     * @return A sorted array of names.
     */
    private Name[] _getNames(Topic topic) {
        Collection<Name> names = topic.getNames();
        Name[] nameArray = names.toArray(new Name[names.size()]);
        Arrays.sort(nameArray, _nameComparator);
        return nameArray;
    }

    /**
     * Returns a sorted array of occurrences for the specified topic.
     *
     * @param topic The topic to retrieve the occurrences from.
     * @return A sorted array of occurrences.
     */
    private Occurrence[] _getOccurrences(Topic topic) {
        Collection<Occurrence> occs = topic.getOccurrences();
        Occurrence[] occArray = occs.toArray(new Occurrence[occs.size()]);
        Arrays.sort(occArray, _occComparator);
        return occArray;
    }

    /**
     * Writes a type-instance relationship via "isa".
     *
     * @param type The type to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeTypeInstance(Topic type, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("isa ");
        _writeTopicRef(type);
    }

    /**
     * Writes a supertype-subtype relationship via "isa".
     *
     * @param supertype The supertype to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeSupertypeSubtype(Topic supertype, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("ako ");
        _writeTopicRef(supertype);
    }

    /**
     * Writes a template invocation.
     *
     * @param tpl The template invocation to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeTemplate(ITemplate tpl, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write(tpl.getName());
        _out.write('(');
        boolean wantComma = false;
        for (Object param: tpl.getParameters()) {
            if (wantComma) {
                _out.write(", ");
            }
            if (param instanceof ILiteral) {
                _writeLiteral((ILiteral) param);
            }
            else if (param instanceof Topic) {
                _writeTopicRef((Topic) param);
            }
            else {
                _writeTopicRef((Reference) param);
            }
            wantComma = true;
        }
        _out.write(')');
    }

    /**
     * Serializes the specified occurrence.
     *
     * @param occ The occurrence to serialize
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeOccurrence(IOccurrence occ, boolean wantSemicolon)  throws IOException {
        _writeSemicolon(wantSemicolon);
        _writeTopicRef(occ.getType());
        _out.write(": ");
        _writeLiteral(occ.getLiteral());
        _writeScope(occ);
        _writeReifier(occ);
    }

    /**
     * Serializes the specified name.
     *
     * @param name The name to serialize.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
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

    /**
     * Serializes the specified variant.
     *
     * @param variant The variant to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeVariant(IVariant variant) throws IOException {
        _out.write(" (");
        _writeLiteral(variant.getLiteral());
        _writeScope(variant);
        _writeReifier(variant);
        _out.write(')');
    }

    /**
     * Serializes the specified association.
     *
     * @param assoc The association to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeAssociation(Association assoc) throws IOException {
        _newline();
        _writeTopicRef(assoc.getType());
        _out.write('(');
        Role[] roles = assoc.getRoles().toArray(new Role[0]);
        Arrays.sort(roles, _roleComparator);
        _writeRole(roles[0]);
        for (int i=1; i<roles.length; i++) {
            _out.write(", ");
            _writeRole(roles[i]);
        }
        _out.write(')');
        _writeScope((IScoped) assoc);
        _writeReifier(assoc);
        _newline();
    }

    /**
     * Serializes the specified association role.
     *
     * @param role The association role to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeRole(Role role) throws IOException {
        _writeTopicRef(role.getType());
        _out.write(": ");
        _writeTopicRef(role.getPlayer());
        if (role.getReifier() != null) {
            _writeReifier(role);
            _out.write(" #( Great, you found a reason why a role should be reified, please tell us about it :) )# ");
        }
    }

    /**
     * Writes a semicolon and a newline character iff <tt>wantSemicolon</tt> is
     * <tt>true</tt>.
     * <p>
     * If a semicolon is written, optional whitespaces are written to ident the
     * next statement.
     * </p>
     *
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeSemicolon(boolean wantSemicolon) throws IOException {
        if (wantSemicolon) {
            _out.write(';');
            _newline();
            _out.write(_indent);
        }
    }

    /**
     * Serializes the scope of the scoped construct if the scope is not 
     * unconstrained.
     *
     * @param scoped The scoped construct from which the scope should be written.
     * @throws IOException In case of an error.
     */
    private void _writeScope(IScoped scoped) throws IOException {
        IScope scope = scoped.getScopeObject(); 
        if (!scope.isUnconstrained()) {
            Topic[] themes = scope.asSet().toArray(new Topic[scope.size()]);
            Arrays.sort(themes, _topicIdComparator);
            _out.write(" @");
            _writeTopicRef(themes[0]);
            for (int i=1; i<themes.length; i++) {
                _out.write(", ");
                _writeTopicRef(themes[i]);
            }
        }
    }

    /**
     * Writes the reifier iff <tt>reifiable</tt> is reified.
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
        else if (XSD.STRING.equals(datatype)) {
            _writeString(value);
        }
        else if (_isNativelySupported(lit)) {
            _out.write(value);
        }
        else {
            _writeString(value);
            _out.write("^^");
            _writeLocator(datatype); 
        }
    }

    /**
     * Writes a topic reference for the specified topic.
     *
     * @param topic The topic to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Topic topic) throws IOException {
        _writeTopicRef(_getTopicReference(topic));
    }

    /**
     * Writes the specied topic reference without whitespaces in front.
     *
     * @param topicRef The topic reference to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Reference topicRef) throws IOException {
        _writeTopicRef(topicRef, false);
    }

    /**
     * Writes the specified topic reference.
     *
     * @param topicRef The topic reference to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Reference topicRef, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        switch (topicRef.type) {
            case Reference.ID:
                _out.write(topicRef.reference);
                return;
            case Reference.IID:
                _out.write('^');
                break;
            case Reference.SLO:
                _out.write("= ");
                break;
            case Reference.SID:
                break;
            default:
                throw new RuntimeException("Internal error: Cannot match topic reference type " + topicRef.type);
        }
        _writeLocator(topicRef.reference);
    }

    /**
     * Serializes the provided <tt>string</tt>.
     * <p>
     * This method recognizes characters which have to be escaped.
     * </p>
     *
     * @param string The string to write.
     * @throws IOException In case of an error.
     */
    private void _writeString(String string) throws IOException {
        // Avoid escaping of "
        if (string.indexOf('"') > 0 && !string.endsWith("\"")) {
            _out.write(_TRIPLE_QUOTES);
            char[] ch = string.toCharArray();
            for (int i=0; i<ch.length; i++) {
                switch (ch[i]) {
                    case '\\':
                        _out.write("\\");
                    default:
                        _out.write(ch[i]);
                }
            }
            _out.write(_TRIPLE_QUOTES);
        }
        else {
            // Either the string ends with a " or the string is a 'normal' string
            _out.write('"');
            char[] ch = string.toCharArray();
            for (int i=0; i<ch.length; i++) {
                switch (ch[i]) {
                    case '"':
                    case '\\':
                        _out.write("\\");
                    default:
                        _out.write(ch[i]);
                }
            }
            _out.write('"');
        }
    }

    /**
     * Writes the specified locator (maybe abbreviated as QName).
     *
     * @param loc The locator to write.
     * @throws IOException In case of an error.
     */
    private void _writeLocator(final Locator loc) throws IOException {
        _writeLocator(loc.toExternalForm());
    }

    /**
     * Writes the specified locator <tt>reference</tt> which has been 
     * externalized..
     * <p>
     * If the reference starts with the base IRI followed by a hash ('#'), the
     * reference is abbreviated.
     * </p>
     *
     * @param reference The reference to write.
     * @throws IOException In case of an error.
     */
    private void _writeLocator(String reference) throws IOException {
        // If the reference starts with the base IRI and is followed by a #
        // a relative reference is written
        if (reference.startsWith(_baseIRI)) {
            String tmp = reference.substring(_baseIRI.length());
            if (tmp.charAt(0) == '#') {
                _out.write('<' + tmp + '>');
                return;
            }
        }
        // If no relative IRI was written, check the registered prefixes and
        // write a QName if a prefix matches
        for (Map.Entry<String, String> entry: _prefixes.entrySet()) {
            String iri = entry.getValue();
            if (reference.startsWith(iri)) {
                String localPart = reference.substring(iri.length());
                if (_isValidLocalPart(localPart)) {
                    _out.write(entry.getKey());
                    _out.write(':');
                    _out.write(localPart);
                    return;
                }
            }
        }
        // No relative IRI and no QName was written, write the reference as it is
        _out.write('<' + reference + '>');
    }

    /**
     * Returns a reference to the provided topic.
     * <p>
     * The reference to the topic stays stable during the serialization of
     * the topic map.
     * </p>
     *
     * @param topic The topic to retrieve a reference for.
     * @return A reference to the specified topic.
     */
    private Reference _getTopicReference(Topic topic) {
        Reference ref = _topic2Reference.get(topic);
        if (ref == null) {
            ref = _generateTopicReference(topic);
            _topic2Reference.put(topic, ref);
        }
        return ref;
    }

    /**
     * Returns a reference to the specified <tt>topic</tt>.
     *
     * @param topic The topic to generate a reference for.
     * @return A reference to the specified topic.
     */
    private Reference _generateTopicReference(final Topic topic) {
        Reference ref = null;
        Collection<Reference> refs = new ArrayList<Reference>();
        for (Locator iid: topic.getItemIdentifiers()) {
            String addr = iid.toExternalForm();
            int idx = addr.indexOf('#');
            if (idx > 0) {
                String id = addr.substring(idx+1);
                if (_isValidId(id) && !_isKeyword(id)) {
                    if (_keepAbsoluteIIDs && !addr.startsWith(_baseIRI)) {
                        refs.add(Reference.createItemIdentifier(iid));
                    }
                    else {
                        refs.add(Reference.createId(id));
                    }
                }
                else {
                    refs.add(Reference.createItemIdentifier(iid));
                }
            }
        }
        if (refs.isEmpty()) {
            for (Locator sid: topic.getSubjectIdentifiers()) {
                refs.add(Reference.createSubjectIdentifier(sid));
            }
        }
        if (refs.isEmpty()) {
            for (Locator sid: topic.getSubjectLocators()) {
                refs.add(Reference.createSubjectLocator(sid));
            }
        }
        if (!refs.isEmpty()) {
            Reference[] refArray = refs.toArray(new Reference[refs.size()]);
            Arrays.sort(refArray);
            ref = refArray[0];
        }
        else {
            ref = Reference.createItemIdentifier("#" + topic.getId());
        }
        return ref;
    }

    /**
     * Returns if the provided identifier is a CTM keyword.
     *
     * @param id The id to check.
     * @return <tt>true</tt> if <tt>id</tt> is a keyword, otherwise <tt>false</tt>.
     */
    private boolean _isKeyword(String id) {
        return id.length() == 3
                && ("def".equals(id)
                        || "end".equals(id)
                        || "isa".equals(id)
                        || "ako".equals(id));
    }

    /**
     * Returns if the provided <tt>id</tt> is a valid CTM topic identifier.
     *
     * @param id The id to check.
     * @return <tt>true</tt> if the id is valid, otherwise <tt>false</tt>.
     */
    private boolean _isValidId(String id) {
        return _ID_PATTERN.matcher(id).matches();
    }

    /**
     * Returns if the provided <tt>id</tt> is a valid local part of a QName.
     *
     * @param id The id to check.
     * @return <tt>true</tt> if the id is valid, otherwise <tt>false</tt>.
     */
    private boolean _isValidLocalPart(String id) {
        return _LOCAL_PATTERN.matcher(id).matches();
    }

    /**
     * Returns if the provided <tt>literal</tt> is supported by CTM natively.
     *
     * @param literal The literal to check.
     * @return <tt>true</tt> if the literal is supported, else <tt>false</tt>.
     */
    private boolean _isNativelySupported(ILiteral literal) {
        Locator datatype = literal.getDatatype();
        return XSD.STRING.equals(datatype)
                || XSD.ANY_URI.equals(datatype)
                || XSD.DECIMAL.equals(datatype)
                || XSD.INTEGER.equals(datatype)
                || XSD.DATE.equals(datatype)
                || XSD.DATE_TIME.equals(datatype)
                || XSD.DOUBLE.equals(datatype);
    }

    /**
     * Writes a EOL character.
     *
     * @throws IOException In case of an error.
     */
    private void _newline() throws IOException {
        _out.write('\n');
    }

    /**
     * Writes a section name.
     *
     * @param name The section name to write.
     * @throws IOException In case of an error.
     */
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

    static final class Template implements Comparable<Template> {
        private final String name;
        private final Collection<Object> params;
        private Template(String name) {
            this.name = name;
            this.params = new ArrayList<Object>();
        }
        @Override
        public int compareTo(Template o) {
            int res = name.compareTo(o.name);
            if (res == 0) {
                res = params.size() - o.params.size();
            }
            return res;
        }
    }

    /**
     * Represents a reference to a topic.
     */
    private static final class Reference implements Comparable<Reference> {
        static final int 
            ID = 0,
            SID = 1,
            SLO = 2,
            IID = 3;
        final int type;
        final String reference;

        private Reference(int type, String reference) {
            this.type = type;
            this.reference = reference;
        }

        public Reference(int type, Locator loc) {
            this(type, loc.toExternalForm());
        }

        public static Reference createId(String reference) {
            return new Reference(ID, reference);
        }
        
        public static Reference createSubjectIdentifier(Locator reference) {
            return new Reference(SID, reference);
        }

        public static Reference createSubjectLocator(Locator reference) {
            return new Reference(SLO, reference);
        }

        public static Reference createItemIdentifier(String string) {
            return new Reference(IID, string);
        }

        public static Reference createItemIdentifier(Locator reference) {
            return createItemIdentifier(reference.toExternalForm());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder();
            switch (type) {
                case Reference.ID:
                    return reference;
                case Reference.IID:
                    buff.append('^');
                    break;
                case Reference.SID:
                    break;
                case Reference.SLO:
                    buff.append("= ");
                    break;
            }
            buff.append('<');
            buff.append(reference);
            buff.append('>');
            return buff.toString();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Reference)) {
                return false;
            }
            Reference other = (Reference) obj;
            return type == other.type && reference.equals(other.reference); 
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return type + reference.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Reference o) {
            int res = type - o.type;
            if (res == 0) {
                res = reference.compareTo(o.reference);
            }
            return res;
        }

    }


    /*
     * Comparators.
     */

    private class TopicIdComparator implements Comparator<Topic> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Topic o1, Topic o2) {
            return _getTopicReference(o1).compareTo(_getTopicReference(o2));
        }
    }

    private class TopicComparator implements Comparator<Topic> {

        private final TopicTypeSetComparator _typesComparator;

        TopicComparator() {
            _typesComparator = new TopicTypeSetComparator();
        }

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Topic o1, Topic o2) {
            int res = _typesComparator.compare(o1.getTypes(), o2.getTypes());
            if (res == 0) {
                res = _getTopicReference(o1).compareTo(_getTopicReference(o2));
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
            return _topicIdComparator.compare(o1.getType(), o2.getType());
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
            if (reifier1 == reifier2) {
                return 0;
            }
            int res = 0;
            if (reifier1 == null) {
                res = reifier2 == null ? 0 : -1;
            }
            else if (reifier2 == null) {
                res = 1;
            }
            return res != 0 ? res : _topicIdComparator.compare(reifier1, reifier2);
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
            _roleSetComparator = new RoleSetComparator();
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
                res = _topicIdComparator.compare(o1.getPlayer(), o2.getPlayer());
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
     * Compares role sets. The parent of the roles is ignored! 
     */
    private final class RoleSetComparator extends AbstractSetComparator<Role> {

        private RoleComparator _roleCmp; 

        RoleSetComparator() {
            _roleCmp = new RoleComparator();
        }

        /* (non-Javadoc)
         * @see org.tinytim.mio.CXTMTopicMapWriter.AbstractSetComparator#compareContent(java.util.Set, java.util.Set, int)
         */
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

    private final class TopicTypeSetComparator extends AbstractSetComparator<Topic> {

        /* (non-Javadoc)
         * @see org.tinytim.mio.CXTMTopicMapWriter.AbstractSetComparator#compareContent(java.util.Set, java.util.Set, int)
         */
        @Override
        int compareContent(Set<Topic> o1, Set<Topic> o2,
                int size) {
            int res = 0;
            Topic[] types1 = o1.toArray(new Topic[size]);
            Topic[] types2 = o2.toArray(new Topic[size]);
            Arrays.sort(types1, _topicIdComparator);
            Arrays.sort(types2, _topicIdComparator);
            for (int i=0; i < size && res == 0; i++) {
                res = _topicIdComparator.compare(types1[i], types2[i]);
            }
            return res;
        }
    }

    /**
     * Compares the scope of two scoped Topic Maps constructs.
     */
    private final class ScopeComparator extends AbstractSetComparator<Topic> {

        /* (non-Javadoc)
         * @see org.tinytim.mio.CTMTopicMapWriter.AbstractSetComparator#compareContent(java.util.Set, java.util.Set, int)
         */
        @Override
        int compareContent(Set<Topic> o1, Set<Topic> o2, int size) {
            int res = 0 ;
            Topic[] topics1 = o1.toArray(new Topic[size]);
            Topic[] topics2 = o2.toArray(new Topic[size]);
            Arrays.sort(topics1, _topicIdComparator);
            Arrays.sort(topics2, _topicIdComparator);
            for (int i=0; i < size && res == 0; i++) {
                res = _topicIdComparator.compare(topics1[i], topics2[i]);
            }
            return res;
        }
    }

}
