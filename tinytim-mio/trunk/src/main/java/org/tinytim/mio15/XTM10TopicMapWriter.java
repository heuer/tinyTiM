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
import java.util.logging.Logger;

import org.tinytim.AssociationImpl;
import org.tinytim.AssociationRoleImpl;
import org.tinytim.IDatatypeAwareConstruct;
import org.tinytim.IReifiable;
import org.tinytim.ITyped;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicImpl;
import org.tinytim.TopicMapImpl;
import org.tinytim.TopicNameImpl;
import org.tinytim.VariantImpl;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import org.xml.sax.helpers.AttributesImpl;

/**
 * {@link TopicMapWriter} that serializes a topic map into 
 * a <a href="http://www.topicmaps.org/xtm/1.0/">XTM 1.0</a> representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class XTM10TopicMapWriter extends AbstractXTMTopicMapWriter {

    private static final String _XSD_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

    private static final Logger LOG = Logger.getLogger(XTM10TopicMapWriter.class.getName());

    //TODO: Export iids, 
    //      warn if name.type != default name type, 
    //      warn if datatype not in (xsd:string, xsd:anyURI) 

    /**
     * Creates a XTM 1.0 writer using "utf-8" encoding.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public XTM10TopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        super(out, baseIRI);
    }

    /**
     * Creates a XTM 1.0 writer.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public XTM10TopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        super(out, baseIRI, encoding);
    }

    private String _getId(IReifiable reifiable) {
        assert reifiable.getReifier() != null;
        return "reifier-id-" + reifiable.getReifier().getObjectId();
    }

    private void _addId(AttributesImpl attrs, final IReifiable reifiable) {
        if (reifiable.getReifier() == null) {
            return;
        }
        attrs.addAttribute("", "id", "", "CDATA", _getId(reifiable));
    }

    private void _addLocator(AttributesImpl attrs, Locator loc) {
        attrs.addAttribute("", "xlink:href", "", "CDATA", loc.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(final TopicMap topicMap_) throws IOException {
        TopicMapImpl topicMap = (TopicMapImpl) topicMap_;
        _out.startDocument();
        _attrs.clear();
        _attrs.addAttribute("", "xmlns", "", "CDATA", "http://www.topicmaps.org/xtm/1.0/");
        _attrs.addAttribute("", "xmlns:xlink", "", "CDATA", "http://www.w3.org/1999/xlink");
        _addId(_attrs, topicMap);
        _out.startElement("topicMap", _attrs);
        for (Topic topic: topicMap.getTopics()) {
            _writeTopic(topic);
        }
        for (Association assoc: topicMap.getAssociations()) {
            _writeAssociation(assoc);
        }
        _out.endElement("topicMap");
        _out.endDocument();
    }

    protected void _writeTopic(final Topic topic_) throws IOException {
        TopicImpl topic = (TopicImpl) topic_;
        _attrs.clear();
        _attrs.addAttribute("", "id", "", "CDATA",  _getId(topic));
        _out.startElement("topic", _attrs);
        _writeIdentities(topic);
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
        _attrs.clear();
        _addId(_attrs, assoc);
        _out.startElement("association", _attrs);
        _writeType(assoc);
        _writeScope(assoc);
        for (AssociationRole role: roles) {
            _writeRole(role);
        }
        _out.endElement("association");
    }

    protected void _writeRole(final AssociationRole role_) throws IOException {
        AssociationRoleImpl role = (AssociationRoleImpl) role_;
        _attrs.clear();
        _addId(_attrs, role);
        _out.startElement("member", _attrs);
        _out.startElement("roleSpec");
        _writeTopicRef(role.getType());
        _out.endElement("roleSpec");
        _writeTopicRef(role.getPlayer());
        _out.endElement("member");
    }

    protected void _writeName(final TopicName name_) throws IOException {
        TopicNameImpl name = (TopicNameImpl) name_;
        _attrs.clear();
        _addId(_attrs, name);
        _out.startElement("baseName", _attrs);
        _writeScope(name);
        _out.dataElement("baseNameString", name.getValue());
        for (Variant variant: name.getVariants()) {
            _writeVariant(variant);
        }
        _out.endElement("baseName");
    }

    protected void _writeVariant(final Variant variant_) throws IOException {
        VariantImpl variant = (VariantImpl) variant_;
        _attrs.clear();
        _addId(_attrs, variant);
        _out.startElement("variant", _attrs);
        _out.startElement("parameters");
        for (Topic theme: variant.getScope()) {
            _writeTopicRef(theme);
        }
        _out.endElement("parameters");
        _writeDatatypeAware(variant);
        _out.endElement("variant");
    }

    protected void _writeOccurrence(final Occurrence occ_) throws IOException {
        OccurrenceImpl occ = (OccurrenceImpl) occ_;
        _attrs.clear();
        _addId(_attrs, occ);
        _out.startElement("occurrence", _attrs);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endElement("occurrence");
    }

    private void _writeDatatypeAware(final IDatatypeAwareConstruct datatyped) throws IOException {
        if (_XSD_ANY_URI.equals(datatyped.getDatatype().getReference())) {
            _attrs.clear();
            _addLocator(_attrs, _getLocator(datatyped));
            _out.emptyElement("resourceRef", _attrs);
        }
        else {
            _out.dataElement("resourceData", datatyped.getValue2());
        }
    }

    /**
     * Returns the locator value from a datatype-ware construct.
     */
    private Locator _getLocator(IDatatypeAwareConstruct datatyped) {
        return datatyped instanceof Occurrence ? ((Occurrence) datatyped).getResource()
                                               : ((Variant) datatyped).getResource();
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "xlink:href", "", "CDATA", "#" + _getId(topic));
        _out.emptyElement("topicRef", _attrs);
    }

    private void _writeType(final ITyped typed) throws IOException {
        _out.startElement("instanceOf");
        _writeTopicRef(typed.getType());
        _out.endElement("instanceOf");
    }

    private void _writeScope(final ScopedObject scoped) throws IOException {
        @SuppressWarnings("unchecked")
        Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return;
        }
        _out.startElement("scope");
        for (Topic theme: scope) {
            _writeTopicRef(theme);
        }
        _out.endElement("scope");
    }

    protected void _writeIdentities(final TopicImpl topic) throws IOException {
        Set<Locator> sids = topic.getSubjectIdentifiers();
        Set<Locator> slos = topic.getSubjectLocators();
        IReifiable reifiable = topic.getReifiedConstruct();
        if (reifiable == null
                && sids.isEmpty()
                && slos.isEmpty()) {
            return;
        }
        _out.startElement("subjectIdentity");
        if (!slos.isEmpty()) {
            if (slos.size() > 1) {
                LOG.warning("The topic " + topic.getObjectId() + " has more than one subject locator, exporting just one");
            }
            // Choose one subject locator
            Locator slo = slos.iterator().next();
            _attrs.clear();
            _addLocator(_attrs, slo);
            _out.emptyElement("resourceRef", _attrs);
        }
        for (Locator sid: sids) {
            _attrs.clear();
            _addLocator(_attrs, sid);
            _out.emptyElement("subjectIndicatorRef", _attrs);
        }
        if (reifiable != null) {
            _attrs.clear();
            _attrs.addAttribute("", "xlink:href", "", "CDATA", "#" + _getId(reifiable));
            _out.emptyElement("subjectIndicatorRef", _attrs);
        }
        _out.endElement("subjectIdentity");
    }

}
