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
package org.tinytim.index.tmapi;

import java.lang.ref.WeakReference;

import org.tinytim.ICollectionFactory;
import org.tinytim.TopicMapImpl;
import org.tmapi.core.HelperObjectConfigurationException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.TMAPIIndexException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTMAPIIndex implements Index {

    protected WeakReference<TopicMapImpl> _weakTopicMap;

    public AbstractTMAPIIndex(TopicMapImpl topicMap, ICollectionFactory collFactory) {
        _weakTopicMap = new WeakReference<TopicMapImpl>(topicMap);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#close()
     */
    public void close() throws TMAPIIndexException {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#isOpen()
     */
    public boolean isOpen() throws TMAPIIndexException {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#open()
     */
    public void open() throws TMAPIIndexException {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.TopicMapSystem.ConfigurableHelperObject#configure(org.tmapi.core.TopicMap)
     */
    public void configure(TopicMap tm)
            throws HelperObjectConfigurationException {
        // noop.
    }

}
