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
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;

/**
 * This interface represents a reader to deserialize a topic map from a source.
 * <p>
 * The reader is not meant to be reused and should be thrown away once one
 * of the <tt>read</tt> methods were invoked.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface ITopicMapReader {

    /**
     * Reads a topic map from <tt>source</tt> using the provided <tt>docIRI</tt>
     * to resolve IRIs against.
     *
     * @param source The source to read the serialized topic map from.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public void read(InputSource source, String docIRI) throws IOException;

    /**
     * Reads a topic map from <tt>source</tt> using the provided <tt>docIRI</tt>
     * to resolve IRIs against.
     *
     * @param source The file to read the serialized topic map from.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public void read(File source, String docIRI)throws IOException;

    /**
     * Reads a topic map from <tt>source</tt> using the provided <tt>docIRI</tt>
     * to resolve IRIs against.
     *
     * @param source The stream to read the serialized topic map from.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public void read(InputStream source, String docIRI)throws IOException;

}
