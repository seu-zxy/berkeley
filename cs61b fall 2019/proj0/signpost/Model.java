package signpost;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

import static signpost.Place.*;
import static signpost.Utils.*;

/** The state of a Signpost puzzle.  Each cell has coordinates (x, y),
 *  where 0 <= x < width(),  0 <= y < height().  The upper-left corner of
 *  the puzzle has coordinates (0, height() - 1), and the lower-right corner
 *  is at (width() - 1, 0).
 *
 *  A constructor initializes the squares according to a particular
 *  solution.  A solution is an assignment of sequence numbers from 1 to
 *  size() == width() * height() to square positions so that squares with
 *  adjacent numbers are separated by queen moves. A queen move is a move from
 *  one square to another horizontally, vertically, or diagonally. The effect
 *  is to give each square whose number in the solution is less than
 *  size() an <i>arrow direction</i>, 1 <= d <= 8, indicating the direction
 *  of the next higher numbered square in the solution: d * 45 degrees clockwise
 *  from straight up (i.e., toward higher y coordinates).  The highest-numbered
 *  square has direction 0.  Certain squares can have their values fixed to
 *  those in the solution. Initially, the only two squares with fixed values
 *  are those with the lowest and highest sequence numbers in the solution.
 *
 *  At any given time after initialization, a square whose value is not fixed
 *  may have an unknown value, represented as 0, or a tentative number (not
 *  necessarily that of the solution) between 1 and size(). Squares may be
 *  connected together, indicating that their sequence numbers (unknown or not)
 *  are consecutive.
 *
 *  When square S0 is connected to S1, we say that S1 is the <i>successor</i> of
 *  S0, and S0 is the <i>predecessor</i> of S1.  Sequences of connected squares
 *  with unknown (0) values form a <i>group</i>, identified by a unique
 *  <i>group number</i>.  Numbered cells (whether linked or not) are in group 0.
 *  Unnumbered, unlinked cells are in group -1.
 *
 *  Squares are represented as objects of the inner class Sq (Model.Sq).  A
 *  Model object is itself iterable, yielding its squares in unspecified order.
 *
 *  The puzzle is solved when all cells are contained in a single sequence
 *  of consecutively numbered cells (therefore all in group 0) and all cells
 *  with fixed sequence numbers appear at the corresponding position
 *  in that sequence.
 *
 *  @author josiath
 */
class Model implements Iterable<Model.Sq> {

