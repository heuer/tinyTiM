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

import org.tinytim.voc.Namespace;
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

import org.xml.sax.helpers.AttributesImpl;

/**
 * {@link TopicMapWriter} that serializes a topic map into 
 * a <a href="http://www.topicmaps.org/xtm/1.0/">XTM 1.0</a> representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class XTM10Writer extends AbstractXTMWriter {

    //TODO: Export iids, 
    //      warn if len(slos) > 1, 
    //      warn if name.type != default name type, 
    //      warn if datatype not in (xsd:string, xsd:anyURI) 

    /**
     * 
     *
     * @param baseIRI
     */
    public XTM10Writer(final OutputStream out, final String baseIRI) {
        super(out, baseIRI);
    }

    private String _getId(Reifiable reifiable) {
        assert reifiable.getReifier() != null;
        return "reifier-id-" + reifiable.getReifier().getId();
    }

    private void _addId(AttributesImpl attrs, final Reifiable reifiable) {
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
    public void write(final TopicMap topicMap) throws IOException {
        _out.startDocument();
        _attrs.clear();
        _attrs.addAttribute("", "xmlns", "", "CDATA", Namespace.XTM_10);
        _attrs.addAttribute("", "xmlns:xlink", "", "CDATA", Namespace.XLINK);
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

    protected void _writeTopic(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "id", "", "CDATA",  _getId(topic));
        _out.newline();
        _out.startElement("topic", _attrs);
        _writeIdentities(topic);
        for (Topic type: topic.getTypes()) {
            _out.newline();
            _out.startElement("instanceOf");
            _writeTopicRef(type);
            _out.endElement("instanceOf");
            _out.newline();
        }
        for (Name name: topic.getNames()) {
            _out.newline();
            _writeName(name);
            _out.newline();
        }
        for (Occurrence occ: topic.getOccurrences()) {
            _out.newline();
            _writeOccurrence(occ);
            _out.newline();
        }
        _out.endElement("topic");
        _out.newline();
    }

    protected void _writeAssociation(final Association assoc) throws IOException {
        _attrs.clear();
        _addId(_attrs, assoc);
        _out.newline();
        _out.startElement("association", _attrs);
        _writeType(assoc);
        _writeScope(assoc);
        for (Role role: assoc.getRoles()) {
            _writeRole(role);
        }
        _out.endElement("association");
        _out.newline();
    }

    protected void _writeRole(final Role role) throws IOException {
        _attrs.clear();
        _addId(_attrs, role);
        _out.newline();
        _out.startElement("member", _attrs);
        _out.startElement("roleSpec");
        _writeTopicRef(role.getType());
        _out.endElement("roleSpec");
        _writeTopicRef(role.getPlayer());
        _out.endElement("member");
        _out.newline();
    }

    protected void _writeName(final Name name) throws IOException {
        _attrs.clear();
        _addId(_attrs, name);
        _out.startElement("baseName", _attrs);
        _writeScope(name);
        _out.dataElement("baseNameString", name.getValue());
        for (Variant variant: name.getVariants()) {
            _out.newline();
            _writeVariant(variant);
            _out.newline();
        }
        _out.endElement("baseName");
    }

    protected void _writeVariant(final Variant variant) throws IOException {
        _attrs.clear();
        _addId(_attrs, variant);
        _out.startElement("variant", _attrs);
        _out.newline();
        _out.startElement("parameters");
        for (Topic theme: variant.getScope()) {
            _writeTopicRef(theme);
        }
        _out.endElement("parameters");
        _out.newline();
        _writeDatatypeAware(variant);
        _out.endElement("variant");
    }

    protected void _writeOccurrence(final Occurrence occ) throws IOException {
        _attrs.clear();
        _addId(_attrs, occ);
        _out.startElement("occurrence", _attrs);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endElement("occurrence");
    }

    private void _writeDatatypeAware(final DatatypeAware datatyped) throws IOException {
        if (XSD.ANY_URI.equals(datatyped.getDatatype())) {
            _attrs.clear();
            _addLocator(_attrs, datatyped.locatorValue());
            _out.emptyElement("resourceRef", _attrs);
        }
        else {
            _out.dataElement("resourceData", datatyped.getValue());
        }
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "href", "", "CDATA", "#" + _getId(topic));
        _out.newline();
        _out.emptyElement("topicRef", _attrs);
        _out.newline();
    }

    private void _writeType(final Typed typed) throws IOException {
        _out.newline();
        _out.startElement("type");
        _writeTopicRef(typed.getType());
        _out.endElement("type");
        _out.newline();
    }

    private void _writeScope(final Scoped scoped) throws IOException {
        final Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return;
        }
        _out.newline();
        _out.startElement("scope");
        for (Topic theme: scope) {
            _writeTopicRef(theme);
        }
        _out.endElement("scope");
        _out.newline();
    }

    protected void _writeIdentities(final Topic topic) throws IOException {
        Set<Locator> sids = topic.getSubjectIdentifiers();
        Set<Locator> slos = topic.getSubjectLocators();
        Reifiable reifiable = topic.getReified();
        if (reifiable == null
                && sids.isEmpty()
                && slos.isEmpty()) {
            return;
        }
        _out.startElement("subjectIdentity");
        _out.newline();
        if (!slos.isEmpty()) {
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
        _out.newline();
    }

}
