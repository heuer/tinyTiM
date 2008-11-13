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
package org.tinytim.core.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.tinytim.internal.api.ILiteral;
import org.tinytim.voc.XSD;

import org.tmapi.core.Locator;

/**
 * 
 * <p>
 * This class is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
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
