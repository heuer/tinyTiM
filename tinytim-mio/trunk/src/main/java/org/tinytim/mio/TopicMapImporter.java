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
import com.semagia.mio.Property;
import com.semagia.mio.Syntax;

/**
 * Functions to import serialized topic maps.
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
    public static void importInto(TopicMap topicMap, String docIRI, InputSource input) throws IOException {
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
        importInto(topicMap, docIRI, new InputSource(input), syntax);
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
     * @param input The source to read the serialized topic map from.
     * @param syntax The name of the syntax of the encoded topic map. I.e. "xtm".
     * @throws IOException If an error occurs.
     */
    public static void importInto(TopicMap topicMap, String docIRI, InputSource input, String syntax) throws IOException {
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
        _import(syntax, topicMap, docIRI, new InputSource(input));
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
            InputSource input) throws IOException {
        IDeserializer deser = DeserializerRegistry.createDeserializer(syntax);
        if (deser == null) {
            throw new IOException("No deserializer found for the syntax '" + syntax.getName() + "'");
        }
        deser.setProperty(Property.VALIDATE, "false");
        deser.setMapHandler(new MapInputHandler(topicMap));
        try {
            deser.parse(input, docIRI);
        }
        catch (MIOException ex) {
            if (ex.getException() instanceof IOException) {
                throw (IOException) ex.getException();
            }
            else {
                ex.printStackTrace();
                throw new TMAPIRuntimeException(ex);
            }
        }
    }
}
