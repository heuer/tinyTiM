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
package org.tinytim.internal.api;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

/**
 * Factory for {@link IConstruct}s.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IConstructFactory {

    /**
     * Creates a topic attached to the topic map this factory belongs to.
     *
     * @return A topic with no identity.
     */
    public ITopic createTopic();

    /**
     * Creates an association attached to the topic map this 
     * factory belongs to.
     *
     * @return An association with no type, scope, and roles.
     */
    public Association createAssociation();

    /**
     * Creates a role attached to the specified <tt>parent</tt>.
     *
     * @param parent The parent of the role.
     * @return A role with no type and player.
     */
    public Role createRole(Association parent);

    /**
     * Creates an occurrence attached to the specified <tt>parent</tt>.
     *
     * @param parent The parent of the occurrence.
     * @return An occurrence with no type, literal, and scope.
     */
    public IOccurrence createOccurrence(ITopic parent);

    /**
     * Creates a name attached to the specified <tt>parent</tt>.
     *
     * @param parent The parent of the name.
     * @return A name with no type, literal, and scope.
     */
    public IName createName(ITopic parent);

    /**
     * Creates a variant attached to the specified <tt>parent</tt>.
     *
     * @param parent The parent of the variant.
     * @return A variant with the parent's scope, but no own scope and 
     *          no literal.
     */
    public IVariant createVariant(IName parent);

}
