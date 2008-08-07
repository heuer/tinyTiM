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

import org.tmapi.core.Construct;

/**
 * Event handler that is able to handle Topic Maps events.
 * 
 * This interface is not meant to be used outside of the tinyTiM package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IEventHandler {

    /**
     * Callback method if a {@link IEventPublisher} sends an event to which
     * this handler is subscribed to.
     *
     * @param evt The event.
     * @param sender The sender of the event (this is not necessarily the
     *               publisher).
     * @param oldValue The old value or <code>null</code> if the old value
     *                 is not available or was <code>null</code>.
     * @param newValue The new value or <code>null</code> if the new value
     *                 is not available or should become <code>null</code>.
     */
    public void handleEvent(Event evt, Construct sender, Object oldValue, Object newValue);

}
