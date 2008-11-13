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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.tinytim.internal.api.ITopicMap;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Variant;

import junit.framework.TestCase;

/**
 * Base class of all tinyTiM-specific test cases.
 * 
 * This class sets up a default {@link org.tinytim.TopicMapSystemFactoryImpl},
 * a {@link org.tinytim.MemoryTopicMapSystem}, and a 
 * {@link org.tinytim.MemoryTopicMap}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TinyTimTestCase extends TestCase {

    protected static final String _IRI = "http://www.semagia.com/tinyTiM/testTopicMap/";
    protected Locator _base;
    protected TopicMap _tm;
    protected TopicMapSystem _sys;
    protected TopicMapSystemFactory _sysFactory;

    public TinyTimTestCase() {
        super();
    }

    public TinyTimTestCase(String name) {
        super(name);
    }

    /**
     * Returns additional / non-default properties which should be set
     * to configure the {@link org.tmapi.core.TopicMapSystemFactory}.
     *
     * @return Properties instance or <code>null</code> if no properties != 
     *          default properties should be set.
     */
    protected Properties getAdditionalProperties() {
        Properties props = new Properties();
        for (Enumeration<?> e = System.getProperties().propertyNames(); e.hasMoreElements();) {
            String name = null;
            try {
                name = (String) e.nextElement();
            }
            catch (ClassCastException ex) {
                continue;
            }
            if (name.startsWith("org.tmapi") || name.startsWith("http://tinytim")) {
                props.setProperty(name, System.getProperty(name));
            }
        }
        return props;
    }

    /**
     * Creates a topic with a random item identifier.
     *
     * @return The topic.
     */
    protected Topic createTopic() {
        return _tm.createTopic();
    }

    /**
     * Creates an association with a random type and no roles.
     *
     * @return The association.
     */
    protected Association createAssociation() {
        return _tm.createAssociation(createTopic());
    }

    /**
     * Creates a role which is part of a random association with a random
     * player and type.
     *
     * @return The role.
     */
    protected Role createRole() {
        return createAssociation().createRole(createTopic(), createTopic());
    }

    /**
     * Creates an occurrence which is part of a random topic with a random type.
     *
     * @return The occurrence.
     */
    protected Occurrence createOccurrence() {
        return createTopic().createOccurrence(createTopic(), "Occurrence");
    }

    /**
     * Creates a name which is part of a newly created topic using the default
     * type name.
     *
     * @return The name.
     */
    protected Name createName() {
        return createTopic().createName("Name");
    }

    /**
     * Creates a variant which is part of a newly created name.
     *
     * @return The variant.
     */
    protected Variant createVariant() {
        return createName().createVariant("Variant", createTopic());
    }

    protected Locator createLocator(final String reference) {
        return _sys.createLocator(reference);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _sysFactory = new TopicMapSystemFactoryImpl();
        Properties properties = getAdditionalProperties();
        if (properties != null) {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                _sysFactory.setProperty(name, properties.getProperty(name));
            }
        }
        _sys =  _sysFactory.newTopicMapSystem();
        _base = _sys.createLocator(_IRI);
        _tm = (ITopicMap) _sys.createTopicMap(_base);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        for (Locator loc: new ArrayList<Locator>(_sys.getLocators())) {
            _sys.getTopicMap(loc).remove();
        }
        _sys.close();
        _sysFactory = null;
        _sys = null;
        _tm = null;
        _base = null;
    }

}
