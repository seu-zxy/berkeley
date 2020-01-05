package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest{
    @Test
    public void naturalruntest()
    {
        int[] a = {1, 0, 4, 5, 6, 7, 3};
        int[][] b = {{1}, {0, 4, 5, 6, 7}, {3}};
        lists.IntListList L = lists.IntListList.list(b);
        lists.IntList P = lists.IntList.list(a);
        lists.IntListList w = lists.Lists.naturalRuns(P);
        assertEquals(L,w);
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
