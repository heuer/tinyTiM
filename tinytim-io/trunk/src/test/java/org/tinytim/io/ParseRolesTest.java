package org.tinytim.io;

import java.io.File;

import junit.framework.TestCase;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;


public class ParseRolesTest  extends TestCase {
		
	public void testBugImportWithoutException() throws Exception{
        TopicMapSystemFactory tmSysFactory = TopicMapSystemFactory.newInstance();
//        tmSysFactory.setProperty(Property.XTM10_REIFICATION, "false");
        TopicMapSystem tmSys = tmSysFactory.newTopicMapSystem();
        TopicMap tm = tmSys.createTopicMap("");
        TopicMapImporter.importInto(tm, "", new File("src/test/parseRoles.xtm"));
        assertEquals(2,tm.getTopics().size());
	}
	public void testBugImportRoles() throws Exception{
        TopicMapSystemFactory tmSysFactory = TopicMapSystemFactory.newInstance();
//        tmSysFactory.setProperty(Property.XTM10_REIFICATION, "false");
        TopicMapSystem tmSys = tmSysFactory.newTopicMapSystem();
        TopicMap tm = tmSys.createTopicMap("");
        TopicMapImporter.importInto(tm, "", new File("src/test/parseRoles.xtm"));
        assertEquals(1,tm.getAssociations().size());
        assertNotNull(((Topic)tm.getTopics().iterator().next()).getRolesPlayed());
        assertEquals(1,((Topic)tm.getTopics().iterator().next()).getRolesPlayed().size());
	}

	public void testBugImportRoles2() throws Exception{
        TopicMapSystemFactory tmSysFactory = TopicMapSystemFactory.newInstance();
//        tmSysFactory.setProperty(Property.XTM10_REIFICATION, "false");
        TopicMapSystem tmSys = tmSysFactory.newTopicMapSystem();
        TopicMap tm = tmSys.createTopicMap("");
        TopicMapImporter.importInto(tm, "", new File("src/test/parseRoles2.xtm"));
        assertEquals(1,tm.getAssociations().size());
        assertNotNull(((Topic)tm.getTopics().iterator().next()).getRolesPlayed());
        assertEquals(1,((Topic)tm.getTopics().iterator().next()).getRolesPlayed().size());
	}
}
