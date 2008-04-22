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

import java.util.Collection;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * {@link org.tmapi.core.Variant} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class VariantImpl extends DatatypeAwareConstruct implements 
        Variant, IScoped {

    VariantImpl(TopicMapImpl topicMap, String value, Collection<Topic> scope) {
        super(topicMap, null, value, scope);
    }

    VariantImpl(TopicMapImpl topicMap, Locator value, Collection<Topic> scope) {
        super(topicMap, null, value, scope);
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Variant#getTopicName()
     */
    public TopicName getTopicName() {
        return (TopicName) _parent;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Variant#remove()
     */
    public void remove() throws TMAPIException {
        ((TopicNameImpl) _parent).removeVariant(this);
        super.dispose();
    }

}
