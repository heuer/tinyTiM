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

import org.xml.sax.helpers.AttributesImpl;

/**
 * Abstract superclass for XTM serializers.
 * <p>
 * Provides a XML writer and takes care about the encoding.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractXTMTopicMapWriter extends AbstractTopicMapWriter {

    protected final AttributesImpl _attrs;

    protected final XMLWriter _out;

    /**
     * Creates a new instance using "utf-8" encoding.
     * 
     * @param out The output stream to write onto.
     * @param baseIRI The base IRI.
     * @throws IOException If an error occurs.
     */
    protected AbstractXTMTopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Creates a new instance.
     * 
     * @param out
     *            The output stream to write onto.
     * @param baseIRI
     *            The base IRI.
     * @param encoding
     *            The encoding to use.
     * @throws IOException
     *             If an error occurs.
     */
    protected AbstractXTMTopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        super(baseIRI);
        if (encoding == null) {
            throw new IOException("The encoding must not be null");
        }
        _out = new XMLWriter(out, encoding);
        _out.setPrettify(true);
        _attrs = new AttributesImpl();
    }

    /**
     * Enables / disables newlines and indentation of XML elements.
     * (newlines and indentation is enabled by default)
     *
     * @param prettify <tt>true</tt> to enable prettified XML, otherwise <tt>false</tt>.
     */
    public void setPrettify(boolean prettify) {
        _out.setPrettify(prettify);
    }

    /**
     * Returns if newlines and indentation are enabled.
     *
     * @return <tt>true</tt> if prettified XML is enabled, otherwise <tt>false</tt>.
     */
    public boolean getPrettify() {
        return _out.getPrettify();
    }

}
