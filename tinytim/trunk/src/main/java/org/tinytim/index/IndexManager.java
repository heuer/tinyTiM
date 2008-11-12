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
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * {@link IIndexManager} implementation which provides autoupdated default 
 * indexes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class IndexManager implements IIndexManager {

    private final TypeInstanceIndexImpl _typeInstanceIndex;
    private final ScopedIndexImpl _scopedIndex;
    private final LiteralIndexImpl _literalIndex;

    public IndexManager() {
        _typeInstanceIndex = new TypeInstanceIndexImpl();
        _scopedIndex = new ScopedIndexImpl();
        _literalIndex = new LiteralIndexImpl();
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IEventPublisherAware#subscribe(org.tinytim.core.IEventPublisher)
     */
    public void subscribe(final IEventPublisher publisher) {
        _typeInstanceIndex.subscribe(publisher);
        _scopedIndex.subscribe(publisher);
        _literalIndex.subscribe(publisher);
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IEventPublisherAware#unsubscribe(org.tinytim.core.IEventPublisher)
     */
    public void unsubscribe(IEventPublisher publisher) {
        _typeInstanceIndex.unsubscribe(publisher);
        _scopedIndex.unsubscribe(publisher);
        _literalIndex.unsubscribe(publisher);
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndexManager#getTypeInstanceIndex()
     */
    public TypeInstanceIndex getTypeInstanceIndex() {
        return _typeInstanceIndex;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndexManager#getScopedIndex()
     */
    public ScopedIndex getScopedIndex() {
        return _scopedIndex;
    }

    /* (non-Javadoc)
     * @see org.tinytim.index.IIndexManager#getLiteralIndex()
     */
    public LiteralIndex getLiteralIndex() {
        return _literalIndex;
    }

    public void close() {
        _typeInstanceIndex.clear();
        _scopedIndex.clear();
        _literalIndex.clear();
    }
}
