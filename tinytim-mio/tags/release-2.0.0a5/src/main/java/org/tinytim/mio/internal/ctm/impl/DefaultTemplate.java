/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package org.tinytim.mio.internal.ctm.impl;

import java.util.ArrayList;
import java.util.List;

import org.tinytim.mio.internal.ctm.ITemplate;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class DefaultTemplate implements ITemplate {

    private final String _name;
    private List<Object> _params;

    public DefaultTemplate(String name) {
        _name = name;
        _params = new ArrayList<Object>();
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.internal.ctm.ITemplate#getName()
     */
    @Override
    public String getName() {
        return _name;
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.internal.ctm.ITemplate#getParameters()
     */
    @Override
    public List<Object> getParameters() {
        return _params;
    }

    public void addParameter(Object param) {
        if (param == null) {
            throw new IllegalArgumentException("The paramater must not be null");
        }
        _params.add(param);
    }
    
    @Override
    public int compareTo(ITemplate o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
