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
package org.tinytim.mio;

import java.io.IOException;

import org.tmapi.core.TopicMap;

/**
 * This interface represents a writer to serialize a topic map.
 * <p>
 * The writer is not meant to be reused and should be thrown away once the 
 * {@link #write(TopicMap)} method was invoked.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface TopicMapWriter {

    /**
     * Serializes the specified <tt>topicMap</tt>.
     *
     * @param topicMap The topic map to serialize.
     * @throws IOException If an error ocurrs.
     */
    public void write(TopicMap topicMap) throws IOException;

}
