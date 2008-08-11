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
package org.tinytim.cxtm;

import org.tinytim.core.ILiteral;
import org.tinytim.core.ILiteralAware;
import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class LiteralCanonicalizer {

    private LiteralCanonicalizer () {
        // noop.
    }

    public static String canonicalize(final ILiteralAware obj) {
        final ILiteral lit = obj.getLiteral();
        final String value = lit.getValue();
        final Locator datatype = lit.getDatatype();
        if (datatype == XSD.ANY_URI || datatype == XSD.STRING) {
            return lit.getValue();
        }
        else if (datatype == XSD.DECIMAL || datatype == XSD.INT || datatype == XSD.INTEGER) {
            if (value.charAt(0) == '+') {
                return value.substring(1);
            }
            return value;
        }
        return lit.getValue();
    }

    
}
