/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package org.tinytim.schema;

/**
 * Generic schema-specific exception which may be thrown in case of 
 * schema-related errors (i.e. syntax errors).
 * <p>
 * This exception must not be thrown in case of schema violation errors, use
 * {@link SchemaViolationException} for that purpose.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class SchemaException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2670201943956954869L;

    /**
     * Initializes a new instance.
     *
     * @param message The message.
     */
    public SchemaException(String message) {
        super(message);
    }

}
