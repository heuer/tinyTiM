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
 * Normalizes literal values.
 * 
 * This class is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
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
        if (XSD.BOOLEAN.equals(datatype)) {
            return normalizeBoolean(value);
        }
        else if (XSD.INTEGER.equals(datatype)) {
            return normalizeInteger(value);
        }
        else if (XSD.DECIMAL.equals(datatype)) {
            return normalizeDecimal(value);
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
