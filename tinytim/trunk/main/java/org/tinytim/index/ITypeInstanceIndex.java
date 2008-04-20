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
import org.tinytim.AssociationRoleImpl;
import org.tinytim.OccurrenceImpl;
import org.tinytim.TopicNameImpl;
import org.tmapi.core.Topic;

/**
 * Type-instance index. This index allows the retrieval of all typed Topic Maps 
 * constructs and topics.
 * 
 * Copied from the TMAPIX-project, this interface is not meant to be used
 * outside of the tinyTiM package. Once TMAPI exposes <code>ITyped</code> this
 * interface should be changed to return interfaces rather than the 
 * implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface ITypeInstanceIndex extends IIndex {

    /**
     * Returns the topics that include the <code>type</code> as one of 
     * their types. 
     *
     * @param type The type of topics to be returned. If <code>type</code> is
     *              <code>null</code> all untyped topics will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getTopics(Topic... type);

    /**
     * Returns the associations that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of associations to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              associations will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<AssociationImpl> getAssociations(Topic type);

    /**
     * Returns the association roles that are typed by the 
     * {@link org.tmapi.core.Topic} <code>type</code>.
     *
     * @param type The type of association roles to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              association roles will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<AssociationRoleImpl> getRoles(Topic type);

    /**
     * Returns the occurrences that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of occurrences to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              occurrences will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<OccurrenceImpl> getOccurrences(Topic type);

    /**
     * Returns the topic names that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of topic names to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              topic names will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<TopicNameImpl> getNames(Topic type);

    /**
     * Returns the topics that are used as type of {@link org.tmapi.core.Topic}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getTopicTypes();

    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.Association}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getAssociationTypes();

    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.AssociationRole}s. 
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getRoleTypes();

    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.Occurrence}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getOccurrenceTypes();

    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.TopicName}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getNameTypes();

}
