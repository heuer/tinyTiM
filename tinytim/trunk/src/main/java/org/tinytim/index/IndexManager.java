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

    private TypeInstanceIndexImpl _typeInstanceIndex;
    private ScopedIndexImpl _scopedIndex;
    private LiteralIndexImpl _literalIndex;

    public IndexManager(IEventPublisher publisher) {
        _typeInstanceIndex = new TypeInstanceIndexImpl(publisher);
        _scopedIndex = new ScopedIndexImpl(publisher);
        _literalIndex = new LiteralIndexImpl(publisher);
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
        _typeInstanceIndex = null;
        _scopedIndex = null;
        _literalIndex = null;
    }
}
