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

import org.tmapi.core.TopicMap;

/**
 * Validates a topic map with respect to a schema. The schema is determined when
 * the <tt>SchemaValidator</tt> is created and cannot be changed.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface SchemaValidator {

    /**
     * Returns the underlying error handler.
     * <p>
     * If no error handler was set explictly via 
     * {@link #setErrorHandler(SchemaErrorHandler)}, the schema validator 
     * returns a default error handler.
     * </p>
     *
     * @return The error handler instance, never <tt>null</tt>.
     */
    public SchemaErrorHandler getErrorHandler();

    /**
     * Sets the error handler which is used to report errors.
     * <p>
     * A previously set error handler will be overridden.
     * </p>
     *
     * @param errorHandler The error handler.
     */
    public void setErrorHandler(SchemaErrorHandler errorHandler);

    /**
     * Validates the topic map against the schema to which this validator 
     * instance is bound to.
     * <p>
     * If the schema validator stops at the first error or checks the whole 
     * topic map and reports all found errors depends on the 
     * {@link SchemaErrorHandler}.
     * </p>
     *
     * @param topicMap The topic map to validate.
     * @throws SchemaViolationException In case of an error.
     */
    public void validate(TopicMap topicMap) throws SchemaViolationException;

}
