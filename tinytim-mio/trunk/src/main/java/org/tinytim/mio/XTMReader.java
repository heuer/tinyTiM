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

import org.tmapi.core.TopicMap;

import com.semagia.mio.Syntax;

/**
 * {@link ITopicMapReader} implementation that is able to deserialize XML Topic 
 * Maps (XTM) <a href="http://www.topicmaps.org/xtm/1.0/">version 1.0</a> and
 * <a href="http://www.isotopicmaps.org/sam/sam-xtm/">version 2.0</a>.
 * <p>
 * This reader detects automatically the used XTM version.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XTMReader extends AbstractTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     */
    public XTMReader(final TopicMap topicMap) {
        super(topicMap, Syntax.XTM);
    }

}
