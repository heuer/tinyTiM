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
package org.tinytim.index;

import org.tinytim.core.IEventPublisher;
import org.tinytim.core.IEventPublisherAware;
import org.tmapi.index.Index;

/**
 * Abstract base class for {@link org.tmapi.index.Index} implementation which 
 * are autoupdated.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractIndex implements Index, IEventPublisherAware {

    /* (non-Javadoc)
     * @see org.tinytim.core.IEventPublisherAware#unsubscribe(org.tinytim.core.IEventPublisher)
     */
    public void unsubscribe(IEventPublisher publisher) {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#close()
     */
    public void close() {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#isOpen()
     */
    public boolean isOpen() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#open()
     */
    public void open() {
        // noop.
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.Index#reindex()
     */
    public void reindex() {
        // noop.
    }

}
