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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class IntegerLiteral implements ILiteral {

    private final BigInteger _integer;
    private final String _lexicalForm;
    private BigDecimal _decimal;

    public IntegerLiteral(BigInteger value) {
        _integer = value;
        _lexicalForm = LiteralNormalizer.normalizeInteger(value.toString());
    }

    public IntegerLiteral(String value) {
        _lexicalForm = LiteralNormalizer.normalizeInteger(value);
        _integer = new BigInteger(_lexicalForm);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#decimalValue()
     */
    public BigDecimal decimalValue() {
        if (_decimal == null) {
            _decimal = new BigDecimal(_integer);
        }
        return _decimal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#floatValue()
     */
    public float floatValue() {
        return _integer.floatValue();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#getDatatype()
     */
    public Locator getDatatype() {
        return XSD.INTEGER;
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
        return _integer.intValue();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#integerValue()
     */
    public BigInteger integerValue() {
        return _integer;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteral#longValue()
     */
    public long longValue() {
        return _integer.longValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _integer.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof IntegerLiteral) && ((IntegerLiteral) obj)._lexicalForm.equals(_lexicalForm);
    }

}
