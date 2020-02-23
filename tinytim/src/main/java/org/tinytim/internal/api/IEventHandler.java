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
package org.tinytim.internal.api;

/**
 * Event handler that is able to handle Topic Maps events.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
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
    public void handleEvent(Event evt, IConstruct sender, Object oldValue, Object newValue);

}
