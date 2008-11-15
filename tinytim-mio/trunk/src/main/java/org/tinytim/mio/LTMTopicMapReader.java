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

import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that is able to deserialize the
 * <a href="http://www.ontopia.net/download/ltm.html">Linear Topic Map Notation (LTM) 1.3</a>.
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

//    /**
//     * Enables / disables processing of the "#MERGEMAP" directive.
//     * <p>
//     * The reader won't deserialize topic maps referenced by mergeMap if
//     * this feature is enabled (disabled by default).
//     * </p>
//     *
//     * @param ignore <tt>true</tt> to ignore "#MERGEMAP" directives, 
//     *                  otherwise <tt>false</tt>.
//     */
//    public void setIgnoreMergeMap(boolean ignore) {
//        _deserializer.setProperty(Property.IGNORE_MERGEMAP, ignore);
//    }
//
//    /**
//     * Returns if this reader ignores "#MERGEMAP" directives.
//     *
//     * @return <tt>true</tt> if "#MERGEMAP" is ignored, otherwise <tt>false</tt>.
//     */
//    public boolean isIgnoringMergeMap() {
//        Object property = _deserializer.getProperty(Property.IGNORE_MERGEMAP);
//        return property instanceof Boolean && Boolean.TRUE.equals(property);
//    }

//    /**
//     * Enables / disables processing of the "#INCLUDE" directive.
//     * <p>
//     * The reader won't deserialize topic maps referenced by "#INCLUDE" if
//     * this feature is enabled (disabled by default).
//     * </p>
//     *
//     * @param ignore <tt>true</tt> to ignore "#INCLUDE" directives, 
//     *                  otherwise <tt>false</tt>.
//     */
//    public void setIgnoreInclude(boolean ignore) {
//        _deserializer.setProperty(Property.IGNORE_INCLUDE, ignore);
//    }
//
//    /**
//     * Returns if this reader ignores "#INCLUDE" directives.
//     *
//     * @return <tt>true</tt> if "#INCLUDE" is ignored, otherwise <tt>false</tt>.
//     */
//    public boolean isIgnoringInclude() {
//        Object property = _deserializer.getProperty(Property.IGNORE_INCLUDE);
//        return property instanceof Boolean && Boolean.TRUE.equals(property);
//    }
}
