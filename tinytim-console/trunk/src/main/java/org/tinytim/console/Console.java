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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinytim.Version;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * Console to issue commands against a {@link org.tmapi.core.TopicMapSystem}.
 * <p>
 * Even if the implementation tries to use TMAPI as much as possible, the 
 * console needs tinyTiM and is not compatible to other TMAPI implementations. 
 * Adapting the code to pure TMAPI should be possible with some effort, though.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class Console {

    private static final Pattern _CL_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private static final String _VERSION = "0.1.0";

    private final TopicMapSystem _tmSys;
    private Map<String, ICommand> _commands;

    private InputStream _in = System.in;
    private PrintStream _out = System.out;
    private BufferedReader _reader;

    public static void  main(String[] args) {
        Console con = null;
        try {
            con = new Console();
            con.start();
        }
        catch (TMAPIException ex) {
            throw new RuntimeException(ex);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Console() throws TMAPIException {
        this(TopicMapSystemFactory.newInstance().newTopicMapSystem());
    }

    private Console(TopicMapSystem tmSys) {
        if (tmSys == null) {
            throw new IllegalArgumentException("The topic map system must not be null");
        }
        _tmSys = tmSys;
        _commands = new HashMap<String, ICommand>();
        _registerCommands();
    }

    private void _registerCommands() {
        _registerCommand(new CreateCommand());
        _registerCommand(new ShowCommand());
        _registerCommand(new ReadCommand());
        _registerCommand(new WriteCommand());
    }

    private void _registerCommand(ICommand cmd) {
        _commands.put(cmd.getName(), cmd);
    }

    public void start() throws IOException {
        _reader = new BufferedReader(new InputStreamReader(_in));
        _printBanner();
        boolean exit = false;
        while(!exit) {
            String command = _readInput();
            if (command == null) {
                break;
            }
            exit = _executeCommand(command);
        }
        _println("Thanks for using tinyTiM.");
        _println();
        _println("Bye.");
    }

    private boolean _executeCommand(String command) {
        String[] args = _parse(command);
        if (args.length == 0) {
            return false;
        }
        String op = args[0];
        if ("exit".equals(op)) {
            return true;
        }
        else if ("help".equals(op)) {
            List<String> list = new ArrayList<String>(_commands.keySet());
            Collections.sort(list);
            for (String name: list) {
                ICommand cmd = _commands.get(name);
                _print(cmd.getSytax());
                _print("\t");
                _println(cmd.getHelp());
            }
            _println("exit\t Closes the console");
            return false;
        }
        ICommand cmd = _commands.get(op);
        if (cmd == null) {
            _printError("Unknown command: '" + command + "'");
        }
        else {
            cmd.execute(args);
        }
        return false;
    }

    private String _readInput() throws IOException {
        _print(">>> ");
        String line = _reader.readLine();
        if (line == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(256);
        buf.append(line);
        while (line != null && !line.endsWith(".")) {
            _print("... ");
            line = _reader.readLine();
            buf.append('\n')
                .append(line);
        }
        // Remove command delimiter
        buf.setLength(buf.length() - 1);
        return buf.toString().trim();
    }

    private void _printBanner() {
        _println();
        _print("Console v");
        _print(_VERSION);
        _print(" using tinyTiM v");
        _println(Version.RELEASE);
        _println("Commands end with a '.'.");
        _println("Type 'help.' for more information. Use 'exit.' to leave the console.");
    }

    private void _print(String s) {
        _out.print(s);
    }

    private void _println() {
        _out.println();
    }

    private void _println(String s) {
        _out.println(s);
    }

    private void _printError(String msg) {
        _out.println("ERROR: " + msg);
    }

    private static String[] _parse(String command) {
        Matcher matcher = _CL_PATTERN.matcher(command);
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            }
            else {
                tokens.add(matcher.group());
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }


    /**
     * Creates a new topic map with the specified locator
     */
    private class CreateCommand extends AbstractCommand {

        public CreateCommand() {
            super("create <target:url>", 
                    "Creates a topic map under the specified location");
        }

        public void execute(String[] args) {
            if (args.length > 2) {
                _printError("Too many arguments");
                return;
            }
            else if (args.length < 2) {
                _printError("No IRI specified");
                return;
            }
            String iri = args[1];
            try {
                _tmSys.createTopicMap(iri);
            }
            catch (TopicMapExistsException ex) {
                _printError("A topic map with the IRI <" + iri + "> exists");
                return;
            }
            _println("<" + iri + "> created.");
        }
    }

    /**
     * Shows an overview about the topic maps available in the system.
     */
    private class ShowCommand extends AbstractCommand {

        public ShowCommand() {
            super("show", "Returns a list of topic maps");
        }

        public void execute(String[] args) {
            Collection<Locator> locs = _tmSys.getLocators();
            if (locs.isEmpty()) {
                _println("No topic maps available.");
            }
            else {
                List<String> list = new ArrayList<String>();
                for (Locator loc: locs) {
                    list.add(loc.toExternalForm());
                }
                Collections.sort(list);
                for (String loc: list) {
                    _println(loc);
                }
            }
        }
    }

    /**
     * Deserializes a topic map and adds the content to a local topic map.
     */
    private class ReadCommand extends AbstractCommand {

        public ReadCommand() {
            super("read <source: