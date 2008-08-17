/*
 * This is tinyTiM, a tiny Topic Maps engine.
 *
 * Copyright (C) 2008 Lars Heuer (heuer[at]semagia.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
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
