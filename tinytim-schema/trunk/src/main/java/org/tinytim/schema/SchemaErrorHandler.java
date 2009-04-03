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

import org.tmapi.core.Construct;

/**
 * Handler which gets notified in case of Topic Maps schema violations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface SchemaErrorHandler {

    /**
     * Notification when the validation starts.
     *
     * @throws SchemaViolationException In case of an error.
     */
    public void startValidation() throws SchemaViolationException;

    /**
     * Notification when the validation has finished.
     *
     * @throws SchemaViolationException In case of an error.
     */
    public void endValidation() throws SchemaViolationException;

    /**
     * Notification when an error has occured.
     * <p>
     * The handler may throw a {@link SchemaViolationException} or simply report
     * the violation (i.e. write a log entry) without forcing the 
     * {@link SchemaValidator} to stop the validation process.
     * </p>
     *
     * @param message The error message.
     * @param construct The construct which causes the error.
     * @throws SchemaViolationException Default case.
     */
    public void error(String message, Construct construct) throws SchemaViolationException;

}
