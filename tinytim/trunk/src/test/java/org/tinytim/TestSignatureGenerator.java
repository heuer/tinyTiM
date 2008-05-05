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

import org.tinytim.SignatureGenerator;
import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

/**
 * Tests against the {@link org.tinytim.SignatureGenerator}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestSignatureGenerator extends TinyTimTestCase {

    /**
     * Tests if an association with no initialized properties returns the same 
     * signature. 
     */
    public void testAssociationBasic() {
        Association assoc = _tm.createAssociation();
        Association assoc2 = _tm.createAssociation();
        assertFalse(assoc.getObjectId().equals(assoc2.getObjectId()));
        String sig = SignatureGenerator.generateSignature(assoc);
        assertEquals(sig, SignatureGenerator.generateSignature(assoc2));
    }

    /**
     * Tests if a role with no initialized properties returns the same 
     * signature. 
     */
    public void testRoleBasic() {
        Association assoc = _tm.createAssociation();
        AssociationRole role = assoc.createAssociationRole(null, null);
        AssociationRole role2 = assoc.createAssociationRole(null, null);
        assertFalse(role.getObjectId().equals(role2.getObjectId()));
        String sig = SignatureGenerator.generateSignature(role);
        assertEquals(sig, SignatureGenerator.generateSignature(role2));
    }

    /**
     * Tests if an occurrence with no initialized properties returns the same 
     * signature. 
     */
    public void testOccurrenceBasic() {
        Topic topic = _tm.createTopic();
        Occurrence occ = topic.createOccurrence((String) null, null, null);
        Occurrence occ2 = topic.createOccurrence((String) null, null, null);
        assertFalse(occ.getObjectId().equals(occ2.getObjectId()));
        String sig = SignatureGenerator.generateSignature(occ);
        assertEquals(sig, SignatureGenerator.generateSignature(occ2));
    }

    /**
     * Tests if a name with no initialized properties returns the same 
     * signature. 
     */
    public void testNameBasic() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName(null, null);
        TopicName name2 = topic.createTopicName(null, null);
        assertFalse(name.getObjectId().equals(name2.getObjectId()));
        String sig = SignatureGenerator.generateSignature(name);
        assertEquals(sig, SignatureGenerator.generateSignature(name2));
    }

    /**
     * Tests if a variant with no initialized properties returns the same 
     * signature. 
     */
    public void testVariantBasic() {
        Topic topic = _tm.createTopic();
        TopicName name = topic.createTopicName("tinyTiM", null);
        Variant variant = name.createVariant("tiny Topic Maps", null);
        String sig = SignatureGenerator.generateSignature(variant);
        assertEquals(sig, SignatureGenerator.generateSignature(variant));
    }

    /**
     * Tests if associations with the same type return the same signature.
     */
    public void testAssociationTyped() {
        Association assoc = _tm.createAssociation();
        String sigBefore = SignatureGenerator.generateSignature(assoc);
        Topic type = _tm.createTopic();
        assoc.setType(type);
        String sigAfter = SignatureGenerator.generateSignature(assoc);
        assertFalse(sigBefore.equals(sigAfter));
        Association assoc2 = _tm.createAssociation();
        assertEquals(sigBefore, SignatureGenerator.generateSignature(assoc2));
        assoc2.setType(type);
        assertEquals(sigAfter, SignatureGenerator.generateSignature(assoc2));
    }

    public void testRoles() {
        Association assoc = _tm.createAssociation();
        Topic type = _tm.createTopic();
        Topic player = _tm.createTopic();
        AssociationRole role1 = assoc.createAssociationRole(player, type);
        AssociationRole role2 = assoc.createAssociationRole(player, type);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getAssociationRoles().size());
        String sig = SignatureGenerator.generateSignature(role1);
        assertEquals(sig, SignatureGenerator.generateSignature(role2));
    }
}
