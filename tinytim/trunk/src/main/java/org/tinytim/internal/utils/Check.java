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
package org.tinytim.internal.utils;

import java.util.Collection;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

/**
 * Utility class to check arguments and to throw 
 * {@link org.tmapi.core.ModelConstraintException}s if the arg violates a 
 * constraint.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class Check {

    private static void _reportError(Construct sender, String msg) {
        throw new ModelConstraintException(sender, msg);
    }

    public static void scopeNotNull(Construct sender, Topic[] scope) {
        if (scope == null) {
            _reportError(sender, "The scope must not be null");
        }
    }

    public static void scopeNotNull(Construct sender, Collection<Topic> scope) {
        if (scope == null) {
            _reportError(sender, "The scope must not be null");
        }
    }

    public static void typeNotNull(Construct sender, Topic type) {
        if (type == null) {
            _reportError(sender, "The type must not be null");
        }
    }

    public static void valueNotNull(Construct sender, Object value) {
        if (value == null) {
            _reportError(sender, "The value must not be null");
        }
    }

    public static void valueNotNull(Construct sender, Object value, Locator datatype) {
        valueNotNull(sender, value);
        if (datatype == null) {
            _reportError(sender, "The datatype must not be null");
        }
    }

    public static void playerNotNull(Construct sender, Topic player) {
        if (player == null) {
            _reportError(sender, "The role player must not be null");
        }
    }

}
