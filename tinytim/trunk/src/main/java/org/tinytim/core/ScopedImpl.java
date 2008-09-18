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

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

/**
 * Class that provides a "scope" property and sends events if that property 
 * changes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class ScopedImpl extends TypedImpl implements IScoped {

    //NOTE: This class does NOT implement IScoped by intention!

    protected IScope _scope;

    ScopedImpl(TopicMapImpl tm) {
        super(tm);
        _scope = Scope.UCS;
    }

    ScopedImpl(TopicMapImpl topicMap, Topic type, IScope scope) {
        super(topicMap, type);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScoped#getScopeObject()
     */
    public IScope getScopeObject() {
        return _scope;
    }

    /* (non-Javadoc)
     * @see org.tinytim.core.IScoped#setScopeObject(org.tinytim.core.IScope)
     */
    public void setScopeObject(IScope scope) {
        if (_scope == scope) {
            return;
        }
        _fireEvent(Event.SET_SCOPE, _scope, scope);
        _scope = scope;
    }

    /* (non-Javadoc)
     * @see org.tmapi.core.ScopedObject#getScope()
     */
    public Set<Topic> getScope() {
        return _scope.asSet();
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void addTheme(Topic theme) {
        if (theme == null) {
            throw new ModelConstraintException(this, "The theme must not be null");
        }
        setScopeObject(_scope.add(theme));
    }

    /* (non-Javadoc)
     * @see org.tmapi.Scoped#removeTheme(org.tmapi.core.Topic)
     */
    public void removeTheme(Topic theme) {
        setScopeObject(_scope.remove(theme));
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
