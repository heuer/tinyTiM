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
package org.tinytim.console;


/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class AbstractCommand implements ICommand {

    protected final String _syntax;
    protected final String _name;
    protected final String _help;

    protected AbstractCommand(String syntax) {
        this(syntax, null);
    }

    protected AbstractCommand(String syntax, String help) {
        _syntax = syntax;
        String[] tmp = syntax.split(" ");
        _name = tmp[0];
        _help = help == null ? "" : help;
    }

    /* (non-Javadoc)
     * @see org.tinytim.console.ICommand#getHelp()
     */
    public String getHelp() {
        return _help;
    }

    /* (non-Javadoc)
     * @see org.tinytim.console.ICommand#getName()
     */
    public String getName() {
        return _name;
    }

    /* (non-Javadoc)
     * @see org.tinytim.console.ICommand#getSytax()
     */
    public String getSytax() {
        return _syntax;
    }

}
