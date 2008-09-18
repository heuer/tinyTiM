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
package org.tinytim.core;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLDecoder;

import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;

/**
 * Immutable representation of an IRI.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class IRI implements ILocator {

    private final URI _uri;
    private final String _reference;

    IRI(String reference) {
        try {
            _reference = URLDecoder.decode(reference, "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        _uri = URI.create(_reference.replace(" ", "%20"));
    }

    private IRI(URI uri) {
        try {
            _reference = URLDecoder.decode(uri.toString(), "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        _uri = uri;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#getDatatype()
     */
    public Locator getDatatype() {
        return XSD.ANY_URI;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#getValue()
     */
    public String getValue() {
        return _reference;
    }

    public BigDecimal decimalValue() {
        throw new NumberFormatException();
    }

    public float floatValue() {
        throw new NumberFormatException();
    }

    public BigInteger integerValue() {
        throw new NumberFormatException();
    }

    public int intValue() {
        throw new NumberFormatException();
    }

    public long longValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#getReference()
     */
    public String getReference() {
        return _reference;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#resolve(java.lang.String)
     */
    public Locator resolve(String reference) {
        return new IRI(_uri.resolve(reference));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#toExternalForm()
     */
    public String toExternalForm() {
        return _uri.toASCIIString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof IRI && _reference.equals(((IRI) obj)._reference));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _reference.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _uri.toString();
    }

}
