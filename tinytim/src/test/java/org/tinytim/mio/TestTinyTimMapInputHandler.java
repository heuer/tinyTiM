/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package org.tinytim.mio;

import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Typed;

import com.semagia.mio.AbstractMapHandlerTest;
import com.semagia.mio.IMapHandler;

/**
 * Tests against the {@link org.tinytim.mio.TinyTimMapInputHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTinyTimMapInputHandler extends AbstractMapHandlerTest {

    protected static final String _IRI = "http://www.semagia.com/tinyTiM/testTopicMap/";
    private TopicMapSystemFactory _sysFactory;
    private TopicMapSystem _sys;
    private TopicMap _tm;
    private Locator _base;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _sysFactory = TopicMapSystemFactory.newInstance();
        _sys =  _sysFactory.newTopicMapSystem();
        _base = _sys.createLocator(_IRI);
        _tm = _sys.createTopicMap(_base);
    }

    @Override
    protected IMapHandler makeMapHandler() {
        return new TinyTimMapInputHandler(_tm);
    }


    @Override
    protected Object getType(Object obj) {
        return ((Typed) obj).getType();
    }

    @Override
    protected int getAssociationSize() {
        return _tm.getAssociations().size();
    }

    @Override
    protected Object getConstructByItemIdentifier(String iid) {
        return _tm.getConstructByItemIdentifier(_tm.createLocator(iid));
    }

    @Override
    protected String getDatatypeAsString(Object obj) {
        return ((DatatypeAware) obj).getDatatype().getReference();
    }

    @Override
    protected Object getParent(Object obj) {
        return ((Construct) obj).getParent();
    }

    @Override
    protected Object getReified(Object obj) {
        return ((Topic) obj).getReified();
    }

    @Override
    protected Object getReifier(Object obj) {
        return ((Reifiable) obj).getReifier();
    }

    @Override
    protected Object getTopicBySubjectIdentifier(String sid) {
        return _tm.getTopicBySubjectIdentifier(_tm.createLocator(sid));
    }

    @Override
    protected Object getTopicBySubjectLocator(String slo) {
        return _tm.getTopicBySubjectLocator(_tm.createLocator(slo));
    }

    @Override
    protected Object getTopicMapReifier() {
        return _tm.getReifier();
    }

    @Override
    protected int getTopicSize() {
        return _tm.getTopics().size();
    }

    @Override
    protected String getValue(Object obj) {
        if (obj instanceof DatatypeAware) {
            return ((DatatypeAware) obj).getValue();
        }
        return ((Name) obj).getValue();
    }

}