    /** A Model whose solution is SOLUTION, initialized to its starting,
     *  unsolved state (where only cells with fixed numbers currently
     *  have sequence numbers and no unnumbered cells are connected).
     *  SOLUTION must be a proper solution:
     *      1. It must have dimensions w x h such that w * h >= 2.
     *      2. There must be a sequence of chess-queen moves such that
     *         the sequence of values in the cells reached is 1, 2, ... w * h.
     *  The contents of SOLUTION are copied into this Model, so that subsequent
     *  changes to it have no effect on the Model.
     */
    Model(int[][] solution) {
        if (solution.length == 0 || solution.length * solution[0].length < 2) {
            throw badArgs("must have at least 2 squares");
        }
        _width = solution.length;
        _height = solution[0].length;
        int last = _width * _height;
        BitSet allNums = new BitSet();
        _allSuccessors = Place.successorCells(_width, _height);
        _solution = new int[_width][_height];
        deepCopy(solution, _solution);
        _board = new Sq[_width][_height];
        _solnNumToPlace = new Place[last + 1];
        int i, j, k, j1, i1, x2, y2, dir2, x3, y3;
        j1 = i1 = i = j = k = 0;
        int[] temp = new int[last + 1];
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                for (k = 1; k <= last; k++) {
                    if (k == solution[i][j]) {
                        _solnNumToPlace[k] = Place.pl(i, j);
                        temp[k] = 1;
                    }
                }
                int dir1 = arrowDirection(i, j);
                if (_solution[i][j] != last && _solution[i][j] != 1) {
                    _board[i][j] = new Sq(i, j, 0, false, dir1, -1);
                } else {
                    _board[i][j] = new Sq(i, j, _solution[i][j], true, dir1, 0);
                }
                _allSquares.add(_board[i][j]);
            }
        }
        for (i = 1; i <= last; i++) {
            if (temp[i] != 1) {
                throw badArgs("not the right solution");
            }
        }
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                int h4 = _board[i][j].direction();
                _board[i][j]._successors = _allSuccessors[i][j][h4];
                for (i1 = 0; i1 < _width; i1++) {
                    y3 = _board[i][j].y;
                    for (j1 = 0; j1 < _height; j1++) {
                        x2 = _board[i1][j1].x;
                        y2 = _board[i1][j1].y;
                        dir2 = dirOf(x2, y2, _board[i][j].x, y3);
                        if (dir2 == _board[i1][j1].direction()) {
                            if (_board[i][j]._predecessors == null) {
                                _board[i][j]._predecessors = new PlaceList();
                            }
                            _board[i][j]._predecessors.add(pl(x2, y2));
                        }
                    }
                }
            }
        }
        _unconnected = last - 1;
    }

    /** Initializes a copy of MODEL. */
    Model(Model model) {
        _width = model.width(); _height = model.height();
        _unconnected = model._unconnected;
        _solnNumToPlace = model._solnNumToPlace;
        _solution = model._solution;
        _usedGroups.addAll(model._usedGroups);
        _allSuccessors = model._allSuccessors;
        int i, j;
        i = j = 0;
        _board = new Sq[_width][_height];
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                int x1 = model._board[i][j].x;
                int y1 = model._board[i][j].y;
                int group1 = model._board[i][j]._group;
                boolean hasFixednum1 = model._board[i][j]._hasFixedNum;
                int seq1 = model._board[i][j]._sequenceNum;
                int dir1 = model._board[i][j]._dir;
                _board[i][j] = new Sq(x1, y1, seq1, hasFixednum1, dir1, group1);
                _allSquares.add(_board[i][j]);
            }
        }
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                _board[i][j]._predecessor = model._board[i][j].predecessor();
                _board[i][j]._successor = model._board[i][j].successor();
                _board[i][j]._head = model._board[i][j]._head;
            }
        }
    }

    /** Returns the width (number of columns of cells) of the board. */
    final int width() {
        return _width;
    }

    /** Returns the height (number of rows of cells) of the board. */
    final int height() {
        return _height;
    }

    /** Returns the number of cells (and thus, the sequence number of the
     *  final cell). */
    final int size() {
        return _width * _height;
    }

    /** Returns true iff (X, Y) is a valid cell location. */
    final boolean isCell(int x, int y) {
        return 0 <= x && x < width() && 0 <= y && y < height();
    }

    /** Returns true iff P is a valid cell location. */
    final boolean isCell(Place p) {
        return isCell(p.x, p.y);
    }

    /** Returns all cell locations that are a queen move from (X, Y)
     *  in direction DIR, or all queen moves in any direction if DIR = 0. */
    final PlaceList allSuccessors(int x, int y, int dir) {
        return _allSuccessors[x][y][dir];
    }

    /** Returns all cell locations that are a queen move from P in direction
     *  DIR, or all queen moves in any direction if DIR = 0. */
    final PlaceList allSuccessors(Place p, int dir) {
        return _allSuccessors[p.x][p.y][dir];
    }

    /** Initialize MODEL to an empty WIDTH x HEIGHT board with a null solution.
     */
    void init(int width, int height) {
        if (width <= 0 || width * height < 2) {
            throw badArgs("must have at least 2 squares");
        }
        _width = width; _height = height;
        _unconnected = _width * _height - 1;
        _solution = null;
        _usedGroups.clear();
        int i = 0;
        int j = 0;
        int d = 0;
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                _board[i][j] = null;
            }
        }
        _allSquares.clear();
        _allSuccessors = Place.successorCells(_width, _height);
    }

    /** Remove all connections and non-fixed sequence numbers. */
    void restart() {
        for (Sq sq : this) {
            sq.disconnect();
        }
        assert _unconnected == _width * _height - 1;
    }

    /** Return the number array that solves the current puzzle (the argument
     *  the constructor.  The result must not be subsequently modified.  */
    final int[][] solution() {
        return _solution;
    }

    /** Return the position of the cell with sequence number N in my
     *  solution. */
    Place solnNumToPlace(int n) {
        return _solnNumToPlace[n];
    }

    /** Return the current number of unconnected cells. */
    final int unconnected() {
        return _unconnected;
    }

    /** Returns true iff the puzzle is solved. */
    final boolean solved() {
        return _unconnected == 0;
    }

    /** Return the cell at (X, Y). */
    final Sq get(int x, int y) {
        return _board[x][y];
    }

    /** Return the cell at P. */
    final Sq get(Place p) {
        return p == null ? null : _board[p.x][p.y];
    }

    /** Return the cell at the same position as SQ (generally from another
     *  board), or null if SQ is null. */
    final Sq get(Sq sq) {
        return sq == null ? null : _board[sq.x][sq.y];
    }

    /** Connect all numbered cells with successive numbers that as yet are
     *  unconnected and are separated by a queen move.  Returns true iff
     *  any changes were made. */
    boolean autoconnect() {
        int i = 0;
        int j = 0;
        int d = 0;
        int e = 0;
        boolean makechange = false;
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                if (_board[i][j].group() == 0) {
                    for (d = 0; d < _width; d++) {
                        for (e = 0; e < _height; e++) {
                            int h1 = _board[d][e].group();
                            int h2 = _board[d][e]._sequenceNum;
                            int h5 = _board[i][j]._sequenceNum + 1;
                            if (h1 == 0 && h2 == h5) {
                                if (_board[i][j].connectable(_board[d][e])) {
                                    _board[i][j].connect(_board[d][e]);
                                    makechange = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (makechange) {
            return true;
        }
        return false;
    }

    /** Sets the numbers in my squares to the solution from which I was
     *  last initialized by the constructor. */
    void solve() {
        int i = 0;
        int j = 0;
        int k = 0;
        int x1;
        int y1;
        for (k = 1; k <= _width * _height; k++) {
            Place temp = _solnNumToPlace[k];
            x1 = temp.x;
            y1 = temp.y;
            _board[x1][y1]._sequenceNum = k;
        }
        autoconnect();
        _unconnected = 0;
    }

    /** Return the direction from cell (X, Y) in the solution to its
     *  successor, or 0 if it has none. */
    private int arrowDirection(int x, int y) {
        int seq0 = _solution[x][y];
        int i = 0;
        int j = 0;
        int x1, y1;
        x1 = _width + 1;
        y1 = _height + 1;
        int dir;
        for (i = 0; i < _width; i++) {
            for (j = 0; j < _height; j++) {
                if (_solution[i][j] == _solution[x][y] + 1) {
                    x1 = i;
                    y1 = j;
                }
            }
        }
        if (x1 == _width + 1 || y1 == _height + 1) {
            return 0;
        } else {
            return Place.dirOf(x, y, x1, y1);
        }
    }

    /** Return a new, currently unused group number > 0.  Selects the
     *  lowest not currently in used. */
    private int newGroup() {
        for (int i = 1; true; i += 1) {
            if (_usedGroups.add(i)) {
                return i;
            }
        }
    }

    /** Indicate that group number GROUP is no longer in use. */
    private void releaseGroup(int group) {
        _usedGroups.remove(group);
    }

    /** Combine the groups G1 and G2, returning the resulting group. Assumes
     *  G1 != 0 != G2 and G1 != G2. */
    private int joinGroups(int g1, int g2) {
        assert (g1 != 0 && g2 != 0);
        if (g1 == -1 && g2 == -1) {
            return newGroup();
        } else if (g1 == -1) {
            return g2;
        } else if (g2 == -1) {
            return g1;
        } else if (g1 < g2) {
            releaseGroup(g2);
            return g1;
        } else {
            releaseGroup(g1);
            return g2;
        }
    }

    @Override
    public Iterator<Sq> iterator() {
        return _allSquares.iterator();
    }

    @Override
    public String toString() {
        String hline;
        hline = "+";
        for (int x = 0; x < _width; x += 1) {
            hline += "------+";
        }

        Formatter out = new Formatter();
        for (int y = _height - 1; y >= 0; y -= 1) {
            out.format("%s%n", hline);
            out.format("|");
            for (int x = 0; x < _width; x += 1) {
                Sq sq = get(x, y);
                if (sq.hasFixedNum()) {
                    out.format("+%-5s|", sq.seqText());
                } else {
                    out.format("%-6s|", sq.seqText());
                }
            }
            out.format("%n|");
            for (int x = 0; x < _width; x += 1) {
                Sq sq = get(x, y);
                if (sq.predecessor() == null && sq.sequenceNum() != 1) {
                    out.format(".");
                } else {
                    out.format(" ");
                }
                if (sq.successor() == null
                    && sq.sequenceNum() != size()) {
                    out.format("o ");
                } else {
                    out.format("  ");
                }
                out.format("%s |", ARROWS[sq.direction()]);
            }
            out.format("%n");
        }
        out.format(hline);
        return out.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Model model = (Model) obj;
        return (_unconnected == model._unconnected
                && _width == model._width && _height == model._height
                && Arrays.deepEquals(_solution, model._solution)
                && Arrays.deepEquals(_board, model._board));
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_solution) * Arrays.deepHashCode(_board);
    }

    /** Represents a square on the board. */
    final class Sq {
        /** A square at (X0, Y0) with arrow in direction DIR (0 if not
         *  set), group number GROUP, sequence number SEQUENCENUM (0
         *  if none initially assigned), which is fixed iff FIXED. */
        Sq(int x0, int y0, int sequenceNum, boolean fixed, int dir, int group) {
            x = x0; y = y0;
            pl = pl(x, y);
            _hasFixedNum = fixed;
            _sequenceNum = sequenceNum;
            _dir = dir;
            _head = this;
            _group = group;
        }

        /** A copy of OTHER, excluding head, successor, and predecessor. */
        Sq(Sq other) {
            this(other.x, other.y, other._sequenceNum, other._hasFixedNum,
                 other._dir, other._group);
            _successor = _predecessor = null;
            _head = this;
            _successors = other._successors;
            _predecessors = other._predecessors;
        }

        /** Return my current sequence number, or 0 if none assigned. */
        int sequenceNum() {
            return _sequenceNum;
        }

        /** Fix my current sequence number at N>0.  It is an error if my number
         *  is not initially 0 or N. */
        void setFixedNum(int n) {
            if (n == 0 || (_sequenceNum != 0 && _sequenceNum != n)) {
                throw badArgs("sequence number may not be fixed");
            }
            _hasFixedNum = true;
            if (_sequenceNum == n) {
                return;
            } else {
                releaseGroup(_head._group);
            }
            _sequenceNum = n;
            for (Sq sq = this; sq._successor != null; sq = sq._successor) {
                sq._successor._sequenceNum = sq._sequenceNum + 1;
            }
            for (Sq sq = this; sq._predecessor != null; sq = sq._predecessor) {
                sq._predecessor._sequenceNum = sq._sequenceNum - 1;
            }
        }

        /** Unfix my sequence number if it is currently fixed; otherwise do
         *  nothing. */
        void unfixNum() {
            Sq next = _successor, pred = _predecessor;
            _hasFixedNum = false;
            disconnect();
            if (pred != null) {
                pred.disconnect();
            }
            _sequenceNum = 0;
            if (next != null) {
                connect(next);
            }
            if (pred != null) {
                pred.connect(this);
            }
        }

        /** Return true iff my sequence number is fixed. */
        boolean hasFixedNum() {
            return _hasFixedNum;
        }

        /** Returns direction of my arrow (0 if no arrow). */
        int direction() {
            return _dir;
        }

        /** Return my current predecessor. */
        Sq predecessor() {
            return _predecessor;
        }

        /** Return my current successor. */
        Sq successor() {
            return _successor;
        }

        /** Return the head of the connected sequence I am currently in. */
        Sq head() {
            return _head;
        }

        /** Return the group number of my group.  It is 0 if I am numbered, and
         *  -1 if I am alone in my group. */
        int group() {
            if (_sequenceNum != 0) {
                return 0;
            } else {
                return _head._group;
            }
        }

        /** Size of alphabet. */
        static final int ALPHA_SIZE = 26;

        /** Return a textual representation of my sequence number or
         *  group/position. */
        String seqText() {
            if (_sequenceNum != 0) {
                return String.format("%d", _sequenceNum);
            }
            int g = group() - 1;
            if (g < 0) {
                return "";
            }

            String groupName =
                String.format("%s%s",
                              g < ALPHA_SIZE ? ""
                              : Character.toString((char) (g / ALPHA_SIZE
                                                           + 'a')),
                              Character.toString((char) (g % ALPHA_SIZE
                                                         + 'a')));
            if (this == _head) {
                return groupName;
            }
            int n;
            n = 0;
            for (Sq p = this; p != _head; p = p._predecessor) {
                n += 1;
            }
            return String.format("%s%+d", groupName, n);
        }

        /** Return locations of my potential successors. */
        PlaceList successors() {
            return _successors;
        }

        /** Return locations of my potential predecessors. */
        PlaceList predecessors() {
            return _predecessors;
        }

        /** Returns true iff I may be connected to cell S1, that is:
         *  + S1 is in the correct direction from me.
         *  + S1 does not have a current predecessor, and I do not have a
         *    current successor.
         *  + If S1 and I both have sequence numbers, then mine is
         *    sequenceNum() == S1.sequenceNum() - 1.
         *  + If neither S1 nor I have sequence numbers, then we are not part
         *    of the same connected sequence.              not finished
         */
        boolean connectable(Sq s1) {
            int h6 = pl.dirOf(s1.pl);
            int h7 = direction();
            if (s1._predecessor == null && _successor == null && h6 == h7) {
                if (_sequenceNum != 0 && s1._sequenceNum != 0) {
                    return (_sequenceNum == (s1._sequenceNum - 1));
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        /** Connect me to S1, if we are connectable; otherwise do nothing.
         *  Returns true iff we were connectable.  Assumes S1 is in the proper
         *  arrow direction from me. */
        boolean connect(Sq s1) {
            if (!connectable(s1)) {
                return false;
            }
            int sgroup = s1.group();
            _unconnected -= 1;
            _successor = s1;
            s1._predecessor = this;
            int sq1 = _sequenceNum;
            int sq2 = s1.sequenceNum();
            if (_sequenceNum != 0 && s1._sequenceNum == 0) {
                _usedGroups.remove(s1.group());
            }
            if (_sequenceNum == 0 && s1._sequenceNum != 0) {
                _usedGroups.remove(this.group());
            }
            if (_sequenceNum != 0) {
                Sq temp = this;
                while (temp._successor != null) {
                    temp = temp.successor();
                    sq1++;
                    temp._sequenceNum = sq1;
                }
            }
            if (s1._sequenceNum != 0) {
                Sq temp = s1;
                while (temp._predecessor != null) {
                    temp = temp.predecessor();
                    sq2--;
                    temp._sequenceNum = sq2;
                }
            }
            if (_sequenceNum == 0 && s1._sequenceNum == 0) {
                Sq temp = this;
                _head._group = joinGroups(_group, s1._group);
                while (temp._successor != null) {
                    temp = temp.successor();
                    temp._head = this._head;
                }
            }
            return true;
        }

        /** Disconnect me from my current successor, if any. */
        void disconnect() {
            Sq next = _successor;
            if (next == null) {
                return;
            }
            _unconnected += 1;
            next._predecessor = _successor = null;
            next._head = next;
            if (_sequenceNum == 0) {
                if (this._predecessor == null && next._successor == null) {
                    _usedGroups.remove(this.group());
                    this._group = next._group = -1;
                }
                if (_predecessor == null && next._successor != null) {
                    next._group = this.group();
                    _group = -1;
                }
                if (_predecessor != null && next._successor == null) {
                    next._group = -1;
                }
                if (_predecessor != null && next._successor != null) {
                    next._group = newGroup();
                }
            } else {
                if (_predecessor == null) {
                    _group = -1;
                }
                if (_predecessor != null) {
                    boolean haveseq = false;
                    Sq temp = this;
                    while (temp._predecessor != null) {
                        temp = temp._predecessor;
                        haveseq = haveseq || temp._hasFixedNum;
                    }
                    if (!haveseq) {
                        temp._group = newGroup();
                        temp._sequenceNum = 0;
                        fzs(temp);
                    }
                }
                if (next._successor == null) {
                    next._group = -1;
                } else {
                    Sq temp = next;
                    boolean havesq = ws(next);
                    if (!havesq) {
                        next._group = newGroup();
                        temp._sequenceNum = 0;
                        fz(temp);
                    }
                }
            }
            Sq temp = next;
            next._head = next;
            while (temp._successor != null) {
                temp = temp._successor;
                temp._head = next;
            }
        }
        /** Disconnect me from my current successor, if any.
         * @param a s
         * */
        void fz(Sq a) {
            Sq temp = a;
            while (temp._predecessor != null) {
                temp = temp._predecessor;
                temp._sequenceNum = 0;
            }
        }
        /** Disconnect me from my current successor, if any.
         * @param a s
         * */
        void fzs(Sq a) {
            Sq temp = a;
            while (temp._successor != null) {
                temp = temp._successor;
                temp._sequenceNum = 0;
            }
        }
        /** Disconnect me from my current successor, if any.
         * @param a s
         * @return haveseq
         * */
        boolean ws(Sq a) {
            Sq temp = a;
            boolean havesq = false;
            while (temp._successor != null) {
                temp = temp._successor;
                temp._head = a._head;
                havesq = havesq || temp.hasFixedNum();
            }
            return havesq;
        }
        @Override
        public boolean equals(Object obj) {
            Sq sq = (Sq) obj;
            return sq != null
                && pl == sq.pl
                && _hasFixedNum == sq._hasFixedNum
                && _sequenceNum == sq._sequenceNum
                && _dir == sq._dir
                && (_predecessor == null) == (sq._predecessor == null)
                && (_predecessor == null
                    || _predecessor.pl == sq._predecessor.pl)
                && (_successor == null || _successor.pl == sq._successor.pl);
        }

        @Override
        public int hashCode() {
            return (x + 1) * (y + 1) * (_dir + 1)
                * (_hasFixedNum ? 3 : 1) * (_sequenceNum + 1);
        }

        /** The coordinates of this square in the board. */
        protected final int x, y;
        /** My coordinates as a Place. */
        protected final Place pl;
        /** The first in the currently connected sequence of cells ("group")
         *  that includes this one. */
        private Sq _head;
        /** If _head == this, then the group number of the group of which this
         *  is a member.  Numbered sequences have a group number of 0,
         *  regardless of the value of _group. Unnumbered one-member groups
         *  have a group number of -1.   */
        private int _group;
        /** True iff assigned a fixed sequence number. */
        private boolean _hasFixedNum;
        /** The current imputed or fixed sequence number,
         *  numbering from 1, or 0 if there currently is none. */
        private int _sequenceNum;
        /** The arrow direction. The possible values are 0 (for unset),
         *  1 for northeast, 2 for east, 3 for southeast, 4 for south,
         *  5 for southwest, 6 for west, 7 for northwest, and 8 for north. */
        private int _dir;
        /** The current predecessor of this square, or null if there is
         *  currently no predecessor. */
        private Sq _predecessor;
        /** The current successor of this square, or null if there is
         *  currently no successor. */
        private Sq _successor;
        /** Locations of my possible predecessors. */
        private PlaceList _predecessors;
        /** Locations of my possible successor. */
        private PlaceList _successors;
    }

    /** ASCII denotations of arrows, indexed by direction. */
    private static final String[] ARROWS = {
        " *", "NE", "E ", "SE", "S ", "SW", "W ", "NW", "N "
    };

    /** Number of squares that haven't been connected. */
    private int _unconnected;
    /** Dimensions of board. */
    private int _width, _height;
    /** Contents of board, indexed by position. */
    private Sq[][] _board;
    /** Contents of board as a sequence of squares for convenient iteration. */
    private ArrayList<Sq> _allSquares = new ArrayList<>();
    /** _allSuccessors[x][y][dir] is a sequence of all queen moves possible
     *  on the board of in direction dir from (x, y).  If dir == 0,
     *  this is all places that are a queen move from (x, y) in any
     *  direction. */
    private PlaceList[][][] _allSuccessors;
    /** The solution from which this Model was built. */
    private int[][] _solution;
    /** Inverse mapping from sequence numbers to board positions. */
    private Place[] _solnNumToPlace;
    /** The set of positive group numbers currently in use. */
    private HashSet<Integer> _usedGroups = new HashSet<>();

}
