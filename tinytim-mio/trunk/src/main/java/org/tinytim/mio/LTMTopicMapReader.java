/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.tmapi.core.TopicMap;

import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that is able to deserialize the
 * <a href="http://www.ontopia.net/download/ltm.html">Linear Topic Map Notation (LTM) 1.3</a>.
 * This deserializer handles LTM instances somewhat different from the official
 * specification:
 * <ul>
 *  <li>Identifiers are not limited to <tt>[A-Za-z0-9_-.]</tt>, but may 
 *      contain Unicode characters.</li>
 *  <li>Roles are not postprocessed; if a role does not have a type, the type 
 *      is set to a default role type.</li>
 *  <li>Multiple subject locators are allowed: If a topic contains more than
 *      one subject locator, the subject locator is added to the topic.</li>
 *  <li>Subject identifiers / locators may occur in any order: The LTM 
 *      specification seems to mandate, that the subject identifiers must be 
 *      specified after an subject locator (if given); this implementation 
 *      does not mandate any order; a subject locator may be followed by an 
 *      subject identifier etc.</li>
 *  <li>Reification is handled directly without adding an additional item 
 *      identifier to the reified construct and without adding an additional
 *      subject identifier to the reifier (unless this "feature" is enabled
 *      explicitely; c.f. {@link #setLegacyReifierHandling(boolean)}</li>
 * </ul>
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class LTMTopicMapReader extends AbstractTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs. 
     */
    public LTMTopicMapReader(final TopicMap topicMap, final File source,
            final String docIRI) throws IOException {
        super(topicMap, Syntax.LTM, source, docIRI);
    }

    /**
     * Constructs a new instance.
     * <p>
     * The <tt>source</tt> is converted into an absolute IRI which will be
     * utilised as document IRI
     * </p>
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @throws IOException If an error occurs. 
     */
    public LTMTopicMapReader(final TopicMap topicMap, final File source)
            throws IOException {
        super(topicMap, Syntax.LTM, source);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     */
    public LTMTopicMapReader(final TopicMap topicMap, final InputStream source,
            final String docIRI) {
        super(topicMap, Syntax.LTM, source, docIRI);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the serialized topic map from.
     */
    public LTMTopicMapReader(final TopicMap topicMap, final Source source) {
        super(topicMap, Syntax.LTM, source);
    }

    /**
     * Indicates if reified construct should receive an item identifier of the 
     * form <tt>uri-of-file#--reified--id</tt> and the reifying topic should 
     * receive a subject identifier of the same form.
     * <p>
     * By default this "feature" is disabled; the parser sets [reifier] property 
     * of the reified construct and does not add any additional IRIs. To be 
     * conform to the LTM specification, this feature has to be enabled even if
     * it adds no value.
     * </p>
     *
     * @param enable <tt>true</tt> to enable the legacy mode, otherwise <tt>false</tt>.
     */
    public void setLegacyReifierHandling(boolean enable) {
        _deserializer.setProperty(Property.XTM_10_LEGACY, Boolean.valueOf(enable));
    }

    /**
     * Indicates if the parser handles the reification of constructs in a legacy
     * way.
     *
     * @return <tt>true</tt> if the parser is in legacy mode, otherwise <tt>false</tt>.
     */
    public boolean getLegacyReifierHandling() {
        return Boolean.TRUE.equals(_deserializer.getProperty(Property.XTM_10_LEGACY));
    }

}
