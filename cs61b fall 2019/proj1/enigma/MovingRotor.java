package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author josiath
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _permutation = perm;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            int temp = alphabet().toInt(_notches.charAt(i));
            if (temp == this.setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }


    @Override
    void advance() {
        int temp = _permutation.wrap(this.setting() + 1);
        set(temp);
    }

    @Override
    void setNotch(char kch) {
        int k = alphabet().toInt(kch);
        String newnotch = "";
        for (int i = 0; i < _notches.length(); i++) {
            int temp = alphabet().toInt(_notches.charAt(i));
            temp = temp - k;
            temp = temp + alphabet().size() * 3;
            temp = temp % alphabet().size();
            newnotch += alphabet().toChar(temp);
        }
        _notches = newnotch;
    }
    @Override
    String notches() {
        return _notches;
    }
    /** notches. */
    private String  _notches;
    /** My name. */
    private String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    /** setting is its setting. */
}
