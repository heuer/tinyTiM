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
package org.tinytim.core;

import java.util.Enumeration;
import java.util.Properties;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.Variant;

import junit.framework.TestCase;

/**
 * Base class of all tinyTiM-specific test cases.
 * 
 * This class sets up a default {@link org.tinytim.TopicMapSystemFactoryImpl},
 * a {@link org.tinytim.TopicMapSystemImpl}, and a 
 * {@link org.tinytim.TopicMapImpl}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TinyTimTestCase extends TestCase {

    protected static final String _IRI = "http://www.semagia.com/tinyTiM/testTopicMap/";
    protected Locator _base;
    protected TopicMapImpl _tm;
    protected TopicMapSystem _sys;
    protected TopicMapSystemFactoryImpl _sysFactory;

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
        return null;
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
        _sys =  (TopicMapSystemImpl) _sysFactory.newTopicMapSystem();
        _base = _sys.createLocator(_IRI);
        _tm = (TopicMapImpl) _sys.createTopicMap(_base);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _sysFactory = null;
        _sys = null;
        _tm = null;
        _base = null;
    }

}
