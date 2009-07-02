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

import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that deserializes 
 * <a href="http://www.cerny-online.com/jtm/">JSON Topic Maps (JTM)</a>.
 * <p>
 * This reader rejects JTM instances where the "item_type" is either
 * "role" or "variant" even if they are valid according to the JTM specification. 
 * </p>
 * <p>
 * Further the reader expects that at least one identity of a topic 
 * (item identifier, subject identifier, subject locator) occurs before any
 * occurrence / name.
 * </p>
 * <p>
 * The JTM instance must start with a "version" property followed by the 
 * "item_type" property before any further properties are specified.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class JTMTopicMapReader extends AbstractTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs. 
     */
    public JTMTopicMapReader(final TopicMap topicMap, final File source,
            final String docIRI) throws IOException {
        super(topicMap, Syntax.JTM, source, docIRI);
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
    public JTMTopicMapReader(final TopicMap topicMap, final File source)
            throws IOException {
        super(topicMap, Syntax.JTM, source);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     */
    public JTMTopicMapReader(final TopicMap topicMap, final InputStream source,
            final String docIRI) {
        super(topicMap, Syntax.JTM, source, docIRI);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the serialized topic map from.
     */
    public JTMTopicMapReader(final TopicMap topicMap, final Source source) {
        super(topicMap, Syntax.JTM, source);
    }

}
