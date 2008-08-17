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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Simple SAX-alike XML writer that respects canonical XML to some extend.
 * 
 * This class is not meant to be a generic XML-C14N writer, but it should be
 * good enough to support CXTM.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class XMLC14NWriter {

    public static final Attributes EMPTY_ATTRS = new AttributesImpl();

    private static final char _NL = '\n';

    private OutputStreamWriter _out;

    public XMLC14NWriter(OutputStream out) throws IOException {
        _out = new OutputStreamWriter(out, "UTF-8");
    }

    /**
     * Indicates the start of the serialization process.
     *
     * @throws IOException If an error occurs.
     */
    public void startDocument() throws IOException {
        // noop
    }

    /**
     * Indicates the end of the serialization process.
     *
     * @throws IOException If an error occurs.
     */
    public void endDocument() throws IOException {
        _out.flush();
    }

    /**
     * Indicates the start of an element with the specified local name.
     *
     * @see #startElement(String, Attributes).
     * 
     * @param localName The element's name.
     * @throws IOException If an error occurs.
     */
    public void startElement(String localName) throws IOException {
        startElement(localName, EMPTY_ATTRS);
    }

    /**
     * Indicates the start of an element with the provided local name.
     * 
     * The attributes written in canonical XML.
     *
     * @param localName The name of the element.
     * @param attrs The element's attributes.
     * @throws IOException If an error occurs.
     */
    public void startElement(String localName, Attributes attrs) throws IOException {
        String[] names = new String[attrs.getLength()];
        for (int i=0; i < names.length; i++) {
            names[i] = attrs.getLocalName(i);
        }
        Arrays.sort(names);
        _out.write('<');
        _out.write(localName);
        for (int i=0; i < names.length; i++) {
            _out.write(' ');
            _out.write(names[i]);
            _out.write("=\"");
            _writeEscapedAttributeValue(attrs.getValue("", names[i]));
            _out.write('"');
        }
        _out.write('>');
    }

    /**
     * Indicates the end of an elemnt.
     *
     * @param localName The element's name.
     * @throws IOException If an error occurs.
     */
    public void endElement(String localName) throws IOException {
        _out.write("</");
        _out.write(localName);
        _out.write('>');
    }

    /**
     * Writes a <tt>#x0A</tt> to the output.
     *
     * @throws IOException If an error occurs.
     */
    public void newline() throws IOException {
        _out.write(_NL);
    }

    /**
     * Writes the specified characters to the output.
     * 
     * The data is written according to the rules of canonicalized XML.
     *
     * @param data The data to write.
     * @throws IOException If an error occurs.
     */
    public void characters(String data) throws IOException {
        _writeEscapedTextContent(data);
    }

    /**
     * Escapes the data according to the canonical XML rules.
     *
     * @param value The value.
     * @return The escaped value.
     * @throws IOException 
     */
    private void _writeEscapedTextContent(String value) throws IOException {
        char[] ch = value.toCharArray();
        for (int i=0; i < ch.length; i++) {
            switch(ch[i]) {
                case '\r':
                    _out.write("&#xD;");
                    break;
                case '&':
                    _out.write("&amp;");
                    break;
                case '<':
                    _out.write("&lt;");
                    break;
                case '>':
                    _out.write("&gt;");
                    break;
                default:
                    _out.write(ch[i]);
            }
        }
    }

    /**
     * Escapes the attribute's value according to canonical XML.
     *
     * @param value The value to escape.
     * @return The escaped value.
     * @throws IOException 
     */
    private void _writeEscapedAttributeValue(String value) throws IOException {
        char[] ch = value.toCharArray();
        for (int i=0; i<ch.length; i++) {
            switch(ch[i]) {
            case '\t':
                _out.write("&#x9;");
                break;
            case '\n':
                _out.write("&#xA;");
                break;
            case '\r':
                _out.write("&#xD;");
                break;
            case '\"':
                _out.write("&quot;");
                break;
            case '&':
                _out.write("&amp;");
                break;
            case '<':
                _out.write("&lt;");
                break;
            default:
                _out.write(ch[i]);
            }
        }
    }
}
