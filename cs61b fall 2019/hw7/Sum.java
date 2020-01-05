import java.util.HashMap;
import java.util.HashSet;

/** HW #7, Two-sum problem.
 * @author josaith
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        HashMap val = new HashMap();
        for(int i = 0; i < A.length; i++) {
            val.put(m-A[i],A[i]);
        }
        for (int i = 0; i < B.length; i++) {
            if (val.get(B[i]) != null) {
                return true;
            }
        }
        return false;
    }

}
