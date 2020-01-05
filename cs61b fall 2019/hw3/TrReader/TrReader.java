import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author josiath
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    private Reader material;
    private String _from;
    private String _to;
    public TrReader(Reader str, String from, String to) {
        int l1 = from.length();
        int l2 = to.length();
        assert (l1 == l2);
        material = str;
        _from = from;
        _to = to;

    }

    public int read (char[] cbuf, int off, int len)throws IOException {
        int sit = material.read(cbuf, off, len);
        int i = 0;
        for (i = 0; i < len; i++){
            cbuf[i + off] = change(cbuf[i + off]);
        }
        if (sit < 0) {
            return -1;
        } else {
            return i;
        }
    }


    public void close() throws IOException {
        material.close();
    }

    private char change (char in) {
        int sequence_num = _from.indexOf(in);
        if (sequence_num < 0){
            return in;
        } else {
            return _to.charAt(sequence_num);
        }
    }


}
