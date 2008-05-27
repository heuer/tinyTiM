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
package org.tinytim;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;

/**
 * Immutable representation of an IRI.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class IRI implements Locator {

    private final URI _uri;
    private final String _reference;

    public IRI(String reference) {
        try {
            _reference = URLDecoder.decode(reference, "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TMAPIRuntimeException(ex);
        }
        _uri = URI.create(_reference.replace(" ", "%20"));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#getNotation()
     */
    public String getNotation() {
        return "URI";
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#getReference()
     */
    public String getReference() {
        return _reference;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Locator#resolveRelative(java.lang.String)
     */
    public Locator resolveRelative(String reference) {
        return new IRI(_uri.resolve(reference).toString());
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
        return this == obj || (obj instanceof IRI && _uri.equals(((IRI) obj)._uri));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _uri.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _uri.toString();
    }

}
