import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author josiath
 */
public class BSTStringSetTest  {
    BSTStringSet k = new BSTStringSet();
    @Test
    public void testNothing() {
        k.put("shadow");
        k.put("wings");
        k.put("assass");
        k.put("assass");
        k.put("scccc");
        k.put("maybe");
        List h = k.asList();
        assertEquals(true,k.contains("shadow"));
        assertEquals(false,k.contains("nmb"));
    }
}
