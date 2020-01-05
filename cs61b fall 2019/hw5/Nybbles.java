/** Represents an array of integers each in the range -8..7.
 *  Such integers may be represented in 4 bits (called nybbles).
 *  @author josiath
 */
public class Nybbles {

    /** Maximum positive value of a Nybble. */
    public static final int MAX_VALUE = 7;

    /** Return an array of size N. */
    public Nybbles(int N) {
        // DON'T CHANGE THIS.
        _data = new int[(N + 7) / 8];
        _n = N;
    }

    /** Return the size of THIS. */
    public int size() {
        return _n;
    }

    /** Return the Kth integer in THIS array, numbering from 0.
     *  Assumes 0 <= K < N. */
    public int get(int k) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else {
            int id = k / 8;
            id = _data[id];
            int h = k % 8;
            h = 4 * h;
            int n1 = (id & (1<<h))>>h;
            h++;
            int n2 = (id & (1<<h))>>h;
            h++;
            int n3 = (id & (1<<h))>>h;
            h++;
            int n4 = (id & (1<<h))>>h;
            n4 = Math.abs(n4);
            h = n4 *(-8) + n3 * 4 + n2 * 2 + n1;

            return h; // REPLACE WITH SOLUTION
        }
    }

    /** Set the Kth integer in THIS array to VAL.  Assumes
     *  0 <= K < N and -8 <= VAL < 8. */
    public void set(int k, int val) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else if (val < (-MAX_VALUE - 1) || val > MAX_VALUE) {
            throw new IllegalArgumentException();
        } else {
            int id = k / 8;
            int h = k % 8;
            h = h * 4;
            int n1 = 1<<h;
            h++;
            int n2 = 1<<h;
            h++;
            int n3 = 1<<h;
            h++;
            int n4 = 1<<h;
            int sum = n1 + n2 + n3 + n4;
            int get0 = -1 - sum;
            _data[id] = _data[id] & get0;
            if (val < 0) {
                if (h == 31) {
                    n4 = -(1<<h);
                } else {
                    n4 = 1 << h;
                }
                val = val + 8;
            } else {
                n4 = 0;
            }
            h = h - 3;
            if (h != 0) {
                n4 = (val << h) + n4;
            } else {
                n4 = val + n4;
            }
            _data[id] = _data[id] | n4;
        }
    }

    // DON'T CHANGE OR ADD TO THESE.
    /** Size of current array (in nybbles). */
    private int _n;
    /** The array data, packed 8 nybbles to an int. */
    private int[] _data;
}
