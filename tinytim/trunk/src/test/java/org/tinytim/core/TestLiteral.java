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

import org.tinytim.voc.XSD;
import org.tmapi.core.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestLiteral extends TinyTimTestCase {

    public void testStringEquality() {
        final String value = "tinyTiM";
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }

    public void testStringEquality2() {
        final String value = "tinyTiM";
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value, XSD.STRING);
        assertSame(lit1, lit2);
    }

    public void testStringEquality3() {
        final String value = "tinyTiM";
        ILiteral lit1 = Literal.create(value, XSD.STRING);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality() {
        final Locator value = _sys.createLocator("http://www.semagia.com/");
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality2() {
        final Locator value = _sys.createLocator("http://www.semagia.com/");
        ILiteral lit1 = Literal.create(value);
        ILiteral lit2 = Literal.create(value.getReference(), XSD.ANY_URI);
        assertSame(lit1, lit2);
    }

    public void testIRIEquality3() {
        final Locator value = _sys.createLocator("http://www.semagia.com/");
        ILiteral lit1 = Literal.create(value.getReference(), XSD.ANY_URI);
        ILiteral lit2 = Literal.create(value);
        assertSame(lit1, lit2);
    }
}
