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

import java.io.IOException;

/**
 * This interface represents a reader to deserialize a Topic Maps schema from 
 * a source.
 * <p>
 * The reader is not meant to be reused and should be thrown away once the 
 * {@link #read()} method was invoked.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface SchemaReader {

    /**
     * Reads a schema.
     *
     * @return The read schema.
     * @throws IOException If an error occurs.
     * @throws SchemaException If a schema-specific error occurs (i.e. syntax error).
     */
    public Schema read() throws IOException, SchemaException;

}
