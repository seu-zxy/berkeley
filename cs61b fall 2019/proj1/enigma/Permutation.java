package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author josiath
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int id = wrap(p);
        char ch = _alphabet.toChar(id);
        ch = permute(ch);
        id = _alphabet.toInt(ch);
        return id;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        char ch = _alphabet.toChar(c);
        ch = invert(ch);
        int id = _alphabet.toInt(ch);
        return id;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int i = 0;
        int num = -1;
        int j = 0;
        for  (i = 0; i < _cycles.length() - 1; i++) {
            if (_cycles.charAt(i) == '(') {
                j = i;
            }
            if (_cycles.charAt(i) == p && _cycles.charAt(i + 1) != ')') {
                num = i + 1;
                break;
            }
            if (_cycles.charAt(i) == p && _cycles.charAt(i + 1) == ')') {
                num = j + 1;
                break;
            }

        }
        if (num == -1) {
            return p;
        } else {
            return _cycles.charAt(num);
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int i = _cycles.length() - 1;
        int num = -2;
        int j = 0;
        for (i = _cycles.length() - 1; i > 0; i--) {
            if (_cycles.charAt(i) == ')') {
                j = i;
            }
            if (_cycles.charAt(i) == c && _cycles.charAt(i - 1) != '(') {
                num = i - 1;
                break;
            }
            if (_cycles.charAt(i) == c && _cycles.charAt(i - 1) == '(') {
                num = j - 1;
                break;
            }
        }
        if (num == -2) {
            return c;
        } else {
            return _cycles.charAt(num);
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int i = 0;
        for (i = 0; i < _alphabet.size(); i++) {
            char temp = _alphabet.toChar(i);
            if (permute(temp) == temp) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /**cycles of this class. */
    private String _cycles;
}
