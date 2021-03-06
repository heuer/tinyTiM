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
package org.tinytim.internal.utils;

import java.util.Arrays;
import java.util.Collection;

import org.tinytim.internal.api.IConstruct;
import org.tinytim.internal.api.ILiteralAware;
import org.tinytim.internal.api.IScoped;

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
 * <p>
 * This class can be used to detect duplicates: If two Topic Maps constructs
 * have the same signature, they should be merged (if they belong to the same 
 * parent).
 * </p>
 * <p>
 * Neither the topic map, the parent, the reifier, nor item identifiers 
 * are taken into account.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class SignatureGenerator {

    private SignatureGenerator() {
        // noop.
    }

    public static int generateSignature(IConstruct construct) {
        if (construct.isAssociation()) {
            return generateSignature((Association) construct);
        }
        else if (construct.isOccurrence()) {
            return generateSignature((Occurrence) construct);
        }
        else if (construct.isName()) {
            return generateSignature((Name) construct);
        }
        else if (construct.isVariant()) {
            return generateSignature((Variant) construct);
        }
        else if (construct.isRole()) {
            return generateSignature((Role) construct);
        }
        throw new IllegalArgumentException("Only association, role, occurrence, name, and variant are supported");
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
    public static int generateSignature(Association assoc) {
        return Arrays.hashCode(new int[] {_generateTypeSignature(assoc),
                _generateScopeSignature(assoc),
                _generateRolesSignature(assoc.getRoles())});
    }

    private static int _generateRolesSignature(final Collection<Role> roles) {
        if (roles.isEmpty()) {
            return 0;
        }
        int[] ids = new int[roles.size()];
        int i = 0;
        for (Role role : roles) {
            ids[i++] = generateSignature(role); 
        }
        Arrays.sort(ids);
        return Arrays.hashCode(ids);
    }

    /**
     * Generates the signature of a role.
     *
     * @param role The role to generate the signature for.
     * @return The role's signature.
     */
    public static int generateSignature(final Role role) {
        return Arrays.hashCode(new int[] {
                _signature(role.getType()), _signature(role.getPlayer())
                });
    }

    /**
     * Generates the signature for an occurrence.
     *
     * @param occ The occurrence to create the signature for.
     * @return The signature of the occurrence.
     */
    public static int generateSignature(final Occurrence occ) {
        return Arrays.hashCode(new int[] {
                _generateTypeSignature(occ),
                _generateScopeSignature(occ),
                _generateDataSignature((ILiteralAware)occ)
            });
    }

    /**
     * Generates a signature for the specified <code>name</code>.
     * 
     * The parent and the variants are not taken into account.
     *
     * @param name The name to generate the signature for.
     * @return A signature for the name.
     */
    public static int generateSignature(final Name name) {
        return Arrays.hashCode(new int[] {
                _generateTypeSignature(name),
                _generateScopeSignature(name),
                _generateDataSignature((ILiteralAware)name)
            });
    }

    /**
     * Generates a signature for the specified <code>variant</code>.
     *
     * @param variant The variant to generate the signature for.
     * @return A signature for the variant.
     */
    public static int generateSignature(final Variant variant) {
        return Arrays.hashCode(new int[] {
                _generateScopeSignature(variant),
                _generateDataSignature((ILiteralAware)variant)
            });
    }

    /**
     * Returns a signature for a value/datatype pair.
     *
     * @param construct An occurrence or variant.
     * @return The signature.
     */
    private static int _generateDataSignature(final ILiteralAware construct) {
        return System.identityHashCode(construct.getLiteral());
    }

    /**
     * Returns a signature for the type of a typed Topic Maps construct.
     *
     * @param typed The typed Topic Maps construct.
     * @return The signature.
     */
    private static int _generateTypeSignature(final Typed typed) {
        return _signature(typed.getType());
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
    private static int _generateScopeSignature(final Scoped scoped) {
        return System.identityHashCode(((IScoped) scoped).getScopeObject());
    }

    private static int _signature(Topic topic) {
        return System.identityHashCode(topic);
    }

}
