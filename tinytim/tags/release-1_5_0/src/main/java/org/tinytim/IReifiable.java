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
package org.tinytim;

import org.tmapi.core.Topic;

/**
 * Reifiable Topic Maps construct.
 * 
 * Every Topic Maps construct != topic is reifiable.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IReifiable extends IConstruct {

    /**
     * Returns the reifier of this construct.
     *
     * @return The topic that reifies this construct or <code>null</code> if 
     *          this construct has no reifier.
     */
    public Topic getReifier();

    /**
     * Sets the reifier of this construct.
     * 
     * If the <code>reifier</code> reifies another Topic Maps construct, a
     * {@link org.tmapi.core.ModelConstraintException} is thrown.
     *
     * @param reifier The reifier or <code>null</code>.
     */
    public void setReifier(Topic reifier);
}
