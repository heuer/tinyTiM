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

import org.tinytim.internal.utils.Check;
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
        Check.valueNotNull(this, value);
        setLiteral(Literal.create(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigDecimal)
     */
    public void setValue(BigDecimal value) {
        Check.valueNotNull(this, value);
        setLiteral(Literal.createDecimal(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.DatatypeAware#setValue(java.math.BigInteger)
     */
    public void setValue(BigInteger value) {
        Check.valueNotNull(this, value);
        setLiteral(Literal.createInteger(value));
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
        Check.valueNotNull(this, value);
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
        Check.valueNotNull(this, value, datatype);
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
