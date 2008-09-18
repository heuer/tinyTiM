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
package org.tinytim.internal.utils;

import java.util.Collection;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

/**
 * Provides various argument constraint checks.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class Check {

    private Check() {
        // noop.
    }

    /**
     * Throws a {@link ModelConstraintException} with the specified <tt>sender</tt>
     * and <tt>msg</tt>
     *
     * @param sender The sender
     * @param msg The error message
     */
    private static void _reportError(Construct sender, String msg) {
        throw new ModelConstraintException(sender, msg);
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param scope The scope.
     */
    public static void scopeNotNull(Construct sender, Topic[] scope) {
        if (scope == null) {
            _reportError(sender, "The scope must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param scope The scope.
     */
    public static void scopeNotNull(Construct sender, Collection<Topic> scope) {
        if (scope == null) {
            _reportError(sender, "The scope must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>type</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param type The type.
     */
    public static void typeNotNull(Construct sender, Topic type) {
        if (type == null) {
            _reportError(sender, "The type must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>value</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param value The value.
     */
    public static void valueNotNull(Construct sender, Object value) {
        if (value == null) {
            _reportError(sender, "The value must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>value</tt> or
     * the <tt>datatype</tt> is <tt>null</tt>.
     *
     * @param sender The sender.
     * @param value The value.
     * @param datatype The datatype.
     */
    public static void valueNotNull(Construct sender, Object value, Locator datatype) {
        valueNotNull(sender, value);
        if (datatype == null) {
            _reportError(sender, "The datatype must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>player</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender.
     * @param player The player.
     */
    public static void playerNotNull(Construct sender, Topic player) {
        if (player == null) {
            _reportError(sender, "The role player must not be null");
        }
    }

}
