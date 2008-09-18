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

import org.xml.sax.InputSource;

/**
 * This interface represents a reader to deserialize a topic map from a source.
 * <p>
 * The reader is not meant to be reused and should be thrown away once one
 * of the <tt>read</tt> methods were invoked.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
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
