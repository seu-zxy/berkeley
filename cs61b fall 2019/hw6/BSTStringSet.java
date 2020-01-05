import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author josiath
 */
public class BSTStringSet implements SortedStringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        if (_root == null) {
            _root = new Node(s);
        } else {
            insert(_root, s);
        }
    }

    public Node insert(Node T, String L) {
        if (T == null) {
            return new Node(L);
        }
        if (L.compareTo(T.s) < 0) {
            T.left = insert(T.left,L);
        }
        if (L.compareTo(T.s) > 0) {
            T.right = insert(T.right,L);
        }
        return T;
    }

    //question return's valuse or address
    @Override
    public boolean contains(String s) {
        Node T = _root;
        boolean val = false;
        while (T !=null) {
            if (s.compareTo(T.s) == 0) {
                val = true;
                break;
            }
            if (s.compareTo(T.s) < 0) {
                T= T.left;
            } else {
                T = T.right;
            }
        }
        return val;
    }

    @Override
    public List<String> asList() {
        List<String> T = new ArrayList<String>();
        if (_root == null) {
            return T;
        }
        getList(_root,T);
        return T;
    }

    public void getList(Node H, List k) {
        if (H.left == null && H.right == null) {
            k.add(H);
        } else {
            if (H.left != null) {
                getList(H.left, k);
            }
            if (H.left != null || H.right != null) {
                k.add(H);
            }
            if (H.right != null) {
                getList(H.right, k);
            }
        }
    }

    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null ) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    @Override
    public Iterator<String> iterator(String low, String high) {
        return new RangIterator(_root,low,high);

    }

   private static class RangIterator implements Iterator<String> {
        public Stack<Node> _toDo = new Stack<>();
        public String L;
        public String U;
       @Override
       public boolean hasNext() {
           return (!_toDo.empty() && _toDo.peek().s.compareTo(U) < 0);
       }
       @Override
       public String next() {
           if (!hasNext()) {
               throw new NoSuchElementException();
           }
           Node temp = _toDo.pop();
           String kk = temp.s;
           addTree(temp.right);
           while (!(L.compareTo(kk) <= 0 && U.compareTo(kk) > 0)) {
               if (!hasNext()) {
                   throw new NoSuchElementException();
               }
               temp = _toDo.pop();
               kk = temp.s;
               addTree(temp.right);
           }
           return kk;
       }
       RangIterator(Node t, String l, String h) {
           this.L = l;
           this.U = h;
           addTree(t);
       }
       private void addTree(Node node ) {
           while (node != null) {
               _toDo.push(node);
               if (L.compareTo(node.s) >= 0) {
                   break;
               }
               node = node.left;
           }
       }
   }


    /** Root node of the tree. */
    private Node _root;
}
