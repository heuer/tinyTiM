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
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class BooleanLiteral implements ILiteral {

    public static final ILiteral TRUE = new BooleanLiteral(true);
    public static final ILiteral FALSE = new BooleanLiteral(false);

    private final boolean _value;

    private BooleanLiteral(boolean value) {
        _value = value;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#decimalValue()
     */
    public BigDecimal decimalValue() {
        return _value == true ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#floatValue()
     */
    public float floatValue() {
        return _value == true ? 1.0F : 0.0F;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getDatatype()
     */
    public Locator getDatatype() {
        return XSD.BOOLEAN;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getValue()
     */
    public String getValue() {
        return _value == true ? "true" : "false";
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#intValue()
     */
    public int intValue() {
        return _value == true ? 1 : 0;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#integerValue()
     */
    public BigInteger integerValue() {
        return _value == true ? BigInteger.ONE : BigInteger.ZERO;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#longValue()
     */
    public long longValue() {
        return intValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof BooleanLiteral) && _value == ((BooleanLiteral) obj)._value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _value == true ? 1 : 0;
    }

}
