package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    @Test
    public  void testcatenate(){
    	int[] A={1,2,4};
    	int[] B={2,4,2};
    	int[] C={1,2,4,2,4,2};
    	int[] temp;
    	temp= arrays.Arrays.catenate(A,B);
    	boolean k= arrays.Utils.equals(C,temp);
    	assertEquals(true,k);
    }
@Test
    public  void testremove(){
        int[] A={1,2,4,6,1,9};
        int[] C={1,2,4};
        int[] temp;
        temp= arrays.Arrays.remove(A,3,3);
        boolean k= arrays.Utils.equals(C,temp);
        assertEquals(true,k);
    }
@Test
    public  void testnatureruns(){
        int[] A={1,3,2,5,4,0};
        int[][] C={{1,3},{2,5},{4},{0}};
        int[][] temp;
        temp= arrays.Arrays.naturalRuns(A);
        boolean k= arrays.Utils.equals(C,temp);
        assertEquals(true,k);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
