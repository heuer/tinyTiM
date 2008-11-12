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
import java.util.Set;

import org.tinytim.core.IConstruct;
import org.tinytim.voc.Namespace;
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
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A {@link TopicMapWriter} implementation that serializes a topic map into
 * a <a href="http://www.isotopicmaps.org/sam/sam-xtm/">XTM 2.0</a> 
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class XTM20Writer extends AbstractXTMWriter {

    /**
     * 
     *
     * @param baseIRI
     */
    public XTM20Writer(String baseIRI) {
        super(baseIRI);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(final TopicMap topicMap) throws IOException {
        _out.startDocument();
        _attrs.addAttribute("", "xmlns", "", "CDATA", Namespace.XTM_20);
        _attrs.addAttribute("", "version", "", "CDATA", "2.0");
        if (topicMap.getReifier() != null) {
            _addReifier(_attrs, topicMap.getReifier());
        }
        _out.startElement("topicMap", _attrs);
        _writeItemIdentifiers(topicMap);
        for (Topic topic: topicMap.getTopics()) {
            _out.newline();
            _writeTopic(topic);
            _out.newline();
        }
        for (Association assoc: topicMap.getAssociations()) {
            _out.newline();
            _writeAssociation(assoc);
            _out.newline();
        }
        _out.endElement("topicMap");
        _out.endDocument();
    }

    protected void _writeTopic(final Topic topic) throws IOException {
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
    }

    protected void _writeAssociation(final Association assoc) throws IOException {
        _out.startElement("association", _reifier(assoc));
        _writeItemIdentifiers(assoc);
        _writeType(assoc);
        _writeScope(assoc);
        for (Role role: assoc.getRoles()) {
            _out.newline();
            _writeRole(role);
            _out.newline();
        }
        _out.endElement("association");
    }

    protected void _writeRole(final Role role) throws IOException {
        _out.startElement("role", _reifier(role));
        _writeItemIdentifiers(role);
        _writeType(role);
        _writeTopicRef(role.getPlayer());
        _out.endElement("role");
    }

    protected void _writeName(final Name name) throws IOException {
        _out.startElement("name", _reifier(name));
        _writeItemIdentifiers(name);
        _writeType(name);
        _writeScope(name);
        _out.dataElement("value", name.getValue());
        for (Variant variant: name.getVariants()) {
            _out.newline();
            _writeVariant(variant);
            _out.newline();
        }
        _out.endElement("name");
    }

    protected void _writeVariant(final Variant variant) throws IOException {
        _out.startElement("variant", _reifier(variant));
        _writeItemIdentifiers(variant);
        _writeScope(variant);
        _writeDatatypeAware(variant);
        _out.endElement("variant");
    }

    protected void _writeOccurrence(final Occurrence occ) throws IOException {
        _out.startElement("occurrence", _reifier(occ));
        _writeItemIdentifiers(occ);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endElement("occurrence");
    }

    private void _writeDatatypeAware(final DatatypeAware datatyped) throws IOException {
        _attrs.clear();
        _out.newline();
        if (XSD.ANY_URI.equals(datatyped.getDatatype())) {
            _attrs.addAttribute("", "href", "", "CDATA", datatyped.locatorValue().toExternalForm());
            _out.emptyElement("resourceRef", _attrs);
        }
        else {
            final Locator datatype = datatyped.getDatatype();
            if (!XSD.STRING.equals(datatype)) {
                _attrs.addAttribute("", "datatype", "", "CDATA", datatype.toExternalForm());
            }
            _out.dataElement("resourceData", _attrs, datatyped.getValue());
        }
        _out.newline();
    }

    /**
     * If the <tt>reifiable</tt> is reified, this method returns attributes
     * with the a reference to the reifier, otherwise the attributes will be empty.
     *
     * @param reifiable The reifiable construct.
     * @return 
     */
    private Attributes _reifier(final Reifiable reifiable) {
        final Topic reifier = reifiable.getReifier();
        if (reifier != null) {
            _attrs.clear();
            _addReifier(_attrs, reifier);
            return _attrs;
        }
        return _EMPTY_ATTRS;
    }

    private void _addReifier(final AttributesImpl attrs, final Topic reifier) {
        attrs.addAttribute("", "reifier", "", "CDATA", "#" + _getId(reifier));
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _attrs.clear();
        _attrs.addAttribute("", "href", "", "CDATA", "#" + _getId(topic));
        _out.emptyElement("topicRef", _attrs);
    }

    private void _writeType(final Typed typed) throws IOException {
        final Topic type = typed.getType();
        if (((IConstruct) typed).isName() 
                    && type.getSubjectIdentifiers().contains(TMDM.TOPIC_NAME)) {
            return;
        }
        _out.newline();
        _out.startElement("type");
        _writeTopicRef(type);
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

    private void _writeItemIdentifiers(final Construct construct) throws IOException {
        _writeLocators("itemIdentity", construct.getItemIdentifiers());
    }

    private void _writeLocators(final String name, final Set<Locator> locs) throws IOException {
        for (Locator loc: locs) {
            _attrs.clear();
            _attrs.addAttribute("", "href", "", "CDATA", loc.toExternalForm());
            _out.newline();
            _out.emptyElement(name, _attrs);
            _out.newline();
        }
    }
}
