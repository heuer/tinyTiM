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

import org.tinytim.TopicMapImpl;
import org.tinytim.TopicMapSystemFactoryImpl;
import org.tinytim.TopicMapSystemImpl;
import org.tmapi.core.Locator;

import junit.framework.TestCase;

/**
 * Base class of all tinyTiM-specific test cases.
 * 
 * This class sets up a default {@link org.tinytim.TopicMapSystemFactoryImpl},
 * a {@link org.tinytim.TopicMapSystemImpl}, and a 
 * {@link org.tinytim.TopicMapImpl}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TinyTimTestCase extends TestCase {

    protected static final String _IRI = "http://www.semagia.com/tinyTiM/testTopicMap/";
    protected Locator _base;
    protected TopicMapImpl _tm;
    protected TopicMapSystemImpl _sys;
    protected TopicMapSystemFactoryImpl _sysFactory;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _sysFactory = new TopicMapSystemFactoryImpl();
        _sys =  (TopicMapSystemImpl) _sysFactory.newTopicMapSystem();
        _tm = (TopicMapImpl) _sys.createTopicMap(_IRI);
        _base = _tm.getBaseLocator();
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
    }

}
