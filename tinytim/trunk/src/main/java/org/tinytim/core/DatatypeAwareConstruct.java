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
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

/**
 * Implementation of {@link org.tmapi.core.DatatypeAware}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class DatatypeAwareConstruct extends ScopedImpl implements 
        DatatypeAware, ILiteralAware {

    private ILiteral _literal;

    DatatypeAwareConstruct(TopicMapImpl tm) {
        super(tm);
    }

    DatatypeAwareConstruct(TopicMapImpl topicMap, Topic type, ILiteral literal, IScope scope) {
        super(topicMap, type, scope);
        _literal = literal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteralAware#getLiteral()
     */
    public ILiteral getLiteral() {
        return _literal;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.ILiteralAware#setLiteral(org.tinytim.core.ILiteral)
     */
    public void setLiteral(ILiteral literal) {
        assert literal != null;
        _fireEvent(Event.SET_LITERAL, _literal, literal);
        _literal = literal;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#getValue()
     */
    public String getValue() {
        return _literal.getValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#getDatatype()
     */
    public Locator getDatatype() {
        return _literal.getDatatype();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String)
     */
    public void setValue(String value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigDecimal)
     */
    public void setValue(BigDecimal value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigInteger)
     */
    public void setValue(BigInteger value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(float)
     */
    public void setValue(float value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(int)
     */
    public void setValue(int value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(org.tmapi.core.Locator)
     */
    public void setValue(Locator value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(long)
     */
    public void setValue(long value) {
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String, org.tmapi.core.Locator)
     */
    public void setValue(String value, Locator datatype) {
        setLiteral(Literal.create(value, datatype));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#decimalValue()
     */
    public BigDecimal decimalValue() {
        return _literal.decimalValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#floatValue()
     */
    public float floatValue() {
        return _literal.floatValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#integerValue()
     */
    public BigInteger integerValue() {
        return _literal.integerValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#intValue()
     */
    public int intValue() {
        return _literal.intValue();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#locatorValue()
     */
    public Locator locatorValue() {
        return XSD.ANY_URI == _literal.getDatatype() ? (Locator) _literal
                                    : Literal.createIRI(_literal.getValue());
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#longValue()
     */
    public long longValue() {
        return _literal.longValue();
    }

    /* (non-Javadoc)
     * @see org.tinytim.Construct#dispose()
     */
    @Override
    protected void dispose() {
        _literal = null;
        super.dispose();
    }
}
