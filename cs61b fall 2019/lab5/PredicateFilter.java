import java.util.Iterator;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import utils.Predicate;
import utils.Filter;

/** A kind of Filter that tests the elements of its input sequence of
 *  VALUES by applying a Predicate object to them.
 *  @author You
 */
class PredicateFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that tests them with PRED,
     *  delivering only those for which PRED is true. */
    PredicateFilter(Predicate<Value> pred, Iterator<Value> input) {
        super(input);
        rule = pred;
    }

    @Override
    protected boolean keep() {
        if (rule.test(_next)) {
        return true;
        } else {
            return false;
        }
    }

    private Predicate<Value> rule;

}


