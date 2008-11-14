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

import org.tinytim.internal.api.IConstant;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.internal.api.ILocator;
import org.tinytim.internal.utils.WeakObjectRegistry;
import org.tinytim.voc.XSD;
import static org.tinytim.core.value.LiteralNormalizer.normalizeBoolean;

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
public final class Literal implements ILiteral {

    private static final WeakObjectRegistry<LocatorImpl> _IRIS = new WeakObjectRegistry<LocatorImpl>(IConstant.LITERAL_IRI_SIZE);
    private static final WeakObjectRegistry<ILiteral> _STRINGS = new WeakObjectRegistry<ILiteral>(IConstant.LITERAL_STRING_SIZE);
    private static final WeakObjectRegistry<ILiteral> _OTHERS = new WeakObjectRegistry<ILiteral>(IConstant.LITERAL_OTHER_SIZE);

    private final String _value;
    private final Locator _datatype;

    private Literal(final String value, final Locator datatype) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        _value = LiteralNormalizer.normalize(value, datatype);
        _datatype = datatype;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getDatatype()
     */
    public Locator getDatatype() {
        return _datatype;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.ILiteral#getValue()
     */
    public String getValue() {
        return _value;
    }

    public static synchronized ILiteral get(String value) {
        return _STRINGS.get(new Literal(value, XSD.STRING));
    }

    public static synchronized ILiteral getIRI(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _IRIS.get(new LocatorImpl(value));
    }

    public static synchronized ILiteral get(String value, Locator datatype) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        if (datatype == null) {
            throw new IllegalArgumentException("The datatype must not be null");
        }
        if (XSD.ANY_URI.equals(datatype)) {
            return getIRI(value);
        }
        if (XSD.STRING.equals(datatype)) {
            return get(value);
        }
        if (XSD.BOOLEAN.equals(datatype)) {
            return normalizeBoolean(value).equals("true") ? BooleanLiteral.TRUE
                                                          : BooleanLiteral.FALSE;
        }
        return _OTHERS.get(new Literal(value, datatype));
        
    }

    private static synchronized <L extends ILiteral> L _registerIfAbsent(final WeakObjectRegistry<L> registry, final L lit) {
        final L existing = registry.get(lit);
        if (existing != null) {
            return existing;
        }
        registry.add(lit);
        return lit;
    }

    public static synchronized ILiteral create(final String value, final Locator datatype) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        if (datatype == null) {
            throw new IllegalArgumentException("The datatype must not be null");
        }
        if (XSD.ANY_URI.equals(datatype)) {
            return createIRI(value);
        }
        if (XSD.STRING.equals(datatype)) {
            return create(value);
        }
        if (XSD.DECIMAL.equals(datatype)) {
            return createDecimal(value);
        }
        if (XSD.INTEGER.equals(datatype)) {
            return createInteger(value);
        }
        if (XSD.BOOLEAN.equals(datatype)) {
            return createBoolean(value);
        }
        return _registerIfAbsent(_OTHERS, new Literal(value, datatype));
    }

    public static synchronized ILiteral create(String value) {
        return _registerIfAbsent(_STRINGS, new Literal(value, XSD.STRING));
    }

    public static ILiteral create(float value) {
        return create(value, XSD.FLOAT);
    }

    public static ILiteral create(long value) {
        return create(value, XSD.LONG);
    }

    public static ILiteral create(int value) {
        return create(value, XSD.INT);
    }

    private static ILiteral create(Number number, Locator datatype) {
        return create(number.toString(), datatype);
    }

    public static ILiteral create(String value, String datatype) {
        return create(value, Literal.createIRI(datatype));
    }

    public static ILiteral create(Locator value) {
        if (value == null) {
            throw new IllegalArgumentException("The locator must not be null");
        }
        return (ILocator) value;
    }

    public static synchronized ILocator createIRI(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _registerIfAbsent(_IRIS, new LocatorImpl(value));
    }

    public static synchronized ILiteral createDecimal(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _registerIfAbsent(_OTHERS, new DecimalLiteral(value));
    }

    public static synchronized ILiteral createDecimal(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _registerIfAbsent(_OTHERS, new DecimalLiteral(value));
    }

    public static synchronized ILiteral createInteger(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _registerIfAbsent(_OTHERS, new IntegerLiteral(value));
    }

    public static synchronized ILiteral createInteger(BigInteger value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _registerIfAbsent(_OTHERS, new IntegerLiteral(value));
    }

    public static ILiteral createBoolean(String value) {
        return normalizeBoolean(value).equals("true") ? BooleanLiteral.TRUE 
                                                      : BooleanLiteral.FALSE;
    }

    public BigDecimal decimalValue() {
        return new BigDecimal(_value);
    }

    public BigInteger integerValue() {
        return new BigInteger(_value);
    }

    public float floatValue() {
        return Float.valueOf(_value);
    }

    public int intValue() {
        return Integer.valueOf(_value);
    }

    public long longValue() {
        return Long.valueOf(_value);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _value.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Literal)) {
            return false;
        }
        Literal other = (Literal) obj;
        return this._value.equals(other._value) 
                    && this._datatype.equals(other._datatype);
    }

}
