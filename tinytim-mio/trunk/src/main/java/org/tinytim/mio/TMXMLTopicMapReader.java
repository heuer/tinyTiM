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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.tmapi.core.TopicMap;

import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that is able to deserialize 
 * <a href="http://www.ontopia.net/topicmaps/tmxml.html">TM/XML</a> topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TMXMLTopicMapReader extends AbstractTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs. 
     */
    public TMXMLTopicMapReader(final TopicMap topicMap, final File source,
            final String docIRI) throws IOException {
        super(topicMap, Syntax.TMXML, source, docIRI);
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
    public TMXMLTopicMapReader(final TopicMap topicMap, final File source)
            throws IOException {
        super(topicMap, Syntax.TMXML, source);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     */
    public TMXMLTopicMapReader(final TopicMap topicMap, final InputStream source,
            final String docIRI) {
        super(topicMap, Syntax.TMXML, source, docIRI);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the serialized topic map from.
     */
    public TMXMLTopicMapReader(final TopicMap topicMap, final Source source) {
        super(topicMap, Syntax.TMXML, source);
    }

    /**
     * Enables / disables validation of the source.
     * <p>
     * The reader validates the XML document if this feature is enabled 
     * (disabled by default).
     * </p>
     *
     * @param validate <tt>true</tt> to enable validation, <tt>false</tt> to 
     *                  disable validation.
     */
    public void setValidation(boolean validate) {
        _deserializer.setProperty(Property.VALIDATE, validate);
    }

    /**
     * Returns if this reader validates the source.
     *
     * @return <tt>true</tt> if this reader validates the source, otherwise <tt>false</tt>.
     */
    public boolean isValidating() {
        return Boolean.TRUE.equals(_deserializer.getProperty(Property.VALIDATE));
    }

}
