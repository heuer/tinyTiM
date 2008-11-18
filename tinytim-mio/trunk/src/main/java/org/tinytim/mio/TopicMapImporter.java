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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.tmapi.core.TopicMap;
import org.xml.sax.InputSource;

import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * Functions to import serialized topic maps.
 * <p>
 * This class is kept for backward compatibility, some methods are already
 * deprectated, maybe the whole class will be deprecated in the near future; 
 * use {@link TopicMapReader} and its implementations. Actually, this class has 
 * become a wrapper around different {@link TopicMapReader} implementations.
 * </p>
 * <p>
 * This class may be deprected since it provides a high-level view on 
 * {@link TopicMapReader}s. A {@link TopicMapReader} instance may provide
 * methods to configure its behaviour while this class does not support any
 * configuration.
 * </p>
 * <p>
 * Applications which use this class should possibly implement something 
 * equivalent or a smarter utility class since this class will never support
 * any configuration of {@link TopicMapReader}s.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TopicMapImporter {

    private TopicMapImporter() {
        // noop.
    }

    /**
     * Reads a XML topic map from <tt>input</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     *
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param input The stream to read the serialized topic map from.
     * @throws IOException If an error occurs.
     */
    public static void importInto(TopicMap topicMap, String docIRI, InputStream input) throws IOException {
        _import(Syntax.XTM, topicMap, docIRI, input);
    }

    /**
     * Reads a topic map from <tt>file</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     * 
     * The syntax of the serialized topic map is guessed by the file name. If
     * the file extension gives no indication of the used syntax, XTM is 
     * assumed.
     *
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param file The file to read the serialized topic map from.
     * @throws IOException If an error occurs. 
     */
    public static void importInto(TopicMap topicMap, String docIRI, File file) throws IOException {
        _import(_guessSyntax(file), topicMap, docIRI, new FileInputStream(file));
    }

    /**
     * Reads a topic map from <tt>file</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     * 
     * The <tt>syntax</tt> is a string with the abbreviated Topic Maps syntax
     * name; i.e. "xtm", "ltm", "ctm". The name is matched case-insensitve, that
     * means "xtm" is the same as "xTm", "XTM" etc.
     *
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param file The file to read the serialized topic map from.
     * @param syntax The name of the syntax of the encoded topic map. I.e. "xtm".
     * @throws IOException If an error occurs.
     */
    public static void importInto(TopicMap topicMap, String docIRI, File file, String syntax) throws IOException {
        importInto(topicMap, docIRI, new FileInputStream(file), syntax);
    }

    /**
     * Reads a topic map from <tt>input</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     * 
     * The <tt>syntax</tt> is a string with the abbreviated Topic Maps syntax
     * name; i.e. "xtm", "ltm", "ctm". The name is matched case-insensitve, that
     * means "xtm" is the same as "xTm", "XTM" etc.
     *
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param input The stream to read the serialized topic map from.
     * @param syntax The name of the syntax of the encoded topic map. I.e. "xtm".
     * @throws IOException If an error occurs.
     */
    public static void importInto(TopicMap topicMap, String docIRI, InputStream input, String syntax) throws IOException {
      Syntax syntax_ = Syntax.valueOf(syntax);
      if (syntax_ == null) {
          throw new RuntimeException("The syntax '" + syntax + "' is unknown");
      }
        _import(syntax_, topicMap, docIRI, input);
    }

    /**
     * Returns a {@link Syntax} instance.
     *
     * @param file The file to guess the syntax from.
     * @return A syntax which matches the file extension or {@link Syntax#XTM}
     *          if the file extension is not available or gives no indication
     *          about the used syntax.
     */
    private static Syntax _guessSyntax(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return i == -1 ? Syntax.XTM 
                       : Syntax.forFileExtension(name.substring(i+1), Syntax.XTM);
    }

    /**
     * Reads a topic map from <tt>input</tt> and adds the content to the
     * <tt>topicMap</tt>.
     *
     * @param syntax A syntax instance.
     * @param topicMap A topic map instance.
     * @param docIRI The IRI which is used to resolve locators against.
     * @param input The source to read the topic map from.
     * @throws IOException If an error occurs.
     */
    private static void _import(Syntax syntax, TopicMap topicMap, String docIRI, 
            InputStream input) throws IOException {
        _import(syntax, topicMap, new Source(input, docIRI));
    }

    /**
     * Reads a topic map from <tt>input</tt> and adds the content to the
     * <tt>topicMap</tt>.
     *
     * @param syntax A syntax instance.
     * @param topicMap A topic map instance.
     * @param docIRI The IRI which is used to resolve locators against.
     * @param input The source to read the topic map from.
     * @throws IOException If an error occurs.
     */
    private static void _import(Syntax syntax, TopicMap topicMap, Source input) throws IOException {
        TopicMapReader tmReader = null;
        if (Syntax.XTM.equals(syntax)) {
           tmReader = new XTMTopicMapReader(topicMap, input);
        }
        else if (Syntax.CTM.equals(syntax)) {
            tmReader = new CTMTopicMapReader(topicMap, input);
        }
        else if (Syntax.LTM.equals(syntax)) {
            tmReader = new LTMTopicMapReader(topicMap, input);
        }
        else if (Syntax.TMXML.equals(syntax)) {
            tmReader = new TMXMLTopicMapReader(topicMap, input);
        }
        else if (Syntax.SNELLO.equals(syntax)) {
            tmReader = new SnelloTopicMapReader(topicMap, input);
        }
        if (tmReader == null) {
            throw new IOException("Unknown syntax " + syntax.getName());
        }
        tmReader.read();
    }

    /**
     * Reads a XML topic map from <tt>input</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     *
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param input The source to read the serialized topic map from.
     * @throws IOException If an error occurs.
     */
    @Deprecated
    public static void importInto(TopicMap topicMap, String docIRI, InputSource input) throws IOException {
        _import(Syntax.XTM, topicMap, docIRI, input);
    }

    /**
     * Reads a topic map from <tt>input</tt> and adds the content to the 
     * specified <tt>topicMap</tt>. The <tt>docIRI</tt> is used to
     * resolve IRIs against.
     * <p>
     * The <tt>syntax</tt> is a string with the abbreviated Topic Maps syntax
     * name; i.e. "xtm", "ltm", "ctm". The name is matched case-insensitve, that
     * means "xtm" is the same as "xTm", "XTM" etc.
     * </p>
     * 
     * @param topicMap The topic map instance which receives the 
     *                  Topic Maps constructs.
     * @param docIRI The IRI which is used to resolve IRIs against.
     * @param input The source to read the serialized topic map from.
     * @param syntax The name of the syntax of the encoded topic map. I.e. "xtm".
     * @throws IOException If an error occurs.
     */
    @Deprecated
    public static void importInto(TopicMap topicMap, String docIRI, InputSource input, String syntax) throws IOException {
        Syntax syntax_ = Syntax.valueOf(syntax);
        if (syntax_ == null) {
            throw new RuntimeException("The syntax '" + syntax + "' is unknown");
        }
        _import(syntax_, topicMap, docIRI, input);
    }

    /**
     * Reads a topic map from <tt>input</tt> and adds the content to the
     * <tt>topicMap</tt>.
     *
     * @param syntax A syntax instance.
     * @param topicMap A topic map instance.
     * @param docIRI The IRI which is used to resolve locators against.
     * @param input The source to read the topic map from.
     * @throws IOException If an error occurs.
     */
    @Deprecated
    private static void _import(Syntax syntax, TopicMap topicMap,
            String docIRI, InputSource input) throws IOException {
        Source src = null;
        if (input.getByteStream() != null) {
            src = new Source(input.getByteStream(), docIRI, input.getEncoding());
        }
        else {
            Reader reader = input.getCharacterStream();
            if (reader != null) {
                src = new Source(reader, docIRI, input.getEncoding());
            }
            else {
                src = new Source(input.getSystemId());
            }
        }
        _import(syntax, topicMap, src);
    }

}
