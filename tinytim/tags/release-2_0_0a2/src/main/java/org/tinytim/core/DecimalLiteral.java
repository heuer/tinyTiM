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
package org.tinytim.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class DecimalLiteral implements ILiteral {

    private final BigDecimal _decimal;
    private final String _lexicalForm;

    public DecimalLiteral(BigDecimal decimal) {
        _decimal = decimal;
        _lexicalForm = LiteralNormalizer.normalizeDecimal(decimal.toPlainString());
    }

    public DecimalLiteral(String value) {
        _lexicalForm = LiteralNormalizer.normalizeDecimal(value);
        _decimal = new BigDecimal(_lexicalForm);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#decimalValue()
     */
    public BigDecimal decimalValue() {
        return _decimal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#floatValue()
     */
    public float floatValue() {
        return _decimal.floatValue();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#getDatatype()
     */
    public Locator getDatatype() {
        return XSD.DECIMAL;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#getValue()
     */
    public String getValue() {
        return _lexicalForm;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#intValue()
     */
    public int intValue() {
        return _decimal.intValue();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#integerValue()
     */
    public BigInteger integerValue() {
        return _decimal.toBigInteger();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#longValue()
     */
    public long longValue() {
        return _decimal.longValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _decimal.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DecimalLiteral) && ((DecimalLiteral) obj)._lexicalForm.equals(_lexicalForm);
    }

}
