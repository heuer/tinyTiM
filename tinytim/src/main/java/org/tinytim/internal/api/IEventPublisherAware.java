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
 * Something that subscribes and unsubscribes itself to an 
 * {org.tinytim.core.IEventPublisher}.
 * <p>
 * Implementations MUST have a default (public) constructor.
 * </p>
 * <p>
 * This interface is not meant to be used outside of the tinyTiM package.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IEventPublisherAware {

    /**
     * Subscribes this instance to the specified <tt>publisher</tt>.
     *
     * @param publisher An event publisher.
     */
    public void subscribe(IEventPublisher publisher);

    /**
     * Unsubscribes this instance from the specified <tt>publisher</tt>.
     * <p>
     * This method is only invoked if this instance has been subscribed to 
     * the <tt>publisher</tt>.
     * </p>
     *
     * @param publisher The publisher to unsubscribe from.
     */
    public void unsubscribe(IEventPublisher publisher);

}
