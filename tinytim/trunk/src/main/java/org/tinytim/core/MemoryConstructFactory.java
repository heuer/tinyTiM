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
package org.tinytim.core;

import org.tinytim.internal.api.IConstructFactory;
import org.tinytim.internal.api.IName;
import org.tinytim.internal.api.IOccurrence;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.IVariant;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class MemoryConstructFactory implements IConstructFactory {

    private final MemoryTopicMap _tm;

    public MemoryConstructFactory(MemoryTopicMap tm) {
        _tm = tm;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createTopic()
     */
    public ITopic createTopic() {
        TopicImpl topic = new TopicImpl(_tm);
        _tm.addTopic(topic);
        return topic;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createAssociation()
     */
    public Association createAssociation() {
        AssociationImpl assoc = new AssociationImpl(_tm);
        _tm.addAssociation(assoc);
        return assoc;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createName(org.tinytim.internal.api.ITopic)
     */
    public IName createName(ITopic parent) {
        NameImpl name = new NameImpl(_tm);
        ((TopicImpl) parent).addName(name);
        return name;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createOccurrence(org.tinytim.internal.api.ITopic)
     */
    public IOccurrence createOccurrence(ITopic parent) {
        OccurrenceImpl occ = new OccurrenceImpl(_tm);
        ((TopicImpl) parent).addOccurrence(occ);
        return occ;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createRole(org.tmapi.core.Association)
     */
    public Role createRole(Association parent) {
        RoleImpl role = new RoleImpl(_tm);
        ((AssociationImpl) parent).addRole(role);
        return role;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IConstructFactory#createVariant(org.tinytim.internal.api.IName)
     */
    public IVariant createVariant(IName parent) {
        VariantImpl variant = new VariantImpl(_tm);
        for (Topic theme: parent.getScope()) {
            variant._addNameTheme(theme);
        }
        ((NameImpl) parent).addVariant(variant);
        return variant;
    }

}
