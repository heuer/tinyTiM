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
package org.tinytim.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.tinytim.core.Event;
import org.tinytim.core.IEventHandler;
import org.tinytim.core.IEventPublisher;
import org.tinytim.core.ILiteral;
import org.tinytim.core.ILiteralAware;
import org.tinytim.core.Literal;
import org.tinytim.utils.ICollectionFactory;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TopicMap;
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

    public LiteralIndexImpl(IEventPublisher publisher, ICollectionFactory collFactory) {
        super((TopicMap) publisher, collFactory);
        _lit2Names = collFactory.createIdentityMap();
        _lit2Occs = collFactory.createIdentityMap();
        _lit2Variants = collFactory.createIdentityMap();
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
                             : new ArrayList<Name>(names);
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
                            : new ArrayList<Occurrence>(occs);
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
                                : new ArrayList<Variant>(variants);
    }

    public void clear() {
        _lit2Names.clear();
        _lit2Occs.clear();
        _lit2Variants.clear();
    }

    private void _index(Map<ILiteral, List<ILiteralAware>> lit2LitAware, ILiteral lit, ILiteralAware litAware) {
        List<ILiteralAware> list = lit2LitAware.get(lit);
        if (list == null) {
            list = new ArrayList<ILiteralAware>();
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
        Map<ILiteral, List<ILiteralAware>> getMap(ILiteralAware typed) {
            Map<ILiteral, ?> type2Typed = null;
            if (typed instanceof Name) {
                type2Typed = _lit2Names;
            }
            else if (typed instanceof Occurrence) {
                type2Typed = _lit2Occs;
            }
            else if (typed instanceof Variant) {
                type2Typed = _lit2Variants;
            }
            return (Map<ILiteral, List<ILiteralAware>>) type2Typed;
        }
    }

    private final class LiteralHandler extends _EvtHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            ILiteralAware typed = (ILiteralAware) sender;
            Map<ILiteral, List<ILiteralAware>> map = getMap(typed);
            _unindex(map, (ILiteral) oldValue, typed);
            _index(map, (ILiteral) newValue, typed);
        }
    }

    private final class AddLiteralAwareHandler extends _EvtHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            ILiteralAware litAware = (ILiteralAware) newValue;
            Map<ILiteral, List<ILiteralAware>> map = getMap(litAware);
            _index(map, litAware.getLiteral(), litAware);
        }
        
    }

    private final class RemoveLiteralAwareHandler extends _EvtHandler {
        public void handleEvent(Event evt, Construct sender, Object oldValue,
                Object newValue) {
            ILiteralAware litAware = (ILiteralAware) oldValue;
            Map<ILiteral, List<ILiteralAware>> map = getMap(litAware);
            _unindex(map, litAware.getLiteral(), litAware);
        }
        
    }
}
