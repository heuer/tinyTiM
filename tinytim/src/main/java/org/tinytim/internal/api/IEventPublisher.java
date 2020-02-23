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
 * Publisher for Topic Maps events.
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IEventPublisher {

    /**
     * Subscribes the handler for the specified event.
     *
     * @param event The event of interesst.
     * @param handler The event handler.
     */
    public void subscribe(Event event, IEventHandler handler);

    /**
     * Removes the handler from the publisher.
     *
     * @param event The event.
     * @param handler The event handler.
     */
    public void unsubscribe(Event event, IEventHandler handler);

}
