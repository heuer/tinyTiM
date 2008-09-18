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

import org.tinytim.core.AbstractMapInputHandler;
import org.tmapi.core.TopicMap;

/**
 * {@link com.semagia.mio.IMapHandler} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TinyTimMapInputHandler extends AbstractMapInputHandler {

    /**
     * Intitializes a new <tt>MapInputHandler</tt> instance with the specified
     * <tt>topicMap</tt>.
     *
     * @param topicMap The {@link TopicMap} instance.
     */
    public TinyTimMapInputHandler(final TopicMap topicMap) {
        super(topicMap);
    }

}
