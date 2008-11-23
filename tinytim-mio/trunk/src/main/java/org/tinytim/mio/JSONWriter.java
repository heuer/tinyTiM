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
import java.io.OutputStreamWriter;

/**
 * Simple JSON serializer. This class is not usable as a generic JSON writer 
 * since it is possible to create an invalid JSON representation, but
 * it is good enough to support JTM.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class JSONWriter {

    private OutputStreamWriter _out;
    private boolean _wantComma;
    private int _depth;
    private boolean _prettify;

    public JSONWriter(OutputStream out) throws IOException {
        _out = new OutputStreamWriter(out, "utf-8");
    }

    public void setPrettify(boolean prettify) {
        _prettify = prettify;
    }

    public boolean getPrettify() {
        return _prettify;
    }

    public void startDocument() {
        _depth = 0;
    }

    public void endDocument() throws IOException {
        _out.write('\n');
        _out.flush();
    }

    private void _indent() throws IOException {
        if (!_prettify) {
            return;
        }
        if (_depth > 0) {
            _out.write('\n');
        }
        int indent = _depth*2;
        char[] chars = new char[indent];
        for (int i=0; i<indent; i++) {
            chars[i] = ' ';
        }
        _out.write(chars);
    }

    public void startObject() throws IOException {
        if (_wantComma) {
            _out.write(',');
        }
        _indent();
        _out.write('{');
        _depth++;
        _wantComma = false;
    }

    public void endObject() throws IOException {
        _out.write('}');
        _depth--;
        _wantComma = true;
    }

    public void startArray() throws IOException {
        _out.write('[');
        _depth++;
        _wantComma = false;
    }

    public void endArray() throws IOException {
        _out.write(']');
        _depth--;
        _wantComma = true;
    }

    /**
     * Writes the key of a <tt>"key": value</tt> pair. 
     * The writer assumes that the key is a valid JSON string (ensured by 
     * by JTM) so the keys are not escaped!
     *
     * @param key
     * @throws IOException
     */
    public void key(String key)  throws IOException {
        if (_wantComma) {
            _out.write(',');
            _indent();
        }
        _out.write('"');
        _out.write(key);
        _out.write('"');
        _out.write(':');
        _wantComma = false;
    }

    public void value(String value) throws IOException {
        if (_wantComma) {
            _out.write(',');
        }
        _out.write(escape(value));
        _wantComma = true;
    }

    /**
     * 
     *
     * @param value
     * @return
     */
    public static String escape(String value) {
        // Code adapted from JSON.org (JSONObject.quote(String))
        // Copyrighted by JSON.org licensed under a BSD-license, see
        // complete copyright notice at the end of this file.
        char b;
        char c = 0;
        char[] chars = value.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length + 4);
        String t;
        sb.append('"');
        for (int i = 0; i < chars.length; i += 1) {
            b = c;
            c = chars[i];
            switch (chars[i]) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /*
    ===========================================================================
    Copyright of the JSONObject code which was used to implement the "escape"
    function.
    ===========================================================================

    Copyright (c) 2002 JSON.org

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    The Software shall be used for Good, not Evil.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
    */
}
