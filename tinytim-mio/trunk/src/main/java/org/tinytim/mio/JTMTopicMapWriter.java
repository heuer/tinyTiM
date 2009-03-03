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
 * a <a href="http://www.cerny-online.com/jtm/">JSON Topic Maps (JTM)</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class JTMTopicMapWriter implements TopicMapWriter {

    private static final String _TMDM_TYPE_INSTANCE = TMDM.TYPE_INSTANCE.getReference();
    private static final String _TMDM_TYPE = TMDM.TYPE.getReference();
    private static final String _TMDM_INSTANCE = TMDM.INSTANCE.getReference();

    private JSONWriter _out;
    private String _baseIRI;

    /**
     * Creates a JTM writer, using "utf-8" encoding.
     *
     * @param out The stream the JTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public JTMTopicMapWriter(OutputStream out, String baseIRI) throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Creates a JTM writer.
     *
     * @param out The stream the JTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public JTMTopicMapWriter(OutputStream out, String baseIRI, String encoding) throws IOException {
        if (encoding == null) {
            throw new IOException("The encoding must not be null");
        }
        _baseIRI = baseIRI;
        _out = new JSONWriter(out, encoding);
        _out.setPrettify(false);
    }

    /**
     * Enables / disables newlines and indentation of the JSON output.
     * (disabled by default)
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
    @Override
    public void write(TopicMap topicMap) throws IOException {
        _out.startDocument();
        _out.startObject();
        _writeKeyValue("version", "1.0");
        _writeKeyValue("item_type", "topicmap");
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

    /**
     * Serializes the specified topic.
     * <p>
     * The default name type topic is omitted in case it carries no further
     * characteristics.
     * </p>
     *
     * @param topic The topic to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeTopic(Topic topic) throws IOException {
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

    /**
     * Serializes the specified name.
     *
     * @param name The name to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeName(Name name) throws IOException {
        _out.startObject();
        _writeReifier(name);
        _writeItemIdentifiers(name);
        _writeType(name);
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

    /**
     * Serializes the specified variant.
     *
     * @param variant The variant to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeVariant(Variant variant) throws IOException {
        _out.startObject();
        _writeReifier(variant);
        _writeItemIdentifiers(variant);
        _writeScope(variant);
        _writeDatatypeAware(variant);
        _out.endObject();
    }

    /**
     * Serializes the specifed occurrence.
     *
     * @param occ The occurrence.
     * @throws IOException If an error occurs.
     */
    private void _writeOccurrence(Occurrence occ) throws IOException {
        _out.startObject();
        _writeReifier(occ);
        _writeItemIdentifiers(occ);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endObject();
    }

    /**
     * Writes the value and datatype of an occurrence or variant.
     *
     * @param datatyped The datatype-aware construct.
     * @throws IOException If an error occurs.
     */
    private void _writeDatatypeAware(DatatypeAware datatyped) throws IOException {
        Locator datatype = datatyped.getDatatype();
        String value = XSD.ANY_URI.equals(datatype) ? datatyped.locatorValue().toExternalForm()
                                                    : datatyped.getValue();
        _writeKeyValue("value", value);
        if (!XSD.STRING.equals(datatype)) {
            _writeKeyValue("datatype", datatype.toExternalForm());
        }
    }

    /**
     * Serializes the item identifiers of the specified construct.
     *
     * @param construct The construct to serialize the iids from.
     * @throws IOException If an error occurs.
     */
    private void _writeItemIdentifiers(Construct construct) throws IOException {
        _writeLocators("item_identifiers", construct.getItemIdentifiers());
    }

    /**
     * Writes a set of locators under the specified name. If the set is
     * empty, this method does nothing.
     *
     * @param name The name (item_identifiers, subject_identifiers, subject_locators)
     * @param locators A (maybe empty) set of locators.
     * @throws IOException If an error occurs.
     */
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

    /**
     * Serializes the specified asssociation.
     *
     * @param assoc The association to serialize.
     * @throws IOException If an error occurs.
     */
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

    /**
     * Serializes the specified role.
     *
     * @param role The role to serialize.
     * @throws IOException If an error occurs.
     */
    private void _writeRole(Role role) throws IOException {
        _out.startObject();
        _writeReifier(role);
        _writeItemIdentifiers(role);
        _writeType(role);
        _writeKeyValue("player", _topicRef(role.getPlayer()));
        _out.endObject();
    }

    /**
     * Writes the type of a typed construct.
     *
     * @param typed The typed construct.
     * @throws IOException If an error occurs.
     */
    private void _writeType(Typed typed) throws IOException {
        _writeKeyValue("type", _topicRef(typed.getType()));
    }

    /**
     * Writes the scope.
     *
     * @param scoped The scoped construct to retrieve the scope from.
     * @throws IOException If an error occurs.
     */
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

    /**
     * Serializes the reifier iff the reifier is not <tt>null</tt>.
     *
     * @param reifiable The reifiable construct to retrieve the reifier from.
     * @throws IOException If an error occurs.
     */
    private void _writeReifier(Reifiable reifiable) throws IOException {
        Topic reifier = reifiable.getReifier();
        if (reifier == null) {
            return;
        }
        _writeKeyValue("reifier", _topicRef(reifier));
    }

    /**
     * Writes a key/value pair.
     *
     * @param key The key to write.
     * @param value The value to write.
     * @throws IOException If an error occurs.
     */
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
        Set<Locator> locs = topic.getSubjectIdentifiers();
        if (!locs.isEmpty()) {
            return "si:" + locs.iterator().next().toExternalForm();
        }
        locs = topic.getSubjectLocators();
        if (!locs.isEmpty()) {
            return "sl:" + locs.iterator().next().toExternalForm();
        }
        locs = topic.getItemIdentifiers();
        if (!locs.isEmpty()) {
            return "ii:" + locs.iterator().next().toExternalForm();
        }
        return "ii:" + _baseIRI + "#" + topic.getId();
    }

}
