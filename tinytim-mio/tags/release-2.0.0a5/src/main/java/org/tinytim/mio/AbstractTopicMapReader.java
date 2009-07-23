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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;

import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * Base class for {@link TopicMapReader} implementations.
 * <p>
 * This class provides a layer to <tt>com.semagia.mio</tt> and handles
 * the discovery of an appropriate deserializer transparently.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapReader implements TopicMapReader  {

    protected IDeserializer _deserializer;
    private final Source _source;

    /**
     * 
     * 
     * @param topicMap
     * @param syntax
     * @param source
     * @throws IOException
     */
    protected AbstractTopicMapReader(final TopicMap topicMap,
            final Syntax syntax, final File source) throws IOException {
        this(topicMap, syntax, source, source.toURI().toURL().toString());
    }

    /**
     * 
     * 
     * @param topicMap
     * @param syntax
     * @param source
     * @param docIRI
     * @throws IOException
     */
    protected AbstractTopicMapReader(final TopicMap topicMap,
            final Syntax syntax, final File source, final String docIRI)
            throws IOException {
        this(topicMap, syntax, new FileInputStream(source), docIRI);
    }

    /**
     * 
     * 
     * @param topicMap
     * @param syntax
     * @param source
     * @param docIRI
     */
    protected AbstractTopicMapReader(final TopicMap topicMap, final Syntax syntax,
            final InputStream source, final String docIRI) {
        this(topicMap, syntax, new Source(source, docIRI));
    }

    /**
     * 
     * 
     * @param topicMap
     * @param syntax
     * @param source
     */
    protected AbstractTopicMapReader(final TopicMap topicMap,
            final Syntax syntax, final Source source) {
        this(new TinyTimMapInputHandler(topicMap), syntax, source);
    }

    /**
     * 
     * 
     * @param handler
     * @param syntax
     * @param source
     */
    protected AbstractTopicMapReader(final IMapHandler handler,
            final Syntax syntax, final Source source) {
        this(handler, DeserializerRegistry.createDeserializer(syntax), source, syntax);
    }

    protected AbstractTopicMapReader(final IMapHandler handler,
            final IDeserializer deserializer, final Source source, final Syntax syntax) {
        if (deserializer == null) {
            throw new IllegalArgumentException("Deserializer for " + syntax.getName() + " not found");
        }
        _deserializer = deserializer;
        _deserializer.setProperty(Property.VALIDATE, Boolean.FALSE);
        _deserializer.setMapHandler(handler);
        _source = source;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapReader#read()
     */
    @Override
    public void read() throws IOException {
        try {
            _deserializer.parse(_source);
            postProcess();
        }
        catch (MIOException ex) {
            if (ex.getException() instanceof IOException) {
                throw (IOException) ex.getException();
            }
            else {
                throw new TMAPIRuntimeException(ex);
            }
        }
        finally {
            _deserializer = null;
        }
    }

    /**
     * Called if parsing has been finished without errors, does nothing by default.
     * <p>
     * The {@link #_deserializer} is still available.
     * </p>
     */
    protected void postProcess() {
        // noop.
    }
}
