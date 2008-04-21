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

import org.tmapi.index.IndexFlags;

/**
 * Immutable {@link org.tmapi.index.IndexFlags} implementation.
 * 
 * Use {@link #AUTOUPDATED} or {@link #NOT_AUTOUPDATED}
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class IndexFlagsImpl implements IndexFlags {

    public static IndexFlags AUTOUPDATED = new IndexFlagsImpl(true);
    public static IndexFlags NOT_AUTOUPDATED = new IndexFlagsImpl(false);

    private final boolean _autoUpdated;

    private IndexFlagsImpl(boolean autoUpdated) {
        _autoUpdated = autoUpdated;
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.IndexFlags#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return _autoUpdated;
    }

}
