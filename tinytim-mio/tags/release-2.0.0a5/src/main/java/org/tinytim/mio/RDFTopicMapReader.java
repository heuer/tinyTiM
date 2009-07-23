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
import java.net.URL;

/**
 * Specialization of the {@link TopicMapReader} which provides methods to
 * define an external RDF to Topic Maps (RTM) mapping source.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface RDFTopicMapReader extends TopicMapReader {

    /**
     * Represents the syntax of a RTM mapping.
     */
    public enum MappingSyntax {
        /**
         * N3 syntax.
         */
        N3, 
        /**
         * N-Triples syntax.
         */
        NTRIPLES, 
        /**
         * RDF/XML syntax.
         */
        RDFXML, 
        /**
         * TriG syntax.
         */
        TRIG, 
        /**
         * TriX syntax.
         */
        TRIX, 
        /**
         * Turtle syntax.
         */
        TURTLE
    };

    /**
     * Sets the file to read the RDF2TM mapping from.
     * 
     * @see #setMappingSource(URL)
     * @see #setMappingSource(String)
     *
     * @param file The file to read the RDF2TM mapping from.
     */
    public void setMappingSource(File file);

    /**
     * Sets the URL to read the RDF2TM mapping from.
     * 
     * @see #setMappingSource(File)
     * @see #setMappingSource(String)
     *
     * @param url The URL to read the RDF2TM mapping from.
     */
    public void setMappingSource(URL url);

    /**
     * Sets the URL to read the RDF2TM mapping from.
     * 
     * @see #setMappingSource(File)
     * @see #setMappingSource(URL)
     *
     * @param iri A string that represents an IRI / URL.
     */
    public void setMappingSource(String iri);

    /**
     * Returns the mapping source or <tt>null</tt> if no source is defined.
     *
     * @return A string representing an IRI or <tt>null</tt> if no source is defined.
     */
    public String getMappingSource();

    /**
     * Sets the syntax of the mapping source.
     * <p>
     * By default, the reader tries to detect the syntax by the file
     * extension.
     * </p>
     *
     * @param syntax The syntax of the mapping source or <tt>null</tt> to
     *                  let the reader autodetect the syntax.
     */
    public void setMappingSourceSyntax(MappingSyntax syntax);

    /**
     * Returns the mapping source syntax or <tt>null</tt> if the syntax
     * is not specified and will be autodetected.
     *
     * @return The mapping syntax or <tt>null</tt> if the syntax is 
     *          automatically detected.
     */
    public MappingSyntax getMappingSourceSyntax();

}
