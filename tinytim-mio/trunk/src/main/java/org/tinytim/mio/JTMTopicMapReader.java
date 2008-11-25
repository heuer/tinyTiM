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
import java.io.Reader;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * {@link TopicMapReader} implementation that deserializes 
 * <a href="http://www.cerny-online.com/topincs/technical-whitepaper">JSON Topic Maps (JTM)</a>.
 * <p>
 * The reader does not accept fragments, i.e. only a topic in a JSON document;
 * each JTM instance must start with a topic map container, i.e. 
 * <tt>{"topics":[...]"}</tt>. The TMDM constructs must provide all
 * properties, i.e. it is not allowed to omit the type of an association. The 
 * only exception are topic names: If the "type" property is not provided, the
 * default topic name type is automatically assigned.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
@SuppressWarnings("unchecked")
public class JTMTopicMapReader implements TopicMapReader {

    private TopicMap _tm;
    private Reader _reader;

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param reader The reader to read the JSON encoded topic map from.
     * @throws IOException If an error occurs.
     */
    public JTMTopicMapReader(TopicMap topicMap, Reader reader) throws IOException {
        _tm = topicMap;
        _reader = reader;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.TopicMapReader#read()
     */
    public void read() throws IOException {
        Object obj = JSONValue.parse(_reader);
        if (!(obj instanceof JSONObject)) {
            // Nothing to do.
            return;
        }
        JSONObject map = (JSONObject) obj;
        SimpleMapHandler mapHandler = SimpleMapHandler.create(new TinyTimMapInputHandler(_tm));
        try {
            mapHandler.startTopicMap();
            _handleReifier(mapHandler, map);
            _handleItemIdentifiers(mapHandler, map);
            _handleTopics(mapHandler, (List<JSONObject>) map.get("topics"));
            _handleAssociations(mapHandler, (List<JSONObject>) map.get("associations"));
            mapHandler.endTopicMap();
        }
        catch (MIOException ex) {
            throw new TMAPIRuntimeException(ex);
        }
    }

    private void _handleTopics(SimpleMapHandler mapHandler,
            List<JSONObject> jsonArray) throws MIOException {
        if (jsonArray == null) {
            return;
        }
        int length = jsonArray.size();
        for (int i=0; i<length; i++) {
            _handleTopic(mapHandler, jsonArray.get(i));
        }
    }

    private void _handleTopic(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        boolean seenIdentity = false;
        List<String> array = (List<String>) jsonObject.get("item_identifiers");
        if (array != null) {
            seenIdentity = true;
            mapHandler.startTopic(Ref.createItemIdentifier(array.get(0)));
            int length = array.size();
            for (int i=1; i<length; i++) {
                mapHandler.itemIdentifier(array.get(i));
            }
        }
        array = (List<String>) jsonObject.get("subject_identifiers");
        if (array != null) {
            int start = 0;
            if (!seenIdentity) {
                seenIdentity = true;
                start = 1;
                mapHandler.startTopic(Ref.createSubjectIdentifier(array.get(0)));
            }
            int length = array.size();
            for (int i=start; i<length; i++) {
                mapHandler.subjectIdentifier(array.get(i));
            }
        }
        array = (List<String>) jsonObject.get("subject_locators");
        if (array != null) {
            int start = 0;
            if (!seenIdentity) {
                seenIdentity = true;
                start = 1;
                mapHandler.startTopic(Ref.createSubjectLocator(array.get(0)));
            }
            int length = array.size();
            for (int i=start; i<length; i++) {
                mapHandler.subjectLocator(array.get(i));
            }
        }
        if (!seenIdentity) {
            throw new MIOException("Topic without any identity: " + jsonObject);
        }
        List<JSONObject> objects = (List<JSONObject>) jsonObject.get("occurrences");
        if (objects != null) {
            int length = objects.size();
            for (int i=0; i<length; i++) {
                _handleOccurrence(mapHandler, objects.get(i));
            }
        }
        objects = (List<JSONObject>) jsonObject.get("names");
        if (objects != null) {
            int length = objects.size();
            for (int i=0; i<length; i++) {
                _handleName(mapHandler, objects.get(i));
            }
        }
        mapHandler.endTopic();
    }

    private void _handleOccurrence(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        mapHandler.startOccurrence();
        _handleType(mapHandler, jsonObject, true);
        _handleScope(mapHandler, jsonObject);
        _handleReifier(mapHandler, jsonObject);
        _handleItemIdentifiers(mapHandler, jsonObject);
        _handleValueDatatype(mapHandler, jsonObject);
        mapHandler.endOccurrence();
    }

    private void _handleValueDatatype(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        String value = (String) jsonObject.get("value");
        if (value == null) {
            throw new MIOException("The value must not be null: " + jsonObject);
        }
        String datatype = (String) jsonObject.get("datatype");
        if (datatype == null) {
            throw new MIOException("The datatype must not be null: " + jsonObject);
        }
        mapHandler.value(value, datatype);
    }

    private void _handleName(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        mapHandler.startName();
        _handleType(mapHandler, jsonObject, false);
        _handleScope(mapHandler, jsonObject);
        _handleReifier(mapHandler, jsonObject);
        _handleItemIdentifiers(mapHandler, jsonObject);
        String value = (String) jsonObject.get("value");
        if (value == null) {
            throw new MIOException("The value of a name must not be null: " + jsonObject);
        }
        mapHandler.value(value);
        List<JSONObject> variants = (List<JSONObject>) jsonObject.get("variants");
        if (variants != null) {
            int length = variants.size();
            for (int i=0; i<length; i++) {
                _handleVariant(mapHandler, variants.get(i));
            }
        }
        mapHandler.endName();
    }

    private void _handleVariant(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        mapHandler.startVariant();
        _handleScope(mapHandler, jsonObject);
        _handleReifier(mapHandler, jsonObject);
        _handleItemIdentifiers(mapHandler, jsonObject);
        _handleValueDatatype(mapHandler, jsonObject);
        mapHandler.endVariant();
    }

    private void _handleAssociations(SimpleMapHandler mapHandler,
            List<JSONObject> jsonArray) throws MIOException {
        if (jsonArray == null) {
            return;
        }
        int length = jsonArray.size();
        for (int i=0; i<length; i++) {
            _handleAssociation(mapHandler, jsonArray.get(i));
        }
    }

    private void _handleAssociation(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        List<JSONObject> roles = (List<JSONObject>) jsonObject.get("roles");
        if (roles == null) {
            throw new MIOException("No roles specified: " + jsonObject);
        }
        mapHandler.startAssociation();
        _handleType(mapHandler, jsonObject, true);
        _handleScope(mapHandler, jsonObject);
        _handleReifier(mapHandler, jsonObject);
        _handleItemIdentifiers(mapHandler, jsonObject);
        int length = roles.size();
        for (int i=0; i<length; i++) {
            _handleRole(mapHandler, roles.get(i));
        }
        mapHandler.endAssociation();
    }

    private void _handleRole(SimpleMapHandler mapHandler,
            JSONObject jsonObject) throws MIOException {
        mapHandler.startRole();
        _handleType(mapHandler, jsonObject, true);
        _handleReifier(mapHandler, jsonObject);
        _handleItemIdentifiers(mapHandler, jsonObject);
        String playerIRI = (String) jsonObject.get("player");
        if (playerIRI == null) {
            throw new MIOException("No player defined: " + jsonObject);
        }
        mapHandler.player(Ref.createItemIdentifier(playerIRI));
        mapHandler.endRole();
    }

    private void _handleScope(SimpleMapHandler mapHandler, JSONObject jsonObject) throws MIOException {
        List<String> array = (List<String>) jsonObject.get("scope");
        if (array == null) {
            return;
        }
        int length = array.size();
        mapHandler.startScope();
        for (int i=0; i<length; i++) {
            mapHandler.theme(Ref.createItemIdentifier(array.get(i)));
        }
        mapHandler.endScope();
    }

    private void _handleType(SimpleMapHandler mapHandler, JSONObject jsonObj, boolean notNull) throws MIOException {
        String typeIRI = (String) jsonObj.get("type");
        if (typeIRI != null) {
            mapHandler.type(Ref.createItemIdentifier(typeIRI));
        }
        else if (notNull) {
            throw new MIOException("The type must not be null: " + jsonObj);
        }
    }

    private void _handleReifier(SimpleMapHandler mapHandler, JSONObject jsonObj) throws MIOException {
        String reifierIRI = (String) jsonObj.get("reifier");
        if (reifierIRI != null) {
            mapHandler.reifier(Ref.createItemIdentifier(reifierIRI));
        }
    }

    private void _handleItemIdentifiers(SimpleMapHandler mapHandler, JSONObject jsonObj) throws MIOException {
        List<String> iids = (List<String>)jsonObj.get("item_identifiers");
        if (iids != null) {
            int length = iids.size();
            for (int i=0; i<length; i++) {
                mapHandler.itemIdentifier(iids.get(i));
            }
        }
    }

}
