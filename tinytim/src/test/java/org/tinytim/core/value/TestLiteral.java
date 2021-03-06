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

import org.tinytim.core.AbstractTinyTimTestCase;
import org.tinytim.internal.api.ILiteral;
import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * Tests against {@link Literal}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestLiteral extends AbstractTinyTimTestCase {

    public void testLocatorResolve() {
        Locator base = Literal.createIRI("http://www.semagia.com/");
        Locator loc1 = base.resolve("#iid");
        Locator loc2 = base.resolve("#iid");
        assertSame(loc1, loc2);
    }

    public void testStringGet() {
        final String value = "tiny tiny tiny";
        ILiteral lit = Literal.get(value);
        assertNull(lit);
        lit = Literal.create(value);
        assertNotNull(lit);
        assertSame(lit, Literal.get(value));
    }

    public void testStringEquality() {
        final String value = "__tinyTiM__";
        assertNull(Literal.get(value));
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }

    public void testStringEquality2() {
        final String value = "tinyTiM!";
        assertNull(Literal.get(value));
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value, XSD.STRING);
        assertSame(lit1, lit2);
    }

    public void testStringEquality3() {
        final String value = "tinyTiM?";
        assertNull(Literal.get(value));
        ILiteral lit1 = Literal.create(value, XSD.STRING);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality() {
        final String value = "http://www.semagia.com/test-iri-eq";
        assertNull(Literal.get(value, XSD.ANY_URI));
        final Locator loc = _sys.createLocator(value);
        ILiteral lit1 = Literal.create(loc);
        ILiteral lit2 = Literal.create(loc);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality2() {
        final String value = "http://www.semagia.net/test-iri-eq2";
        assertNull(Literal.get(value, XSD.ANY_URI));
        final Locator loc = _sys.createLocator(value);
        ILiteral lit1 = Literal.create(loc);
        ILiteral lit2 = Literal.create(value, XSD.ANY_URI);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality3() {
        final String value = "http://www.semagia.de/test-iri-eq3";
        assertNull(Literal.get(value, XSD.ANY_URI));
        final Locator loc = _sys.createLocator(value);
        ILiteral lit1 = Literal.create(value, XSD.ANY_URI);
        ILiteral lit2 = Literal.create(loc);
        assertSame(lit1, lit2);
    }

    public void testBooleanEquality() {
        assertSame(BooleanLiteral.TRUE, Literal.get("true", XSD.BOOLEAN));
        assertSame(BooleanLiteral.TRUE, Literal.get("1", XSD.BOOLEAN));
        assertSame(BooleanLiteral.FALSE, Literal.get("false", XSD.BOOLEAN));
        assertSame(BooleanLiteral.FALSE, Literal.get("0", XSD.BOOLEAN));
        assertSame(BooleanLiteral.TRUE, Literal.create("1", XSD.BOOLEAN));
        assertSame(BooleanLiteral.FALSE, Literal.create("0", XSD.BOOLEAN));
    }
}
