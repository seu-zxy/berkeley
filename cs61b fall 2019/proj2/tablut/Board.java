package tablut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.List;
import java.util.Formatter;
import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;

/** The state of a Tablut Game.
 *  @author josiath
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _turn = model._turn;
        _winner = model._winner;
        _moveCount = model._moveCount;
        _repeated = model._repeated;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                _board[i][j] = model._board[i][j];
            }
        }
        repset = new HashSet<>(model.repset);
        _unnum = new Stack<Integer>();
        _unnum.addAll(model._unnum);
        rep = new Repesave(_board, _turn);
        _unsaveP = new Stack<Piece>();
        _unsaveP.addAll(model._unsaveP);
        _unsaveS = new Stack<Square>();
        _unsaveS.addAll(model._unsaveS);
        _moveLimit = model._moveLimit;
        _stacNUM = model._stacNUM;

    }

    /** Clears the board to the initial position. */
    void init() {
        _board = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                put(EMPTY, sq(i, j));
            }
        }
        for (Square t:INITIAL_ATTACKERS) {
            if (t != null) {
                put(BLACK, t);
            }
        }
        for (Square t1:INITIAL_DEFENDERS) {
            if (t1 != null) {
                put(WHITE, t1);
            }
        }
        put(KING, THRONE);
        _unnum = new Stack<Integer>();
        repset = new HashSet<Repesave>();
        _winner = null;
        _moveCount = 0;
        _stacNUM = 0;
        _repeated = false;
        _turn = BLACK;
        _unsaveS = new Stack<Square>();
        _unsaveP = new Stack<Piece>();
        _moveLimit = Integer.MAX_VALUE;
        rep = new Repesave(_board, _turn);
        repset.add(rep);
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param n  the number  */
    void setMoveLimit(int n) {
        _moveLimit = n;
        if (2 * n <= moveCount()) {
            throw new IllegalArgumentException("wrong move limit");
        }
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        int l1 = repset.size();
        rep = new Repesave(_board, _turn);
        repset.add(rep);
        int l2 = repset.size();
        if (l1 == l2) {
            _winner = _turn;
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (_board[i][j] == KING) {
                    return sq(i, j);
                }
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        int col1 = s.col();
        int row1 = s.row();
        _board[col1][row1] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        _stacNUM++;
        _unsaveP.push(get(s));
        _unsaveS.push(s);
        put(p, s);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        boolean val = false;
        if (get(from) != KING && to == THRONE) {
            return false;
        }
        if (from.isRookMove(to)) {
            int dic = from.direction(to);
            val = true;
            if (dic == 0) {
                for (int i = from.row() + 1; i <= to.row(); i++) {
                    Piece temp1 = _board[from.col()][i];
                    if (temp1 != EMPTY) {
                        val = false;
                        break;
                    }
                }
            }
            if (dic == 2) {
                for (int i = from.row() - 1; i >= to.row(); i--) {
                    Piece temp1 = _board[from.col()][i];
                    if (temp1 != EMPTY) {
                        val = false;
                        break;
                    }
                }
            }
            if (dic == 1) {
                for (int i = from.col() + 1; i <= to.col(); i++) {
                    Piece temp1 = _board[i][from.row()];
                    if (temp1 != EMPTY) {
                        val = false;
                        break;
                    }
                }
            }
            if (dic == 3) {
                for (int i = from.col() - 1; i >= to.col(); i--) {
                    Piece temp1 = _board[i][from.row()];
                    if (temp1 != EMPTY) {
                        val = false;
                        break;
                    }
                }
            }
        }
        return val;
    }
    /**calculate the number around the king.
     * @return val*/
    int blackaroundK() {
        Square kk = kingPosition();
        int val = 0;
        ArrayList<Piece> kr = getPadjacent(kk);
        for (Piece t : kr) {
            if (t == BLACK) {
                val++;
            }
        }
        return val;
    }
    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        int fcol = from.col();
        int frow = from.row();
        int tocol = to.col();
        int torow = to.row();
        Piece fp = get(from);
        _stacNUM = 0;
        revPut(EMPTY, from);
        revPut(fp, to);
        ArrayList<Square> adjj = get2Adjacent(to);
        for (Square t:adjj) {
            if (t != null) {
                if (iscapture(to, t)) {
                    capture(to, t);
                }
            }
        }
        _unnum.push(_stacNUM);
        _moveCount++;
        Square kp = kingPosition();
        if (kp == null) {
            _winner = BLACK;
        } else {
            int kc = kp.col();
            int kr = kp.row();
            if (kc == 0 || kc == 8) {
                _winner = WHITE;
            }
            if (kr == 0 || kr == 8) {
                _winner = WHITE;
            }
        }
        _turn = _turn.opponent();
        if (((_moveCount + 1) / 2) > _moveLimit) {
            _winner = _turn;
        }
        checkRepeated();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /**just determine whether we can capture the sq between 2 sq.
     * @return val
     * @param sq0 the first
     * @param sq2 the second*/
    boolean iscapture(Square sq0, Square sq2) {
        int mid1 = (sq0.col() + sq2.col()) / 2;
        int mid2 = (sq0.row() + sq2.row()) / 2;
        Square mid = sq(mid1, mid2);
        Piece mp = get(mid);
        if (mp == EMPTY) {
            return false;
        } else {
            if (mp != KING) {
                if (isOpponent(mid, sq0) && isOpponent(mid, sq2)) {
                    return true;
                }
            } else {
                if (mid == THRONE) {
                    boolean temp1 = get(NTHRONE) == BLACK;
                    boolean temp2 = get(STHRONE) == BLACK;
                    boolean temp3 = get(STHRONE) == BLACK;
                    boolean temp4 = get(WTHRONE) == BLACK;
                    if (temp1 && temp2 && temp3 && temp4) {
                        return true;
                    }
                } else if (inThrone(mid)) {
                    put(BLACK, THRONE);
                    ArrayList<Piece> adj = getPadjacent(mid);
                    boolean temp1 = adj.get(0) == BLACK;
                    boolean temp2 = adj.get(1) == BLACK;
                    boolean temp3 = adj.get(2) == BLACK;
                    boolean temp4 = adj.get(3) == BLACK;
                    put(EMPTY, THRONE);
                    if (temp1 && temp2 && temp3 && temp4) {
                        return true;
                    }
                } else {
                    if (isOpponent(mid, sq0) && isOpponent(mid, sq2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**a more comprehensive of determining whethe is opponent.
     * @return val
     * @param sq1 the first
     * @param sq2 the second*/
    private boolean isOpponent(Square sq1, Square sq2) {
        Piece t = get(sq1);
        Piece t2 = get(sq2);
        if (t == WHITE || t == KING) {
            if (t2 == BLACK) {
                return true;
            }
        }
        if (t == BLACK) {
            if (t2 == WHITE || t2 == KING) {
                return true;
            }
        }
        if (t == WHITE || t == BLACK) {
            if (sq2 == THRONE && t2 == EMPTY) {
                return true;
            }
        }
        return false;
    }
    /**get the sq adjacent(1 distance) to the sq1.
     * @return val
     * @param sq1  the square*/
    private ArrayList<Square> getAdjacent(Square sq1) {
        ArrayList<Square> val = new ArrayList<>();
        int c1 = sq1.col();
        int r1 = sq1.row();
        if (c1 - 1 >= 0) {
            val.add(sq(c1 - 1, r1));
        }
        if (c1 + 1 <= 8) {
            val.add(sq(c1 + 1, r1));
        }
        if (r1 - 1 >= 0) {
            val.add(sq(c1, r1 - 1));
        }
        if (r1 + 1 <= 8) {
            val.add(sq(c1, r1 + 1));
        }
        return val;
    }
    /**get the Piece adjacent(1 distance) to the sq1.
     * @return temp
     * @param sq1 the square */
    ArrayList<Piece> getPadjacent(Square sq1) {
        ArrayList<Square> val = getAdjacent(sq1);
        ArrayList<Piece> temp = new ArrayList<>();
        for (Square t:val) {
            if (t != null) {
                temp.add(get(t));
            }
        }
        return temp;
    }
    /**get the adjacent pieces(2 distance) to the sq1
     * helper function for catch.
     * @return  val
     * @param sq1  the square*/
    private ArrayList<Square> get2Adjacent(Square sq1) {
        ArrayList<Square> val = new ArrayList<>();
        int c1 = sq1.col();
        int r1 = sq1.row();
        if (c1 - 2 >= 0) {
            val.add(sq(c1 - 2, r1));
        }
        if (c1 + 2 <= 8) {
            val.add(sq(c1 + 2, r1));
        }
        if (r1 - 2 >= 0) {
            val.add(sq(c1, r1 - 2));
        }
        if (r1 + 2 <= 8) {
            val.add(sq(c1, r1 + 2));
        }
        return val;
    }


    /**determine whether the sq is in the 4 throne-side.
     * @return tor fal
     * @param sq1  the suare*/
    private boolean inThrone(Square sq1) {
        if (sq1 == NTHRONE) {
            return true;
        }
        if (sq1 == STHRONE) {
            return true;
        }
        if (sq1 == ETHRONE) {
            return true;
        }
        if (sq1 == WTHRONE) {
            return true;
        }
        return false;
    }
    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        if (sq0.col() == sq2.col()) {
            int del = (sq0.row() + sq2.row()) / 2;
            revPut(EMPTY, sq(sq0.col(), del));
        } else {
            int del = (sq0.col() + sq2.col()) / 2;
            revPut(EMPTY, sq(del, sq0.row()));
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _winner = null;
            _moveCount = _moveCount - 1;
            _turn = _turn.opponent();
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (!_repeated && moveCount() != 0) {
            repset.remove(rep);
            int k = _unnum.pop();
            for (int i = 0; i < k; i++) {
                Piece p = _unsaveP.pop();
                Square s = _unsaveS.pop();
                put(p, s);
            }
        }
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _unnum.clear();
        _unsaveP.clear();
        _unsaveS.clear();
        repset.clear();
    }
    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> val = new ArrayList<Move>();
        HashSet<Square> have = pieceLocations(side);
        for (Square temp:have) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (isUnblockedMove(temp, sq(i, j))) {
                        val.add(mv(temp, sq(i, j)));
                    }
                }
            }
        }
        return val;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        if (legalMoves(side) != null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> val = new HashSet<Square>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (_board[i][j] == side) {
                    val.add(sq(i, j));
                }
            }
        }
        return val;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }
    /** for repeat to save. */
    class Repesave {
        /**THE PIECE. */
        private Piece [][] b = new Piece[SIZE][SIZE];
        /** THE TURN. */
        private Piece turn;
        /**CONSTRUCT FUNCTION.
         * @param d the piece
         * @param t  the t*/
        Repesave(Piece[][] d, Piece t) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    b[i][j] = d[i][j];
                }
            }
            turn = t;
        }
        /**construct function. */
        Repesave() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    b[i][j] = EMPTY;
                }
            }
            turn = EMPTY;
        }
        /**OVERRIDE. */
        @Override
        public int hashCode() {
            return 0;
        }
        /**OVERRIDE1. */
        @Override
        public boolean equals(Object tt) {
            if (tt instanceof Repesave) {
                boolean val = true;
                if (turn != ((Repesave) tt).turn) {
                    return false;
                }
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        if (b[i][j] != ((Repesave) tt).b[i][j]) {
                            val = false;
                            break;
                        }
                    }
                    if (!val) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }


    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or null if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    /** the board of the game. */
    private Piece[][] _board;
    /**the move limit. */
    private int _moveLimit;
    /**the boolean rep. */
    private boolean _repeated;
    /**stac _ num. */
    private int _stacNUM;
    /**my own class. */
    private Repesave rep;
    /**_movehash records move to repeated. */
    private Stack<Integer> _unnum;
    /**set for repeat. */
    private HashSet<Repesave> repset;
    /**save for square undo. */
    private Stack<Square> _unsaveS;
    /**save for piece undo. */
    private Stack<Piece> _unsaveP;
}
