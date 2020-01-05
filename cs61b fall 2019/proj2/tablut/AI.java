package tablut;

import java.util.ArrayList;
import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author josiath
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /**large moves. */
    static final int K = 80;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        findMove();
        if (_lastFoundMove != null) {
            return _lastFoundMove.toString();
        } else {
            return null;
        }
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (b.hasMove(_myPiece)) {
            int d = maxDepth(b);
            int sense = (_myPiece == WHITE) ? 1 : -1;
            findMove(b, d, true, sense, -WINNING_VALUE, WINNING_VALUE);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth <= 0 || board.winner() != null) {
            return staticScore(board);
        }
        int bestval = -WINNING_VALUE;
        if (sense == 1) {
            ArrayList<Move> kk = (ArrayList<Move>) board.legalMoves(WHITE);
            ArrayList<Move> mm = (ArrayList<Move>) board.legalMoves(KING);
            mm.addAll(kk);
            int det = 1;
            for (Move t:mm) {
                if (t != null) {
                    Board temp = new Board(board);
                    temp.makeMove(t);
                    int dep1 = depth - det;
                    int val = findMove(temp, dep1, false, -1, alpha, beta);
                    if (saveMove && val >= bestval) {
                        _lastFoundMove = t;
                    }
                    bestval = Math.max(bestval, val);
                    alpha = Math.max(alpha, bestval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return bestval;
        } else {
            bestval = WINNING_VALUE;
            ArrayList<Move> mm = (ArrayList<Move>) board.legalMoves(BLACK);
            int det = 1;
            for (Move t:mm) {
                if (t != null) {
                    Board temp = new Board(board);
                    temp.makeMove(t);
                    int dep1 = depth - det;
                    int val = findMove(temp, dep1, false, 1, alpha, beta);
                    if (saveMove && val <= bestval) {
                        _lastFoundMove = t;
                    }
                    bestval = Math.min(bestval, val);
                    beta = Math.min(beta, bestval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return bestval;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        ArrayList<Move> wh = (ArrayList<Move>) board.legalMoves(WHITE);
        ArrayList<Move> bl = (ArrayList<Move>) board.legalMoves(BLACK);
        if (wh.size() > K || bl.size() > K) {
            return 2;
        }
        return 4;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Square kp = board.kingPosition();
        if (board.winner() == WHITE) {
            return WINNING_VALUE;
        }
        if (board.winner() == BLACK) {
            return -WINNING_VALUE;
        }
        ArrayList<Move> kk = (ArrayList<Move>) board.legalMoves(KING);
        int num = 0;
        int num1 = 0;
        for (Move t:kk) {
            if (t != null) {
                int c1 = t.to().col();
                int r1 = t.to().row();
                if (c1 == 0 || c1 == 8 || r1 == 0 || r1 == 8) {
                    num++;
                }
            }
        }
        if (board.turn() == BLACK) {
            if (num >= 2) {
                return WILL_WIN_VALUE;
            }
        }
        if (board.turn() == WHITE) {
            if (num > 0) {
                return WILL_WIN_VALUE;
            }
        }
        Square ks = board.kingPosition();
        int val = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Square temp = sq(i, j);
                Piece t1 = board.get(temp);
                if (t1 == WHITE) {
                    val = val + 1;
                }
                if (t1 == BLACK) {
                    val = val - 2;
                    int c1 = kp.col();
                    int r1 = kp.row();
                    boolean t12 = ((i == c1 - 1) || (i == c1 + 1)) && (j == r1);
                    boolean t2 = ((j == r1 - 1) || (j == r1 + 1)) && (i == c1);
                    if (t12 || t2) {
                        num1++;
                    }
                }
            }
        }
        return val;
    }

}
