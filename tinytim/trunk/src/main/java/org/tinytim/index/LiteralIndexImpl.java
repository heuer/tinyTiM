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
package org.tinytim.index;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.tinytim.core.Event;
import org.tinytim.core.IConstruct;
import org.tinytim.core.IEventHandler;
import org.tinytim.core.IEventPublisher;
import org.tinytim.core.ILiteral;
import org.tinytim.core.ILiteralAware;
import org.tinytim.core.Literal;
import org.tinytim.internal.utils.CollectionFactory;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class LiteralIndexImpl extends AbstractIndex implements LiteralIndex {

    private Map<ILiteral, List<Name>> _lit2Names;
    private Map<ILiteral, List<Occurrence>> _lit2Occs;
    private Map<ILiteral, List<Variant>> _lit2Variants;

    public LiteralIndexImpl(IEventPublisher publisher) {
        super();
        _lit2Names = CollectionFactory.createIdentityMap();
        _lit2Occs = CollectionFactory.createIdentityMap();
        _lit2Variants = CollectionFactory.createIdentityMap();
        publisher.subscribe(Event.SET_LITERAL, new LiteralHandler());
        IEventHandler handler = new AddLiteralAwareHandler();
        publisher.subscribe(Event.ADD_OCCURRENCE, handler);
        publisher.subscribe(Event.ADD_NAME, handler);
        publisher.subscribe(Event.ADD_VARIANT, handler);
        handler = new RemoveLiteralAwareHandler();
        publisher.subscribe(Event.REMOVE_OCCURRENCE, handler);
        publisher.subscribe(Event.REMOVE_NAME, handler);
        publisher.subscribe(Event.REMOVE_VARIANT, handler);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getNames(java.lang.String)
     */
    public Collection<Name> getNames(String value) {
        return _getNames(Literal.get(value));
    }

    private Collection<Name> _getNames(ILiteral literal) {
        if (literal == null) {
            return Collections.<Name>emptySet();
        }
        Collection<Name> names = _lit2Names.get(literal);
        return names == null ? Collections.<Name>emptySet()
                             : CollectionFactory.createList(names);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String)
     */
    public Collection<Occurrence> getOccurrences(String value) {
        return _getOccurrences(Literal.get(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(org.tmapi.core.Locator)
     */
    public Collection<Occurrence> getOccurrences(Locator value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _getOccurrences((ILiteral) value);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String, org.tmapi.core.Locator)
     */
    public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
        return _getOccurrences(Literal.get(value, datatype));
    }

    private Collection<Occurrence> _getOccurrences(ILiteral literal) {
        if (literal == null) {
            return Collections.<Occurrence>emptySet();
        }
        Collection<Occurrence> occs = _lit2Occs.get(literal);
        return occs == null ? Collections.<Occurrence>emptySet()
                            : CollectionFactory.createList(occs);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String)
     */
    public Collection<Variant> getVariants(String value) {
        return _getVariants(Literal.get(value));
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(org.tmapi.core.Locator)
     */
    public Collection<Variant> getVariants(Locator value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        return _getVariants((ILiteral) value);
    }

    /* (non-Javadoc)
     * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String, org.tmapi.core.Locator)
     */
    public Collection<Variant> getVariants(String value, Locator datatype) {
        return _getVariants(Literal.get(value, datatype));
    }

    private Collection<Variant> _getVariants(ILiteral literal) {
        if (literal == null) {
            return Collections.<Variant>emptySet();
        }
        Collection<Variant> variants = _lit2Variants.get(literal);
        return variants == null ? Collections.<Variant>emptySet()
                                : CollectionFactory.createList(variants);
    }

    public void clear() {
        _lit2Names.clear();
        _lit2Occs.clear();
        _lit2Variants.clear();
    }

    private void _index(Map<ILiteral, List<ILiteralAware>> lit2LitAware, ILiteral lit, ILiteralAware litAware) {
        List<ILiteralAware> list = lit2LitAware.get(lit);
        if (list == null) {
            list = CollectionFactory.createList();
            lit2LitAware.put(lit, list);
        }
        list.add(litAware);
    }

    private void _unindex(Map<ILiteral, List<ILiteralAware>> type2Typed, ILiteral type, ILiteralAware typed) {
        List<ILiteralAware> list = type2Typed.get(type);
        if (list == null) {
            return;
        }
        list.remove(typed);
        if (list.isEmpty()) {
            type2Typed.remove(type);
        }
    }

    private abstract class _EvtHandler implements IEventHandler {
        @SuppressWarnings("unchecked")
        Map<ILiteral, List<ILiteralAware>> getMap(IConstruct c) {
            Map<ILiteral, ?> lit2LitAware = null;
            if (c.isName()) {
                lit2LitAware = _lit2Names;
            }
            else if (c.isOccurrence()) {
                lit2LitAware = _lit2Occs;
            }
            else if (c.isVariant()) {
                lit2LitAware = _lit2Variants;
            }
            return (Map<ILiteral, List<ILiteralAware>>) lit2LitAware;
        }
    }

    private final class LiteralHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ILiteralAware typed = (ILiteralAware) sender;
            Map<ILiteral, List<ILiteralAware>> map = getMap(sender);
            _unindex(map, (ILiteral) oldValue, typed);
            _index(map, (ILiteral) newValue, typed);
        }
    }

    private final class AddLiteralAwareHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ILiteralAware litAware = (ILiteralAware) newValue;
            Map<ILiteral, List<ILiteralAware>> map = getMap((IConstruct) newValue);
            _index(map, litAware.getLiteral(), litAware);
        }
        
    }

    private final class RemoveLiteralAwareHandler extends _EvtHandler {
        public void handleEvent(Event evt, IConstruct sender, Object oldValue,
                Object newValue) {
            ILiteralAware litAware = (ILiteralAware) oldValue;
            Map<ILiteral, List<ILiteralAware>> map = getMap((IConstruct)oldValue);
            _unindex(map, litAware.getLiteral(), litAware);
        }
        
    }
}
