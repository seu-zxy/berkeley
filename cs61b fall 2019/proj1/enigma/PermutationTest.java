package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkpermuate() {
        perm = new Permutation("(AWER)(CK)", UPPER);
        char t1 = perm.permute('A');
        char t2 = perm.permute('R');
        char t3 = perm.permute('W');
        char t4 = perm.permute('C');
        char t5 = perm.permute('K');
        char t6 = perm.permute('B');
        assertEquals('W', t1);
        assertEquals('A', t2);
        assertEquals('E', t3);
        assertEquals('K', t4);
        assertEquals('C', t5);
        assertEquals('B', t6);
    }

    @Test
    public void checkinvert() {
        perm = new Permutation("(AWER)(CK)", UPPER);
        char t1 = perm.invert('A');
        char t2 = perm.invert('W');
        char t3 = perm.invert('R');
        char t4 = perm.invert('C');
        char t5 = perm.invert('K');
        char t6 = perm.invert('B');
        assertEquals('R', t1);
        assertEquals('A', t2);
        assertEquals('E', t3);
        assertEquals('K', t4);
        assertEquals('C', t5);
        assertEquals('B', t6);
    }
}
