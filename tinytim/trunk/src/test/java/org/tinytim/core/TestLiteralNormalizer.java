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

/**
 * Tests against the {@link LiteralNormalizer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestLiteralNormalizer extends TinyTimTestCase {

    public void testNormalizeBoolean() {
        assertEquals("true", LiteralNormalizer.normalizeBoolean("1"));
        assertEquals("true", LiteralNormalizer.normalizeBoolean("true"));
        assertEquals("false", LiteralNormalizer.normalizeBoolean("0"));
        assertEquals("false", LiteralNormalizer.normalizeBoolean("false"));
        try {
            LiteralNormalizer.normalizeBoolean("invalid");
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testNormalizeInteger() {
        assertEquals("0", LiteralNormalizer.normalizeInteger("0"));
        assertEquals("0", LiteralNormalizer.normalizeInteger("-0"));
        assertEquals("1", LiteralNormalizer.normalizeInteger("+1"));
        assertEquals("1", LiteralNormalizer.normalizeInteger("00001"));
        assertEquals("-1", LiteralNormalizer.normalizeInteger("-1"));
        assertEquals("-1", LiteralNormalizer.normalizeInteger("-00001"));
        try {
            LiteralNormalizer.normalizeInteger("invalid");
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testNormalizeDecimal() {
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("0"));
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("-0"));
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("-0.0"));
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("+0.0"));
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("+00000.0000000"));
        assertEquals("0.0", LiteralNormalizer.normalizeDecimal("-00000.0000000"));
        assertEquals("10.0", LiteralNormalizer.normalizeDecimal("10"));
        assertEquals("-10.0", LiteralNormalizer.normalizeDecimal("-10.00"));
        assertEquals("10.0", LiteralNormalizer.normalizeDecimal("+10.00"));
        try {
            LiteralNormalizer.normalizeDecimal("invalid");
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }
}
