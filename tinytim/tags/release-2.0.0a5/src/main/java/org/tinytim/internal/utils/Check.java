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
import org.tmapi.core.TopicMap;

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
    private static void _reportModelConstraintViolation(Construct sender, String msg) {
        throw new ModelConstraintException(sender, msg);
    }

    /**
     * 
     *
     * @param msg
     */
    private static void _reportIllegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
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
            _reportModelConstraintViolation(sender, "The scope must not be null");
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
            _reportModelConstraintViolation(sender, "The scope must not be null");
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
            _reportModelConstraintViolation(sender, "The type must not be null");
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
            _reportModelConstraintViolation(sender, "The value must not be null");
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
            _reportModelConstraintViolation(sender, "The datatype must not be null");
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
            _reportModelConstraintViolation(sender, "The role player must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>iid</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param iid The item identifier.
     */
    public static void itemIdentifierNotNull(Construct sender, Locator iid) {
        if (iid == null) {
            _reportModelConstraintViolation(sender, "The item identifier must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>sid</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param sid The subject identifier.
     */
    public static void subjectIdentifierNotNull(Construct sender, Locator sid) {
        if (sid == null) {
            _reportModelConstraintViolation(sender, "The subject identifier must not be null");
        }
    }

    /**
     * Throws a {@link ModelConstraintException} iff the <tt>slo</tt> is 
     * <tt>null</tt>.
     *
     * @param sender The sender
     * @param slo The subject locator.
     */
    public static void subjectLocatorNotNull(Construct sender, Locator slo) {
        if (slo == null) {
            _reportModelConstraintViolation(sender, "The subject locator must not be null");
        }
    }

    /**
     * Throws an {@link ModelConstraintException} iff the <tt>theme</tt> is 
     * <tt>null</tt>.
     *
     * @param theme The theme.
     */
    public static void themeNotNull(Construct sender, Topic theme) {
        if (theme == null) {
            _reportModelConstraintViolation(sender, "The theme must not be null");
        }
    }

    /**
     * Reports a {@link ModelConstraintException} iff the <tt>sender<tt> and
     * the <tt>construct</tt> do not belong to the same topic map.
     *
     * @param sender The sender.
     * @param construct The construct.
     */
    public static void sameTopicMap(Construct sender, Construct construct) {
        if (construct == null) {
            return;
        }
        _sameTopicMap(sender, sender.getTopicMap(), construct);
    }

    public static void sameTopicMap(Construct sender, Construct...constructs) {
        if (constructs == null || constructs.length == 0) {
            return;
        }
        TopicMap tm = sender.getTopicMap();
        for (Construct construct: constructs) {
            _sameTopicMap(sender, tm, construct);
        }
    }

    public static void sameTopicMap(Construct sender, Collection<? extends Construct> constructs) {
        if (constructs == null) {
            return;
        }
        TopicMap tm = sender.getTopicMap();
        for (Construct construct: constructs) {
            _sameTopicMap(sender, tm, construct);
        }
    }

    private static void _sameTopicMap(Construct sender, TopicMap tm, Construct other) {
        if (!tm.equals(other.getTopicMap())) {
            _reportModelConstraintViolation(sender, "All constructs must belong to the same topic map");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} iff the <tt>type</tt> is 
     * <tt>null</tt>.
     *
     * @param type The type.
     */
    public static void typeNotNull(Topic type) {
        if (type == null) {
            _reportIllegalArgument("The type must not be null");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>sid</tt> is 
     * <tt>null</tt>.
     *
     * @param sid The subject identifier.
     */
    public static void subjectIdentifierNotNull(Locator sid) {
        if (sid == null) {
            _reportIllegalArgument("The subject identifier must not be null");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>slo</tt> is 
     * <tt>null</tt>.
     *
     * @param slo The subject locator.
     */
    public static void subjectLocatorNotNull(Locator slo) {
        if (slo == null) {
            _reportIllegalArgument("The subject locator must not be null");
        }
    }

    /**
     * Reports an {@link IllegalArgumentException} iff the <tt>iid</tt> is 
     * <tt>null</tt>.
     *
     * @param iid The item identifier.
     */
    public static void itemIdentifierNotNull(Locator iid) {
        if (iid == null) {
            _reportIllegalArgument("The item identifier must not be null");
        }
    }

}
