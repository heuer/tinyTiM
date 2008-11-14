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

import org.tinytim.core.TinyTimTestCase;

/**
 * Tests against the {@link LiteralNormalizer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
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
