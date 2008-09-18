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
 * {@link ITopicMapReader} implementation that is able to deserialize 
 * <a href="http://www.isotopicmaps.org/ctm">Compact Topic Maps (CTM) 1.0</a>.
 * <p>
 * Note that this reader implements the CTM draft dtd. 2008-05-15.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class CTMReader extends AbstractTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     */
    public CTMReader(final TopicMap topicMap) {
        super(topicMap, Syntax.CTM);
    }

}
