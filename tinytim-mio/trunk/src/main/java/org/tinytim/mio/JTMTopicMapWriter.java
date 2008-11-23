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

import org.tinytim.internal.api.IIndexManagerAware;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.IScoped;
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
import org.tmapi.index.TypeInstanceIndex;

/**
 * A {@link TopicMapWriter} implementation that serializes a topic map into
 * a <a href="http://www.cerny-online.com/topincs/">JSON Topic Maps</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class JTMTopicMapWriter implements TopicMapWriter {

    private static final String _TMDM_TYPE_INSTANCE = TMDM.TYPE_INSTANCE.getReference();
    private static final String _TMDM_TYPE = TMDM.TYPE.getReference();
    private static final String _TMDM_INSTANCE = TMDM.INSTANCE.getReference();

    private JSONWriter _out;
    private String _baseIRI;
    private Topic _defaultNameType;

    /**
     * Creates a JTM writer.
     *
     * @param out The stream the JTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public JTMTopicMapWriter(OutputStream out, String baseIRI) throws IOException {
        _baseIRI = baseIRI;
        _out = new JSONWriter(out);
        _out.setPrettify(true);
    }

    /**
     * Enables / disables newlines and indentation of JSON elements.
     * (newlines and indentation is enabled by default)
     *
     * @param prettify <tt>true</tt> to enable prettified JSON, otherwise <tt>false</tt>.
     */
    public void setPrettify(boolean prettify) {
        _out.setPrettify(prettify);
    }

    /**
     * Returns if newlines and indentation are enabled.
     *
     * @return <tt>true</tt> if prettified JSON is enabled, otherwise <tt>false</tt>.
     */
    public boolean getPrettify() {
        return _out.getPrettify();
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(TopicMap topicMap) throws IOException {
        _defaultNameType = topicMap.getTopicBySubjectIdentifier(TMDM.TOPIC_NAME);
        _out.startDocument();
        _out.startObject();
        _writeReifier(topicMap);
        _writeItemIdentifiers(topicMap);
        _out.key("topics");
        _out.startArray();
        for (Topic topic: topicMap.getTopics()) {
            _writeTopic(topic);
        }
        _out.endArray();
        _out.key("associations");
        _out.startArray();
        for (Association assoc: topicMap.getAssociations()) {
            _writeAssociation(assoc);
        }
        // Write type-instance relationships
        TypeInstanceIndex tiIdx = ((IIndexManagerAware) topicMap).getIndexManager().getTypeInstanceIndex();
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        for (Topic type: tiIdx.getTopicTypes()) {
            for (Topic instance: tiIdx.getTopics(type)) {
                _writeTypeInstance(type, instance);
            }
        }
        tiIdx.close();
        _out.endArray();
        _out.endObject();
        _out.endDocument();
    }

    private void _writeTopic(Topic topic) throws IOException {
        // Ignore the topic if it is the default name type and it has no further
        // characteristics
        if (_isDefaultNameType(topic)
                && topic.getReified() == null
                && topic.getSubjectIdentifiers().size() == 1
                && topic.getSubjectLocators().isEmpty()
                && topic.getItemIdentifiers().isEmpty()
                && topic.getRolesPlayed().isEmpty()
                && topic.getTypes().isEmpty()
                && topic.getNames().isEmpty()
                && topic.getOccurrences().isEmpty()) {
            return;
        }
        _out.startObject();
        _writeItemIdentifiers(topic);
        _writeLocators("subject_identifiers", topic.getSubjectIdentifiers());
        _writeLocators("subject_locators", topic.getSubjectLocators());
        Set<Name> names = topic.getNames();
        if (!names.isEmpty()) {
            _out.key("names");
            _out.startArray();
            for (Name name: names) {
                _writeName(name);
            }
            _out.endArray();
        }
        Set<Occurrence> occs = topic.getOccurrences();
        if (!occs.isEmpty()) {
            _out.key("occurrences");
            _out.startArray();
            for (Occurrence occ: occs) {
                _writeOccurrence(occ);
            }
            _out.endArray();
        }
        _out.endObject();
    }

    private void _writeName(Name name) throws IOException {
        _out.startObject();
        _writeReifier(name);
        _writeItemIdentifiers(name);
        if (!_isDefaultNameType(name.getType())) {
            _writeType(name);
        }
        _writeScope(name);
        _writeKeyValue("value", name.getValue());
        Set<Variant> variants = name.getVariants();
        if (!variants.isEmpty()) {
            _out.key("variants");
            _out.startArray();
            for (Variant variant: variants) {
                _writeVariant(variant);
            }
            _out.endArray();
        }
        _out.endObject();
    }

    private void _writeVariant(Variant variant) throws IOException {
        _out.startObject();
        _writeReifier(variant);
        _writeItemIdentifiers(variant);
        _writeScope(variant);
        _writeDatatypeAware(variant);
        _out.endObject();
    }

    private void _writeOccurrence(Occurrence occ) throws IOException {
        _out.startObject();
        _writeReifier(occ);
        _writeItemIdentifiers(occ);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endObject();
    }

    private void _writeDatatypeAware(DatatypeAware datatyped) throws IOException {
        Locator datatype = datatyped.getDatatype();
        String value = XSD.ANY_URI.equals(datatype) ? datatyped.locatorValue().toExternalForm()
                                                    : datatyped.getValue();
        _writeKeyValue("value", value);
        _writeKeyValue("datatype", datatype.toExternalForm());
    }

    private void _writeItemIdentifiers(Construct construct) throws IOException {
        _writeLocators("item_identifiers", construct.getItemIdentifiers());
    }

    private void _writeLocators(String name, Set<Locator> locators) throws IOException {
        if (locators.isEmpty()) {
            return;
        }
        _out.key(name);
        _out.startArray();
        for (Locator loc: locators) {
            _out.value(loc.toExternalForm());
        }
        _out.endArray();
    }

    private void _writeAssociation(Association assoc) throws IOException {
        Set<Role> roles = assoc.getRoles();
        if (roles.isEmpty()) {
            return;
        }
        _out.startObject();
        _writeReifier(assoc);
        _writeItemIdentifiers(assoc);
        _writeType(assoc);
        _writeScope(assoc);
        _out.key("roles");
        _out.startArray();
        for (Role role: roles) {
            _writeRole(role);
        }
        _out.endArray();
        _out.endObject();
    }

    private void _writeRole(Role role) throws IOException {
        _out.startObject();
        _writeReifier(role);
        _writeItemIdentifiers(role);
        _writeType(role);
        _writeKeyValue("player", _topicRef(role.getPlayer()));
        _out.endObject();
    }

    private void _writeType(Typed typed) throws IOException {
        _writeKeyValue("type", _topicRef(typed.getType()));
    }

    private void _writeScope(Scoped scoped) throws IOException {
        IScope scope = ((IScoped) scoped).getScopeObject();
        if (scope.isUnconstrained()) {
            return;
        }
        _out.key("scope");
        _out.startArray();
        for (Topic theme: scope) {
            _out.value(_topicRef(theme));
        }
        _out.endArray();
    }

    /**
     * Writes a type-instance association.
     * <p>
     * JTM provides no shortcut like "instanceOf" at the topic level to indicate
     * that a topic is an instance of another topic, all type-instance 
     * relationships must be encoded as associations.
     * </p>
     *
     * @param type The topic which plays the <tt>tmdm:type</tt> role.
     * @param instance The topic which plays the <tt>tmdm:instance</tt> role.
     * @throws IOException If an error occurs.
     */
    private void _writeTypeInstance(Topic type, Topic instance) throws IOException {
        _out.startObject();
        _writeKeyValue("type", _TMDM_TYPE_INSTANCE);
        _out.key("roles");
        _out.startArray();
        _out.startObject();
        _writeKeyValue("type", _TMDM_TYPE);
        _writeKeyValue("player", _topicRef(type));
        _out.endObject();
        _out.startObject();
        _writeKeyValue("type", _TMDM_INSTANCE);
        _writeKeyValue("player", _topicRef(instance));
        _out.endObject();
        _out.endArray();
        _out.endObject();
    }

    private void _writeReifier(Reifiable reifiable) throws IOException {
        Topic reifier = reifiable.getReifier();
        if (reifier == null) {
            return;
        }
        _writeKeyValue("reifier", _topicRef(reifier));
    }

    private void _writeKeyValue(String key, String value) throws IOException {
        _out.key(key);
        _out.value(value);
    }

    /**
     * Returns an IRI which is usable to as reference to the specified topic.
     *
     * @param topic The topic.
     * @return An IRI.
     */
    private String _topicRef(Topic topic) {
        Set<Locator> locs = topic.getItemIdentifiers();
        if (!locs.isEmpty()) {
            return locs.iterator().next().toExternalForm();
        }
        locs = topic.getSubjectIdentifiers();
        if (!locs.isEmpty()) {
            return locs.iterator().next().toExternalForm();
        }
        return _baseIRI + "#" + topic.getId();
    }

    /**
     * Checks if the specified <tt>topic</tt> is the default TMDM name type.
     *
     * @param topic The topic to check, not <tt>null</tt>.
     * @return <tt>true</tt> if the topic is the default name type, otherwise <tt>false</tt>.
     */
    private boolean _isDefaultNameType(final Topic topic) {
        return topic.equals(_defaultNameType);
    }

}
