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
package org.tinytim.voc;

import org.tmapi.core.Locator;

/**
 * Provides PSIs for the XML Schema Datatypes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public final class XSD extends Vocabulary {

    private XSD() {
        // noop.
    }

    private static final String _BASE = Namespace.XSD;

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anyType">xsd:anyType</a>
     * datatype.
     */
    public static final Locator ANY_TYPE        = _createLocator(_BASE+"anyType");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anySimpleType">xsd:anySimpleType</a>
     * datatype.
     */
    public static final Locator ANY_SIMPLE_TYPE = _createLocator(_BASE+"anySimpleType");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#duration">xsd:duration</a>
     * datatype.
     */
    public static final Locator DURATION        = _createLocator(_BASE+"duration");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#dateTime">xsd:dateTime</a>
     * datatype.
     */
    public static final Locator DATE_TIME       = _createLocator(_BASE+"dateTime");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#time">xsd:time</a>
     * datatype.
     */
    public static final Locator TIME            = _createLocator(_BASE+"time");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#date">xsd:date</a>
     * datatype.
     */
    public static final Locator DATE            = _createLocator(_BASE+"date");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gYearMonth">xsd:gYearMonth</a>
     * datatype.
     */
    public static final Locator G_YEAR_MONTH    = _createLocator(_BASE+"gYearMonth");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gYear">xsd:gYear</a>
     * datatype.
     */
    public static final Locator G_YEAR          = _createLocator(_BASE+"gYear");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gMonthDay">xsd:gMonthDay</a>
     * datatype.
     */
    public static final Locator G_MONTH_DAY     = _createLocator(_BASE+"gMonthDay");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gDay">xsd:gDay</a>
     * datatype.
     */
    public static final Locator G_DAY           = _createLocator(_BASE+"gDay");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gMonth">xsd:gMonth</a>
     * datatype.
     */
    public static final Locator G_MONTH         = _createLocator(_BASE+"gMonth");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#boolean">xsd:boolean</a>
     * datatype.
     */
    public static final Locator BOOLEAN         = _createLocator(_BASE+"boolean");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#base64Binary">xsd:base64Binary</a>
     * datatype.
     */
    public static final Locator BASE64_BINARY   = _createLocator(_BASE+"base64Binary");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#hexBinary">xsd:hexBinary</a>
     * datatype.
     */
    public static final Locator HEX_BINARY      = _createLocator(_BASE+"hexBinary");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#float">xsd:float</a>
     * datatype.
     */
    public static final Locator FLOAT           = _createLocator(_BASE+"float");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#double">xsd:double</a>
     * datatype.
     */
    public static final Locator DOUBLE          = _createLocator(_BASE+"double");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anyURI">xsd:anyURI</a>
     * datatype.
     */
    public static final Locator ANY_URI         = _createLocator(_BASE+"anyURI");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#QName">xsd:QName</a>
     * datatype.
     */
    public static final Locator QNAME           = _createLocator(_BASE+"QName");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NOTATION">xsd:NOTATION</a>
     * datatype.
     */
    public static final Locator NOTATION        = _createLocator(_BASE+"notation");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#string">xsd:string</a>
     * datatype.
     */
    public static final Locator STRING          = _createLocator(_BASE+"string");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#decimal">xsd:decimal</a>
     * datatype.
     */
    public static final Locator DECIMAL         = _createLocator(_BASE+"decimal");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#normalizedString">xsd:normalizedString</a>
     * datatype.
     */
    public static final Locator NORMALIZED_STRING = _createLocator(_BASE+"normalizedString");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#integer">xsd:integer</a>
     * datatype.
     */
    public static final Locator INTEGER         = _createLocator(_BASE+"integer");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#token">xsd:token</a>
     * datatype.
     */
    public static final Locator TOKEN           = _createLocator(_BASE+"token");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#nonPositiveInteger">xsd:nonPositiveInteger</a>
     * datatype.
     */
    public static final Locator NON_POSITIVE_INTEGER = _createLocator(_BASE+"nonPositiveInteger");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#long">xsd:long</a>
     * datatype.
     */
    public static final Locator LONG            = _createLocator(_BASE+"long");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">xsd:nonNegativeInteger</a>
     * datatype.
     */
    public static final Locator NON_NEGATIVE_INTEGER = _createLocator(_BASE+"nonNegativeInteger");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#language">xsd:language</a>
     * datatype.
     */
    public static final Locator LANGUAGE        = _createLocator(_BASE+"language");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#Name">xsd:Name</a>
     * datatype.
     */
    public static final Locator NAME            = _createLocator(_BASE+"Name");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NMTOKEN">xsd:NMTOKEN</a>
     * datatype.
     */
    public static final Locator NMTOKEN         = _createLocator(_BASE+"NMTOKEN");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#negativeInteger">xsd:negativeInteger</a>
     * datatype.
     */
    public static final Locator NEGATIVE_INTEGER = _createLocator(_BASE+"negativeInteger");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#int">xsd:int</a>
     * datatype.
     */
    public static final Locator INT             = _createLocator(_BASE+"int");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedLong">xsd:unsignedLong</a>
     * datatype.
     */
    public static final Locator UNSIGNED_LONG   = _createLocator(_BASE+"unsignedLong");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#positiveInteger">xsd:positiveInteger</a>
     * datatype.
     */
    public static final Locator POSITIVE_INTEGER = _createLocator(_BASE+"positiveInteger");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NCName">xsd:NCName</a>
     * datatype.
     */
    public static final Locator NCNAME          = _createLocator(_BASE+"NCName");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NMTOKEN">xsd:NMTOKEN</a>
     * datatype.
     */
    public static final Locator NMTOKENS        = _createLocator(_BASE+"NMTOKEN");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#short">xsd:short</a>
     * datatype.
     */
    public static final Locator SHORT           = _createLocator(_BASE+"short");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedInt">xsd:unsignedInt</a>
     * datatype.
     */
    public static final Locator UNSIGNED_INT    = _createLocator(_BASE+"unsignedInt");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ID">xsd:ID</a>
     * datatype.
     */
    public static final Locator ID              = _createLocator(_BASE+"ID");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#IDREF">xsd:IDREF</a>
     * datatype.
     */
    public static final Locator IDREF           = _createLocator(_BASE+"IDREF");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ENTITY">xsd:ENTITY</a>
     * datatype.
     */
    public static final Locator ENTITY          = _createLocator(_BASE+"ENTITY");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#String">xsd:byte</a>
     * datatype.
     */
    public static final Locator BYTE            = _createLocator(_BASE+"byte");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedShort">xsd:unsignedShort</a>
     * datatype.
     */
    public static final Locator UNSIGNED_SHORT  = _createLocator(_BASE+"unsignedShort");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#IDREFS">xsd:IDREFS</a>
     * datatype.
     */
    public static final Locator IDREFS          = _createLocator(_BASE+"IDREFS");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ENTITIES">xsd:ENTITIES</a>
     * datatype.
     */
    public static final Locator ENTITIES        = _createLocator(_BASE+"ENTITIES");

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedByte">xsd:unsignedByte</a>
     * datatype.
     */
    public static final Locator UNSIGNED_BYTE   = _createLocator(_BASE+"unsignedByte");
}
