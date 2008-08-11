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
