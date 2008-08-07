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

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * 
 * 
 * This class is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class Literal implements ILiteral {

    private static final WeakObjectRegistry<IRI> _IRIS = new WeakObjectRegistry<IRI>();
    private static final WeakObjectRegistry<ILiteral> _STRINGS = new WeakObjectRegistry<ILiteral>();
    private static final WeakObjectRegistry<ILiteral> _LITERALS = new WeakObjectRegistry<ILiteral>();;

    private final String _value;
    private final Locator _datatype;

    private Literal(final String value, final Locator datatype) {
        _value = value;
        _datatype = datatype;
    }

    public Locator getDatatype() {
        return _datatype;
    }

    public String getValue() {
        return _value;
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
        ILiteral literal = new Literal(value, datatype);
        ILiteral existing = _LITERALS.get(literal);
        if (existing != null) {
            return existing;
        }
        _LITERALS.add(literal);
        return literal;
    }

    public static synchronized ILiteral create(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        ILiteral literal = new Literal(value, XSD.STRING);
        ILiteral existing = _STRINGS.get(literal);
        if (existing != null) {
            return existing;
        }
        _STRINGS.add(literal);
        return literal;
    }

    public static ILiteral create(BigDecimal value) {
        return create(value, XSD.DECIMAL);
    }

    public static ILiteral create(BigInteger value) {
        return create(value, XSD.INTEGER);
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
        IRI iri = new IRI(value);
        IRI existing = _IRIS.get(iri);
        if (existing != null) {
            return existing;
        }
        _IRIS.add(iri);
        return iri;
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

    @Override
    public int hashCode() {
        return _value.hashCode();
    }

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


    private static class WeakObjectRegistry<E> extends AbstractSet<E> {

        private final Map<E, WeakReference<E>> _obj2Ref;

        public WeakObjectRegistry() {
            super();
            _obj2Ref = new WeakHashMap<E, WeakReference<E>>();
        }

        /**
         * 
         *
         * @param key
         * @return
         */
        public E get(Object key) {
            WeakReference<E> weakRef = _obj2Ref.get(key);
            return weakRef != null ? weakRef.get() : null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#add(java.lang.Object)
         */
        @Override
        public boolean add(E obj) {
            WeakReference<E> ref = new WeakReference<E>(obj);
            ref = _obj2Ref.put(obj, ref);
            return ref != null && ref.get() != null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object obj) {
            WeakReference<E> ref = _obj2Ref.remove(obj);
            return ref != null && ref.get() != null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#clear()
         */
        @Override
        public void clear() {
            _obj2Ref.clear();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(Object obj) {
            return get(obj) != null;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<E> iterator() {
            return _obj2Ref.keySet().iterator();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return _obj2Ref.size();
        }
    }

}
