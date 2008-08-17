/*
 * This is tinyTiM, a tiny Topic Maps engine.
 *
 * Copyright (C) 2008 Lars Heuer (heuer[at]semagia.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
package org.tinytim.mio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;
import org.xml.sax.InputSource;

import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.MIOException;
import com.semagia.mio.Syntax;

/**
 * Base class for {@link ITopicMapReader} implementations.
 * 
 * This class provides a layer to <tt>com.semagia.mio</tt> and handles
 * the discovery of an appropriate deserializer transparently.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapReader implements ITopicMapReader  {

    protected IDeserializer _deserializer;

    AbstractTopicMapReader(final TopicMap topicMap, final Syntax syntax) {
        _deserializer = DeserializerRegistry.createDeserializer(syntax);
        if (_deserializer == null) {
            throw new TMAPIRuntimeException("Appropriate deserializer not found for syntax " + syntax.getName());
        }
        _deserializer.setMapHandler(new TinyTimMapInputHandler(topicMap));
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapReader#read(java.io.File, java.lang.String)
     */
    public void read(File source, String baseIRI) throws IOException {
        read(new FileInputStream(source), baseIRI);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapReader#read(java.io.InputStream, java.lang.String)
     */
    public void read(InputStream source, String baseIRI) throws IOException {
        read(new InputSource(source), baseIRI);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.ITopicMapReader#read(org.xml.sax.InputSource, java.lang.String)
     */
    public void read(InputSource source, String baseIRI) throws IOException {
        try {
            _deserializer.parse(source, baseIRI);
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

}
