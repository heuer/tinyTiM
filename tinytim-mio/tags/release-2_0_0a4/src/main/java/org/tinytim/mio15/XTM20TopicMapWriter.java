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
import java.util.Set;

import org.tinytim.AssociationImpl;
import org.tinytim.AssociationRoleImpl;
import org.tinytim.IConstruct;
import org.tinytim.IDatatypeAwareConstruct;
import org.tinytim.IReifiable;
import org.tinytim.ITyped;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicImpl;
import org.tinytim.TopicMapImpl;
import org.tinytim.TopicNameImpl;
import org.tinytim.VariantImpl;
import org.tinytim.voc.TMDM;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A {@link TopicMapWriter} implementation that serializes a topic map into
 * a <a href="http://www.isotopicmaps.org/sam/sam-xtm/">XTM 2.0</a> 
 * representation.
 * <p>
 * CAUTION: TMAPI 1.0 is not datatype-aware, the value of occurrences and 
 * variants will be serialized as xsd:string unless they have a locator value
 * (serialized as xsd:anyURI).
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class XTM20TopicMapWriter extends AbstractXTMTopicMapWriter {

    private static final String _XSD_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";
    private static final String _XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";

    private Topic _defaultNameType;

    /**
     * Creates a XTM 2.0 writer using "utf-8" encoding.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public XTM20TopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        super(out, baseIRI);
    }

    /**
     * Creates a XTM 2.0 writer.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public XTM20TopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        super(out, baseIRI, encoding);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(final TopicMap topicMap) throws IOException {
        TopicMapImpl tm = (TopicMapImpl) topicMap;
        // Cache the default name type. May be null, though
        _defaultNameType = ((TopicMapImpl)topicMap).getTopicBySubjectIdentifier(TMDM.TOPIC_NAME);
        _out.startDocument();
        _attrs.addAttribute("", "xmlns", "", "CDATA", "http://www.topicmaps.org/xtm/");
        _attrs.addAttribute("", "version", "", "CDATA", "2.0");
        if (topicMap.getReifier() != null) {
            _addReifier(_attrs, topicMap.getReifier());
        }
        _out.startElement("topicMap", _attrs);
        _writeItemIdentifiers(tm);
        for (Topic topic: tm.getTopics()) {
            _writeTopic(topic);
        }
        for (Association assoc: tm.getAssociations()) {
            _writeAssociation(assoc);
        }
        _out.endElement("topicMap");
        _out.endDocument();
    }

    protected void _writeTopic(final Topic topic_) throws IOException {
        TopicImpl topic = (TopicImpl) topic_;
        // Ignore the topic if it is the default name type and it has no further
        // characteristics
        if (_isDefaultNameType(topic)
                && topic.getReified() == null
                && topic.getSubjectIdentifiers().size() == 1
                && topic.getSubjectLocators().isEmpty()
                && topic.getItemIdentifiers().isEmpty()
                && topic.getRolesPlayed().isEmpty()
                && topic.getTypes().isEmpty()
                && topic.getTopicNames().isEmpty()
                && topic.getOccurrences().isEmpty()) {
            return;
        }
        _attrs.clear();
        _attrs.addAttribute("", "id", "", "CDATA", _getId(topic));
        _out.startElement("topic", _attrs);
        _writeItemIdentifiers(topic);
        _writeLocators("subjectIdentifier", topic.getSubjectIdentifiers());
        _writeLocators("subjectLocator", topic.getSubjectLocators());
        for (Topic type: topic.getTypes()) {
            _out.startElement("instanceOf");
            _writeTopicRef(type);
            _out.endElement("instanceOf");
        }
        for (TopicName name: topic.getTopicNames()) {
            _writeName(name);
        }
        for (Occurrence occ: topic.getOccurrences()) {
            _writeOccurrence(occ);
        }
        _out.endElement("topic");
    }

    protected void _writeAssociation(final Association assoc_) throws IOException {
        AssociationImpl assoc = (AssociationImpl) assoc_;
        Set<AssociationRole> roles = assoc.getAssociationRoles();
        if (roles.isEmpty()) {
            return;
        }
        _out.startElement("association", _reifier(assoc));
        _writeItemIdentifiers(assoc);
        _writeType(assoc);
        _writeScope(assoc);
        for (AssociationRole role: roles) {
            _writeRole(role);
        }
        _out.endElement("association");
    }

    protected void _writeRole(final AssociationRole role_) throws IOException {
        AssociationRoleImpl role = (AssociationRoleImpl) role_;
        _out.startElement("role", _reifier(role));
        _writeItemIdentifiers(role);
        _writeType(role);
        _writeTopicRef(role.getPlayer());
        _out.endElement("role");
    }

    protected void _writeName(final TopicName name_) throws IOException {
        TopicNameImpl name = (TopicNameImpl) name_;
        _out.startElement("name", _reifier(name));
        _writeItemIdentifiers(name);
        if (!_isDefaultNameType(name.getType())) {
            _writeType(name);
        }
        _writeScope(name);
        _out.dataElement("value", name.getValue());
        for (Variant variant: name.getVariants()) {
            _writeVariant(variant);
        }
        _out.endElement("name");
    }

    protected void _writeVariant(final Variant variant_) throws IOException {
        VariantImpl variant = (VariantImpl) variant_;
        _out.startElement("variant", _reifier(variant));
        _writeItemIdentifiers(variant);
        _writeScope(variant);
        _writeDatatypeAware(variant);
        _out.endElement("variant");
    }

    protected void _writeOccurrence(final Occurrence occ_) throws IOException {
        OccurrenceImpl occ = (OccurrenceImpl) occ_;
        _out.startElement("occurrence", _reifier(occ));
        _writeItemIdentifiers(occ);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endElement("occurrence");
    }

    private void _writeDatatypeAware(final IDatatypeAwareConstruct datatyped) throws IOException {
        _attrs.clear();
        final Locator datatype = datatyped.getDatatype();
        if (_XSD_ANY_URI.equals(datatype.getReference())) {
            _attrs.addAttribute("", "href", "", "CDATA", _getLocator(datatyped).toExternalForm());
            _out.emptyElement("resourceRef", _attrs);
        }
        else {
            if (!_XSD_STRING.equals(datatype.getReference())) {
                _attrs.addAttribute("", "datatype", "", "CDATA", datatype.toExternalForm());
            }
            _out.dataElement("resourceData", _attrs, datatyped.getValue2());
        }
    }

    /**
     * Returns the locator value from a datatype-ware construct.
     */
    private Locator _getLocator(IDatatypeAwareConstruct datatyped) {
        return datatyped instanceof Occurrence ? ((Occurrence) datatyped).getResource()
                                               : ((Variant) datatyped).getResource();
    }

    /**
     * If the <tt>reifiable</tt> is reified, this method returns attributes
     * with the a reference to the reifier, otherwise the attributes will be empty.
     *
     * @param reifiable The reifiable construct.
     * @return 
     */
    private Attributes _reifier(final IReifiable reifiable) {
        final Topic reifier = reifiable.getReifier();
        if (reifier != null) {
            _attrs.clear();
            _addReifier(_attrs, reifier);
            return _attrs;
        }
        return XMLWriter.EMPTY_ATTRS;
    }

    private void _addReifier(final AttributesImpl attrs, final Topic reifier) {
        attrs.addAttribute("", "reifier", "", "CDATA", "#" + _getId(reifier));
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "href", "", "CDATA", "#" + _getId(topic));
        _out.emptyElement("topicRef", _attrs);
    }

    private void _writeType(final ITyped typed) throws IOException {
        _out.startElement("type");
        _writeTopicRef(typed.getType());
        _out.endElement("type");
    }

    private void _writeScope(final ScopedObject scoped) throws IOException {
        @SuppressWarnings("unchecked")
        final Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return;
        }
        _out.startElement("scope");
        for (Topic theme: scope) {
            _writeTopicRef(theme);
        }
        _out.endElement("scope");
    }

    private void _writeItemIdentifiers(final IConstruct construct) throws IOException {
        _writeLocators("itemIdentity", construct.getItemIdentifiers());
    }

    private void _writeLocators(final String name, final Set<Locator> locs) throws IOException {
        for (Locator loc: locs) {
            _attrs.clear();
            _attrs.addAttribute("", "href", "", "CDATA", loc.toExternalForm());
            _out.emptyElement(name, _attrs);
        }
    }

    /**
     * Checks if the specified <tt>topic</tt> is the default TMDM name type.
     *
     * @param topic The topic to check, not <tt>null</tt>.
     * @return <tt>true</tt> if the topic is the default name type, otherwise <tt>false</tt>.
     */
    private boolean _isDefaultNameType(final Topic topic) {
        return topic == null || topic.equals(_defaultNameType);
    }

}
