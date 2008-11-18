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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * Common superclass for all XTM readers which provides some additional methods
 * to configure the reader.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractXTMTopicMapReader extends AbstractTopicMapReader {

    public AbstractXTMTopicMapReader(TopicMap topicMap, Syntax syntax,
            File source) throws IOException {
        super(topicMap, syntax, source);
    }

    public AbstractXTMTopicMapReader(TopicMap topicMap, Syntax syntax,
            File source, String docIRI) throws IOException {
        super(topicMap, syntax, source, docIRI);
    }

    public AbstractXTMTopicMapReader(TopicMap topicMap, Syntax syntax,
            InputStream source, String docIRI) {
        super(topicMap, syntax, source, docIRI);
    }

    public AbstractXTMTopicMapReader(TopicMap topicMap, Syntax syntax,
            Source source) {
        super(topicMap, syntax, source);
    }

    public AbstractXTMTopicMapReader(IMapHandler handler, Syntax syntax,
            Source source) {
        super(handler, syntax, source);
    }

    /**
     * Enables / disables processing of the "mergeMap" element.
     * <p>
     * The reader won't deserialize topic maps referenced by mergeMap if 
     * this feature is enabled (disabled by default).
     * </p>
     *
     * @param ignore <tt>true</tt> to ignore mergeMap elements, otherwise <tt>false</tt>.
     */
    public void setIgnoreMergeMap(boolean ignore) {
        _deserializer.setProperty(Property.IGNORE_MERGEMAP, ignore);
    }

    /**
     * Returns if this reader ignores mergeMap elements.
     *
     * @return <tt>true</tt> if mergeMap is ignored, otherwise <tt>false</tt>.
     */
    public boolean isIgnoringMergeMap() {
        return Boolean.TRUE.equals(_deserializer
                .getProperty(Property.IGNORE_MERGEMAP));
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
        return Boolean.TRUE
                .equals(_deserializer.getProperty(Property.VALIDATE));
    }

}
