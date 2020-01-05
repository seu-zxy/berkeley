import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Your Name
 */
class AlternatingFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that lets through every other
     *  value. */
    AlternatingFilter(Iterator<Value> input) {
        super(input);
        seq = 0;
    }

    @Override
    protected boolean keep() {
        if (seq % 2 == 0) {
            seq ++;
            return true;
        } else {
            seq ++;
            return false;
        }
    }

   private int seq;
}