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
        Topic type = createTopic();
        Association assoc = _tm.createAssociation(type);
        Association assoc2 = _tm.createAssociation(type);
        assertFalse(assoc.getId().equals(assoc2.getId()));
        assertEquals(SignatureGenerator.generateSignature(assoc), 
                        SignatureGenerator.generateSignature(assoc2));
    }

    /**
     * Tests if an occurrence with no initialized properties returns the same 
     * signature. 
     */
    public void testOccurrenceBasic() {
        Topic topic = createTopic();
        Topic type = createTopic();
        Occurrence occ = topic.createOccurrence(type, "tinyTiM");
        Occurrence occ2 = topic.createOccurrence(type, "tinyTiM");
        assertFalse(occ.getId().equals(occ2.getId()));
        assertEquals(SignatureGenerator.generateSignature(occ), 
                        SignatureGenerator.generateSignature(occ2));
        int occ1Sig = SignatureGenerator.generateSignature(occ);
        int occ2Sig = SignatureGenerator.generateSignature(occ2);
        assertEquals(occ1Sig, occ2Sig);
        occ.setType(createTopic());
        int occ1Sig2 = SignatureGenerator.generateSignature(occ);
        assertFalse(occ1Sig == occ1Sig2);
        occ2.setType(occ.getType());
        int occ2Sig2 = SignatureGenerator.generateSignature(occ2);
        assertEquals(occ1Sig2, occ2Sig2);
    }

    /**
     * Tests if a name with no initialized properties returns the same 
     * signature. 
     */
    public void testNameBasic() {
        Topic topic = createTopic();
        Name name = topic.createName("tinyTiM");
        Name name2 = topic.createName("tinyTiM");
        assertFalse(name.getId().equals(name2.getId()));
        int name1Sig = SignatureGenerator.generateSignature(name);
        int name2Sig = SignatureGenerator.generateSignature(name2);
        assertEquals(name1Sig, name2Sig);
        name.setType(createTopic());
        int name1Sig2 = SignatureGenerator.generateSignature(name);
        assertFalse(name1Sig == name1Sig2);
        name2.setType(name.getType());
        int name2Sig2 = SignatureGenerator.generateSignature(name2);
        assertEquals(name1Sig2, name2Sig2);
    }

    /**
     * Tests if a variant with no initialized properties returns the same 
     * signature. 
     */
    public void testVariantBasic() {
        final Name name = createName();
        final Topic theme = createTopic();
        final Topic theme2 = createTopic();
        Variant variant = name.createVariant("tiny Topic Maps", theme);
        Variant variant2 = name.createVariant("tiny Topic Maps", theme);
        int var1Sig = SignatureGenerator.generateSignature(variant);
        int var2Sig = SignatureGenerator.generateSignature(variant2);
        assertEquals(var1Sig, var2Sig);
        variant.addTheme(theme2);
        int var1Sig2 = SignatureGenerator.generateSignature(variant);
        assertFalse(var1Sig == var1Sig2);
        variant2.addTheme(theme2);
        int var2Sig2 = SignatureGenerator.generateSignature(variant2);
        assertEquals(var1Sig2, var2Sig2);
    }

    /**
     * Tests if associations with the same type return the same signature.
     */
    public void testAssociationTyped() {
        Topic type1 = createTopic();
        Topic type2 = createTopic();
        Association assoc = _tm.createAssociation(type1);
        int sigBefore = SignatureGenerator.generateSignature(assoc);
        assoc.setType(type2);
        int sigAfter = SignatureGenerator.generateSignature(assoc);
        assertFalse(sigBefore == sigAfter);
        Association assoc2 = _tm.createAssociation(type1);
        assertEquals(sigBefore, SignatureGenerator.generateSignature(assoc2));
        assoc2.setType(type2);
        assertEquals(sigAfter, SignatureGenerator.generateSignature(assoc2));
    }

    public void testRoles() {
        Association assoc = _tm.createAssociation(createTopic());
        Topic type = createTopic();
        Topic player = createTopic();
        Role role1 = assoc.createRole(type, player);
        Role role2 = assoc.createRole(type, player);
        assertEquals(2, player.getRolesPlayed().size());
        assertEquals(2, assoc.getRoles().size());
        assertEquals(SignatureGenerator.generateSignature(role1), 
                        SignatureGenerator.generateSignature(role2));
    }

    public void testRoles2() {
        Association assoc = createAssociation();
        Topic type = createTopic();
        Topic player = createTopic();
        Role role1 = assoc.createRole(type, player);
        Role role2 = assoc.createRole(player, type);
        assertEquals(2, assoc.getRoles().size());
        int role1Sig = SignatureGenerator.generateSignature(role1);
        int role2Sig = SignatureGenerator.generateSignature(role2);
        assertTrue(role1Sig != role2Sig);
    }
}
