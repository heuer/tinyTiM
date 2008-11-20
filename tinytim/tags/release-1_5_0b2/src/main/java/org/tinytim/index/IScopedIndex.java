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

import java.util.Collection;

import org.tinytim.AssociationImpl;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicNameImpl;
import org.tinytim.VariantImpl;
import org.tmapi.core.Topic;

/**
 * Index for all scoped Topic Maps Constructs.
 * 
 * Copied from the TMAPIX-project, this interface is not meant to be used
 * outside of the tinyTiM package. Once TMAPI exposes <code>ITyped</code> this
 * interface should be changed to return interfaces rather than the 
 * implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IScopedIndex extends IIndex {

    /**
     * Returns all associations which use the specified topic as theme.
     *
     * @param theme The theme.
     * @return A (maybe empty) collection of associations which use the theme
     *          in their [scope] property.
     */
    public Collection<AssociationImpl> getAssociationsByTheme(Topic theme);

    public Collection<Topic> getAssociationThemes();

    /**
     * Returns all occurrences which use the specified topic as theme.
     *
     * @param theme The theme.
     * @return A (maybe empty) collection of occurrences which use the theme
     *          in their [scope] property.
     */
    public Collection<OccurrenceImpl> getOccurrencesByTheme(Topic theme);

    public Collection<Topic> getOccurrenceThemes();

    /**
     * Returns all names which use the specified topic as theme.
     *
     * @param theme The theme.
     * @return A (maybe empty) collection of names which use the theme
     *          in their [scope] property.
     */
    public Collection<TopicNameImpl> getNamesByTheme(Topic theme);

    public Collection<Topic> getNameThemes();

    /**
     * Returns all variants which use the specified topic as theme.
     *
     * @param theme The theme.
     * @return A (maybe empty) collection of variants which use the theme
     *          in their [scope] property.
     */
    public Collection<VariantImpl> getVariantsByTheme(Topic theme);

    public Collection<Topic> getVariantThemes();
}
