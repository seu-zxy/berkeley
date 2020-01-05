package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author josiath
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enig = readConfig();
        ArrayList<Rotor> temp4 = copy(_allRotors);
        String setting = "";
        if (_input.hasNextLine()) {
            setting = _input.nextLine();
        } else {
            throw new EnigmaException("no input");
        }
        if (!setting.contains("*")) {
            throw new EnigmaException("wrong format for input first line");
        }
        setUp(enig, setting);
        String lineMessage = "";
        String outcome = "";
        while (_input.hasNextLine()) {
            lineMessage = _input.nextLine();
            if (lineMessage.isEmpty()) {
                _output.println();
            } else if (lineMessage.contains("*")) {
                _allRotors = copy(temp4);
                enig = new Machine(_alphabet, numRotors, pawls, _allRotors);
                setting = lineMessage;
                setUp(enig, setting);
            } else {
                lineMessage = lineMessage.replaceAll(" ", "");
                outcome = enig.convert(lineMessage);
                printMessageLine(outcome);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String table = _config.next();
            if (table.contains("(") || table.contains(")")) {
                throw new EnigmaException("wrong config board");
            }
            if (table.contains("*")) {
                throw new EnigmaException("wrong config board format");
            }
            _alphabet = new Alphabet(table);
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw new EnigmaException("wrong config for numrOTORS");
            }
            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
            } else {
                throw new EnigmaException("wrong config for pawls");
            }
            if (pawls >= numRotors) {
                throw new EnigmaException("wrong number of rotors and pawls");
            }
            temp = _config.next();
            while (_config.hasNext()) {
                Rotor temp1 = readRotor();
                _allRotors.add(temp1);
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }
    /** copy _allrotors since there may change it.
     * @return val
     * @param inn  like _allrotpr.*/
    private ArrayList<Rotor> copy(ArrayList<Rotor> inn) {
        ArrayList<Rotor> val = new ArrayList<Rotor>();
        for (int i = 0; i < inn.size(); i++) {
            Rotor orign = inn.get(i);
            Rotor temp2;
            if (orign.rotates()) {
                String name2 = orign.name();
                Permutation p2 = orign.permutation();
                temp2 = new MovingRotor(name2, p2, orign.notches());
            } else if (orign.reflecting()) {
                temp2 = new Reflector(orign.name(), orign.permutation());
            } else {
                temp2 = new FixedRotor(orign.name(), orign.permutation());
            }
            val.add(temp2);
        }
        return val;
    }
    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String perm = "";
            String name = temp;
            String nosty = _config.next();
            temp = _config.next();
            while (temp.contains("(")) {
                if (temp.contains(")")) {
                    perm = perm + temp;
                    if (_config.hasNext()) {
                        temp = _config.next();
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            char style = nosty.charAt(0);
            nosty = nosty.substring(1);
            if (style == 'M') {
                Permutation rr = new Permutation(perm, _alphabet);
                return new MovingRotor(name, rr, nosty);
            }
            if (style == 'N') {
                return new FixedRotor(name, new Permutation(perm, _alphabet));
            }
            if (style == 'R') {
                return new Reflector(name, new Permutation(perm, _alphabet));
            }
            throw new EnigmaException("wrong config about rotor");
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int i = 0;
        int agnum = 0;
        String[] all = settings.split(" ");
        if (!all[0].equals("*")) {
            throw new EnigmaException("wrong format for setting");
        }
        for (i = 1; i < all.length; i++) {
            if (!all[i].contains("(")) {
                agnum++;
            }
        }
        if (agnum != numRotors + 1 && agnum != numRotors + 2) {
            throw new EnigmaException("wrong numbers of setting argument");
        }
        String[] rotorName = new String[M.numRotors()];
        for (i = 1; i < M.numRotors() + 1; i++) {
            rotorName[i - 1] = all[i];
        }
        for (i = 0; i < rotorName.length - 1; i++) {
            for (int j = i + 1; j < rotorName.length; j++) {
                if (rotorName[i].equals(rotorName[j])) {
                    throw new EnigmaException("setting have 2 same rotors");
                }
            }
        }
        M.insertRotors(rotorName);
        if (agnum == numRotors + 2) {
            String nortchs = all[M.numRotors() + 2];
            if (nortchs.length() != M.numRotors() - 1) {
                throw new EnigmaException("wrong number rotor setting");
            }
            String realSetting = "";
            for (i = 0; i < nortchs.length(); i++) {
                int nring = _alphabet.toInt(nortchs.charAt(i));
                int ktem = _alphabet.toInt(all[M.numRotors() + 1].charAt(i));
                nring = ktem - nring;
                nring = nring % _alphabet.size();
                if (nring < 0) {
                    nring = nring + _alphabet.size();
                }
                char nch = _alphabet.toChar(nring);
                realSetting += nch;
            }
            M.setRotors(realSetting);
            M.changeNotches(nortchs);
        } else {
            M.setRotors(all[M.numRotors() + 1]);
        }
        String cycle = "";
        for (i = agnum + 1; i < all.length; i++) {
            if (!all[i].contains("(")) {
                throw new EnigmaException("wrong setting for plugboard");
            }
            cycle = cycle + all[i];
        }
        _plugboard = new Permutation(cycle, _alphabet);
        M.setPlugboard(_plugboard);

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int i = 0;
        int left;
        for (i = 0; i < msg.length(); i += 5) {
            left = msg.length() - i;
            if (left <= 5) {
                _output.println(msg.substring(i, msg.length()));
            } else {
                String change = msg.substring(i, i + 5);
                _output.print(msg.substring(i, i + 5) + " ");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** record _config scanner. */
    private String temp;
    /** allRotors. */
    private ArrayList<Rotor> _allRotors = new ArrayList<>();
    /** the num of Rotors. */
    private int numRotors;
    /** pawls. */
    private int pawls;
    /** plug board from input. */
    private Permutation _plugboard;
}
