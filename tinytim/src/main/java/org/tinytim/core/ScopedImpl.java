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

import java.util.Set;

import org.tinytim.internal.api.Event;
import org.tinytim.internal.api.IScope;
import org.tinytim.internal.api.IScoped;
import org.tinytim.internal.api.ITopic;
import org.tinytim.internal.api.ITopicMap;
import org.tinytim.internal.utils.Check;

import org.tmapi.core.Topic;

/**
 * Class that provides a "scope" property and sends events if that property 
 * changes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class ScopedImpl extends TypedImpl implements IScoped {

    protected IScope _scope;

    protected ScopedImpl(ITopicMap topicMap, Topic type, IScope scope) {
        super(topicMap, type);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScoped#getScopeObject()
     */
    @Override
    public IScope getScopeObject() {
        return _scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.internal.api.IScoped#setScopeObject(org.tinytim.internal.api.IScope)
     */
    @Override
    public void setScopeObject(IScope scope) {
        if (_scope == scope) {
            return;
        }
        _fireEvent(Event.SET_SCOPE, _scope, scope);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Scoped#getScope()
     */
    @Override
    public Set<Topic> getScope() {
        return _scope.asSet();
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Scoped#addTheme(org.tmapi.core.Topic)
     */
    @Override
    public void addTheme(Topic theme) {
        Check.themeNotNull(this, theme);
        Check.sameTopicMap(this, theme);
        setScopeObject(_scope.add(theme));
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    @Override
    public void removeTheme(Topic theme) {
        if (theme == null) {
            return;
        }
        setScopeObject(_scope.remove((ITopic) theme));
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.TypedImpl#dispose()
     */
    @Override
    protected void dispose() {
        _scope = null;
        super.dispose();
    }

}
