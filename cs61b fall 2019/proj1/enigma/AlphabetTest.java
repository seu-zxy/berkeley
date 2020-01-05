package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {
    String ch = "abcdefghijk";
    Alphabet test = new Alphabet(ch);
    Alphabet test2 = new Alphabet();

    @Test
    public void testsize() {
        assertEquals(11, test.size());
        assertEquals(26, test2.size());
    }
    @Test
    public void testcontains() {
        assertEquals(true, test.contains('b'));
        assertEquals(false, test.contains('z'));
    }
    @Test
    public void testtochar() {
        assertEquals('a', test.toChar(0));
        assertEquals('h', test.toChar(7));
    }
    @Test
    public void testtoint() {
        assertEquals(0, test.toInt('a'));
        assertEquals(7, test.toInt('h'));
    }
}
