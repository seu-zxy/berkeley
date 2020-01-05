package lists;

public  class Lists {
    /**
     * Return the list of lists formed by breaking up L into "natural runs":
     * that is, maximal strictly ascending sublists, in the same order as
     * the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     * then result is the four-item list
     * ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     * Destructive: creates no new IntList items, and may modify the
     * original list pointed to by L.
     */
    static IntListList naturalRuns(IntList L) {
        if (L == null)
            return null;
        else {
            boolean det = false;
            IntListList temp = new IntListList(L, null);
            IntListList w = temp;
            while (L.tail != null) {
                if (L.head < L.tail.head) {
                    det = false;
                    L = L.tail;
                } else
                    det = true;
                if (det == true) {
                    IntList temp_list = L.tail;
                    L.tail = null;
                    L = temp_list;
                    det = false;
                    w.tail = new IntListList(L, null);
                    w = w.tail;
                }
            }
            return temp;
        }
    }
}
