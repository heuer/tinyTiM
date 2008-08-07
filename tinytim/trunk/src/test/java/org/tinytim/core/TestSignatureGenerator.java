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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
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
        Topic type = _tm.createTopic();
        Association assoc = _tm.createAssociation(type);
        Association assoc2 = _tm.createAssociation(type);
        assertFalse(assoc.getId().equals(assoc2.getId()));
        String sig = SignatureGenerator.generateSignature(assoc);
        assertEquals(sig, SignatureGenerator.generateSignature(assoc2));
    }

    /**
     * Tests if an occurrence with no initialized properties returns the same 
     * signature. 
     */
    public void testOccurrenceBasic() {
        Topic topic = _tm.createTopic();
        Topic type = _tm.createTopic();
        Occurrence occ = topic.createOccurrence(type, "tinyTiM");
        Occurrence occ2 = topic.createOccurrence(type, "tinyTiM");
        assertFalse(occ.getId().equals(occ2.getId()));
        String sig = SignatureGenerator.generateSignature(occ);
        assertEquals(sig, SignatureGenerator.generateSignature(occ2));
    }

    /**
     * Tests if a name with no initialized properties returns the same 
     * signature. 
     */
    public void testNameBasic() {
        Topic topic = _tm.createTopic();
        Name name = topic.createName("tinyTiM");
        Name name2 = topic.createName("tinyTiM");
        assertFalse(name.getId().equals(name2.getId()));
        String sig = SignatureGenerator.generateSignature(name);
        assertEquals(sig, SignatureGenerator.generateSignature(name2));
    }

    /**
     * Tests if a variant with no initialized properties returns the same 
     * signature. 
     */
    public void testVariantBasic() {
        Topic topic = _tm.createTopic();
        Name name = topic.createName("tinyTiM");
        Variant variant = name.createVariant("tiny Topic Maps", _tm.createTopic());
        String sig = SignatureGenerator.generateSignature(variant);
        assertEquals(sig, SignatureGenerator.generateSignature(variant));
    }

    /**
     * Tests if associations with the same type return the same signature.
     */
    public void testAssociationTyped() {
        Topic type1 = _tm.createTopic();
        Topic type2 = _tm.createTopic();
        Association assoc = _tm.createAssociation(type1);
        String sigBefore = SignatureGenerator.generateSignature(assoc);
        assoc.setType(type2);
        String sigAfter = SignatureGenerator.generateSignature(assoc);
        assertFalse(sigBefore.equals(sigAfter));
        Association assoc2 = _tm.createAssociation(type1);
        assertEquals(sigBefore, SignatureGenerator.generateSignature(assoc2));
        assoc2.setType(type2);
        assertEquals(sigAfter, SignatureGenerator.generateSignature(assoc2));
    }

    public void testRoles() {
        Association assoc = _tm.createAssociation(_tm.createTopic());
        Topic type = _tm.createTopic();
        Topic player = _tm.createTopic();
        Role role1 = assoc.createRole(type, player);
        Role role2 = assoc.createRole(type, player);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getRoles().size());
        String sig = SignatureGenerator.generateSignature(role1);
        assertEquals(sig, SignatureGenerator.generateSignature(role2));
    }
}
