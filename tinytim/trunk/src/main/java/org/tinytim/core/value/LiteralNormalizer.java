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

import org.tinytim.voc.XSD;

import org.tmapi.core.Locator;

/**
 * Normalizes literal values.
 * <p>
 * This class is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class LiteralNormalizer {

    private LiteralNormalizer() {
        // noop.
    }

    /**
     * Normalizes the <tt>value</tt> dependent on the <tt>datatype</tt>.
     *
     * @param value The value to normalize.
     * @param datatype The datatype.
     * @return A normalized value.
     */
    public static String normalize(final String value, final Locator datatype) {
        if (XSD.INTEGER.equals(datatype)) {
            return normalizeInteger(value);
        }
        else if (XSD.DECIMAL.equals(datatype)) {
            return normalizeDecimal(value);
        }
        else if (XSD.BOOLEAN.equals(datatype)) {
            return normalizeBoolean(value);
        }
        return value;
    }

    public static String normalizeBoolean(final String value) {
        if ("0".equals(value) || "false".equals(value)) {
            return "false";
        }
        else if ("1".equals(value) || "true".equals(value)) {
            return "true";
        }
        throw new IllegalArgumentException("Illegal boolean value: " + value);
    }

    public static String normalizeInteger(final String value) {
        final String val = value.trim();
        int len = value.length();
        if (len == 0) {
            throw new IllegalArgumentException();
        }
        int idx = 0;
        boolean negative = false;
        switch (val.charAt(idx)) {
            case '-':
                idx++;
                negative = true;
                break;
            case '+':
                idx++;
                break;
        }
        // Skip leading zeros if any
        while (idx < len && val.charAt(idx) == '0') {
            idx++;
        }
        if (idx == len) {
            return "0";
        }
        final String normalized = val.substring(idx);
        len = normalized.length();
        // Check if everything is a digit
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(normalized.charAt(i))) {
                throw new IllegalArgumentException();
            }
        }
        return negative && normalized.charAt(0) != 0 ? '-' + normalized : normalized;
    }

    public static String normalizeDecimal(final String value) {
        final String val = value.trim();
        int len = value.length();
        if (len == 0) {
            throw new IllegalArgumentException();
        }
        int idx = 0;
        boolean negative = false;
        switch (val.charAt(idx)) {
            case '-':
                idx++;
                negative = true;
                break;
            case '+':
                idx++;
                break;
        }
        // Skip leading zeros if any
        while (idx < len && val.charAt(idx) == '0') {
            idx++;
        }
        if (idx == len) {
            return "0.0";
        }
        StringBuilder normalized = new StringBuilder(len);
        if (val.charAt(idx) == '.') {
            normalized.append('0');
        }
        else {
            while (idx < len && val.charAt(idx) != '.') {
                char c = val.charAt(idx);
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("Illegal decimal value: " + value);
                }
                normalized.append(c);
                idx++;
            }
        }
        normalized.append('.');
        len--;
        while (len >= idx && val.charAt(len) == '0') {
            len--;
        }
        if (len == idx || len < idx) {
            normalized.append('0');
        }
        else {
            while (idx < len) {
                char c = val.charAt(idx);
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("Illegal decimal value: " + value);
                }
                normalized.append(c);
                idx++;
            }
        }
        return negative && normalized.charAt(0) != '0' ? '-' + normalized.toString() : normalized.toString();
    }

}
