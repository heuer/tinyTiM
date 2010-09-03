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
package org.tinytim.core.value;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLDecoder;

import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.ILocator;
import org.tinytim.internal.utils.WeakObjectRegistry;
import org.tinytim.voc.XSD;

import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;

/**
 * Immutable representation of an IRI.
 * 
 * <p>
 * This class is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class LocatorImpl implements ILocator {

    private static final String _EMPTY = "";
    private static final WeakObjectRegistry<ILocator> _IRIS = new WeakObjectRegistry<ILocator>(IConstant.LITERAL_IRI_SIZE);
    private final URI _uri;
    private final String _reference;

    private LocatorImpl(String reference) {
        if (_EMPTY.equals(reference) || reference.charAt(0) == '#') {
            throw new MalformedIRIException("Illegal absolute IRI: '" + reference + "'");
        }
        try {
            _reference = URLDecoder.decode(reference, "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        _uri = URI.create(_reference.replace(" ", "%20"));
    }

    private LocatorImpl(URI uri) {
        try {
            _reference = URLDecoder.decode(uri.toString(), "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        _uri = uri;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getDatatype()
     */
    @Override
    public Locator getDatatype() {
        return XSD.ANY_URI;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getValue()
     */
    @Override
    public String getValue() {
        return _reference;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#decimalValue()
     */
    @Override
    public BigDecimal decimalValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#floatValue()
     */
    @Override
    public float floatValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#integerValue()
     */
    @Override
    public BigInteger integerValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#intValue()
     */
    @Override
    public int intValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#longValue()
     */
    @Override
    public long longValue() {
        throw new NumberFormatException();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#getReference()
     */
    @Override
    public String getReference() {
        return _reference;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#resolve(java.lang.String)
     */
    @Override
    public Locator resolve(String reference) {
        if (_EMPTY.equals(reference)) {
            return this;
        }
        return create(_uri.resolve(reference.replace(" ", "%20")));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#toExternalForm()
     */
    @Override
    public String toExternalForm() {
        return _uri.toASCIIString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Locator && _reference.equals(((Locator) obj).getReference()));
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

    static synchronized ILiteral get(String value) {
        return _IRIS.get(new LocatorImpl(value));
    }

    private static synchronized ILocator create(URI value) {
        ILocator loc = new LocatorImpl(value);
        final ILocator existing = _IRIS.get(loc);
        if (existing != null) {
            return existing;
        }
        _IRIS.add(loc);
        return loc;
    }

    static synchronized ILocator create(String value) {
        ILocator loc = new LocatorImpl(value);
        final ILocator existing = _IRIS.get(loc);
        if (existing != null) {
            return existing;
        }
        _IRIS.add(loc);
        return loc;
    }

}
