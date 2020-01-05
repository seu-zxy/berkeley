package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author josiath
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _pawls = pawls;
        _numRotors = numRotors;
        _allRotors = allRotors;
        _rotorArrange = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        HashMap<String, Rotor> source = new HashMap<String, Rotor>();
        for (Rotor temp : _allRotors) {
            source.put(temp.name(), temp);
        }
        int i = 0;
        for (i = 0; i < rotors.length; i++) {
            String tempName = rotors[i];
            if (source.containsKey(tempName)) {
                _rotorArrange[i] = source.get(tempName);
            } else {
                throw new EnigmaException("do not have this rotor");
            }
        }
        if (!_rotorArrange[0].reflecting()) {
            throw new EnigmaException("do not have a reflector");
        }
        int cp = 0;
        for (i = 0; i < _rotorArrange.length; i++) {
            if (_rotorArrange[i].rotates()) {
                cp++;
            }
        }
        if (cp != _pawls) {
            throw new EnigmaException("wrong numbers of pawls(moving rotors)");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("numbers of setting is wrong");
        } else {
            for (int i = 1; i < numRotors(); i++) {
                if (!_alphabet.contains(setting.charAt(i - 1))) {
                    throw new EnigmaException("setting not in the alphabet");
                }
                _rotorArrange[i].set(setting.charAt(i - 1));
            }
        }
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("wrong numbers of setting");
        }
    }
    /** change notches.
     * @param  setting is */
    void changeNotches(String setting) {
        int i = 0;
        for (i = 1; i < _rotorArrange.length; i++) {
            if (_rotorArrange[i].rotates()) {
                _rotorArrange[i].setNotch(setting.charAt(i - 1));
            }
        }
    }
    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        boolean[] willmove = new boolean[_numRotors];
        int i;
        int answer = c;
        answer = _plugboard.permute(answer);
        for (i = 0; i < _numRotors; i++) {
            willmove[i] = false;
        }
        willmove [_numRotors - 1] = true;
        for (i = _numRotors - 1; i > 0; i--) {
            if (_rotorArrange[i].atNotch() && _rotorArrange[i - 1].rotates()) {
                willmove[i] = true;
                willmove[i - 1] = true;
            } else {
                if (willmove[i]) {
                    willmove[i - 1] = false;
                }
            }
        }
        for (i = _numRotors - 1; i >= 0; i--) {
            if (willmove[i]) {
                _rotorArrange[i].advance();
            }
            answer = _rotorArrange[i].convertForward(answer);
        }

        for (i = 1; i < _numRotors; i++) {
            answer = _rotorArrange[i].convertBackward(answer);
        }
        answer = _plugboard.permute(answer);
        return answer;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        int length = msg.length();
        String res = "";
        int i = 0;
        for (i = 0; i < length; i++) {
            int temp = _alphabet.toInt(msg.charAt(i));
            if (temp == -1) {
                throw new EnigmaException("MESSAGE DO NOT EXIST IN _alphabet");
            }
            temp = convert(temp);
            char val = _alphabet.toChar(temp);
            res = res + val;
        }
        return res;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** pawls. */
    private int  _pawls;
    /** numRotors. */
    private int _numRotors;
    /** _allRotors. */
    private Collection<Rotor> _allRotors;
    /** rotorArrange. */
    private Rotor[] _rotorArrange;
    /** _plugboard. */
    private Permutation _plugboard;
}
