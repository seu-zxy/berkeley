import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author josiath
 */
public class ECHashStringSetTest  {
    private static String[] DATA = new String[] {"wings", "assass", "blink", "yyfyyf", "shadow", "yaphets"};
    private ECHashStringSet hashSet = new ECHashStringSet();
    private ECHashStringSet emptySet= new ECHashStringSet();

    @Test
    public void testput() {
        int i = 0;
        for(String data : DATA){
            hashSet.put(data);
            i++;
            assertEquals(i,hashSet.size());
        }
    }

    @Test
    public void testcontain() {
        for(String data : DATA)
            hashSet.put(data);

        for(String data : DATA){
            assertEquals(false,emptySet.contains(data));
            assertEquals(true,hashSet.contains(data));
        }
    }
}
