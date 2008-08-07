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

import java.util.Arrays;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

/**
 * Generates signatures for Topic Maps constructs.
 * 
 * This class can be used to detect duplicates: If two Topic Maps constructs
 * have the same signature, they should be merged (if they belong to the same 
 * parent).
 * 
 * Neither the topic map, the parent, the reifier, nor item identifiers 
 * are taken into account.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class SignatureGenerator {

    private SignatureGenerator() {
        // noop.
    }

    /**
     * Returns the signature of an association.
     * 
     * Beside of the type and scope of the association, the roles are
     * taken into account. 
     * The parent is not taken into account.
     *
     * @param assoc The association to generate the signature for.
     * @return The association's signature.
     */
    public static String generateSignature(Association assoc) {
        StringBuilder sb = new StringBuilder();
        sb.append(_generateTypeSignature(assoc))
            .append('s')
            .append(_generateScopeSignature(assoc))
            .append('.');
        Set<Role> roles = assoc.getRoles();
        String[] roleSigs = new String[roles.size()];
        int i = 0;
        for (Role role : roles) {
            roleSigs[i++] = generateSignature(role);
        }
        Arrays.sort(roleSigs);
        for (String sig : roleSigs) {
            sb.append(sig)
                .append('.');
        }
        return sb.toString();
    }

    /**
     * Generates the signature of a role.
     *
     * @param role The role to generate the signature for.
     * @return The role's signature.
     */
    public static String generateSignature(Role role) {
        StringBuilder sb = new StringBuilder();
        sb.append(_generateTypeSignature(role))
            .append('p')
            .append(role.getPlayer() == null ? "" : role.getPlayer().getId());
        return sb.toString();
    }

    /**
     * Generates the signature for an occurrence.
     *
     * @param occ The occurrence to create the signature for.
     * @return The signature of the occurrence.
     */
    public static String generateSignature(Occurrence occ) {
        StringBuilder sb = new StringBuilder();
        sb.append(_generateTypeSignature(occ))
            .append('s')
            .append(_generateScopeSignature(occ))
            .append('v')
            .append(_generateDataSignature((ILiteralAware) occ));
        return sb.toString();
    }

    /**
     * Generates a signature for the specified <code>name</code>.
     * 
     * The parent and the variants are not taken into account.
     *
     * @param name The name to generate the signature for.
     * @return A signature for the name.
     */
    public static String generateSignature(Name name) {
        StringBuilder sb = new StringBuilder();
        sb.append(_generateTypeSignature(name))
            .append('s')
            .append(_generateScopeSignature(name))
            .append('v')
            .append(_generateDataSignature((ILiteralAware) name));
        return sb.toString();
    }

    /**
     * Generates a signature for the specified <code>variant</code>.
     *
     * @param variant The variant to generate the signature for.
     * @return A signature for the variant.
     */
    public static String generateSignature(Variant variant) {
        StringBuilder sb = new StringBuilder();
        sb.append(_generateScopeSignature(variant))
            .append('v')
            .append(_generateDataSignature((ILiteralAware)variant));
        return sb.toString();
    }

    /**
     * Returns a signature for a value/datatype pair.
     *
     * @param construct An occurrence or variant.
     * @return The signature.
     */
    private static int _generateDataSignature(ILiteralAware construct) {
        return System.identityHashCode(construct.getLiteral());
    }

    /**
     * Returns a signature for the type of a typed Topic Maps construct.
     *
     * @param typed The typed Topic Maps construct.
     * @return The signature.
     */
    private static String _generateTypeSignature(Typed typed) {
        Topic type = typed.getType();
        return type == null ? "" : type.getId();
    }

    /**
     * Returns a signature for the scope of a scoped Topic Maps construct.
     * 
     * This function returns the signature for the scope, only! No other 
     * properties of the scoped Topic Maps construct are taken into account!
     *
     * @param scoped The scoped Topic Maps construct.
     * @return The signature.
     */
    private static String _generateScopeSignature(Scoped scoped) {
        Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return "";
        }
        String[] ids = new String[scope.size()];
        int i = 0;
        for (Topic topic : scope) {
            ids[i++] = topic.getId(); 
        }
        Arrays.sort(ids);
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            sb.append(id)
                .append('.');
        }
        return sb.toString();
    }

}
