package board;

import chessman.Figure;
import chessman.King;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by tim on 09.02.16.
 */
public class Board extends Canvas {

    // Graphics
    private Graphics2D g2;
    private Image background = null; // background image
    // Board of squares with figures
    private final Square[][] board = new Square[8][8];
    private FigureFactory figureFactory;
    private List<Figure> figures_w; // All white figures except King
    private List<Figure> figures_b; // All black figures except King
    private List<Figure> temp_w = new ArrayList<Figure>();
    private List<Figure> temp_b = new ArrayList<Figure>();
    private King king_w; // white king
    private King king_b; // black king
    // FEN-notation
    private String[] fen;
    private String[] position;
    public String activePlayer;
    private String castling;
    private String enPassant;
    private Square resetEnPassat;
    public int half;
    public int full;
    // input patterns
    private String castlingPattern = "([0Oo])([-]\\1){1,2}";
    private String notationPattern = "[a-hA-H][1-8][a-hA-H][1-8]";

    /**
     * Constructor, initializing background-graphics, board, figures, FEN-notation
     * @param fen Starting position
     */
    public Board(String fen) {
        setBackground(Color.GRAY);
        setSize(320, 320);
        newGame(fen);
    }

    /**
     * Creates a new Game with fen
     * @param fen Notation within the board starts
     */
    public void newGame(String fen) {
        figures_b = new ArrayList<Figure>();
        figures_w = new ArrayList<Figure>();
        initBackground();
        figureFactory = new FigureFactory();
        initFen(fen);
        initSquares();
        buildBoard();
    }

    private void initFen(String fen) {
        this.fen = fen.split(" ");
        position = this.fen[0].split("/");
        activePlayer = this.fen[1];
        castling = this.fen[2];
        enPassant = this.fen[3];
        half = Integer.parseInt(this.fen[4]);
        full = Integer.parseInt(this.fen[5]);
    }

    /**
     * Get FEN-notation of actual board
     * @return actual FEN-notation
     */
    public String getFen() {
        String temp = "";
        if (castling.hashCode()==0) castling = "-";
        temp += updateFen() + " " + activePlayer + " " + castling + " " + enPassant + " " + half + " " + full;
        initFen(temp);
        return temp;
    }

    private String updateFen() {
        String temp = "";
        for (int y=0; y<board.length; y++) {
            for (int x=0, i=1; x < board[y].length; i=1) {
                if (board[y][x].isAllocated()) {
                    temp += figureFactory.getFigureType(board[y][x++].getFigure());
                } else {
                    while (x++ < board[y].length-1 && !board[y][x].isAllocated()) i++;
                    temp += i;
                }
            }
            temp += "/";
        }
        return temp;
    }

    private void initBackground() {
        try {
            background = ImageIO.read(Board.class.getResource("/img/board.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        g2 = (Graphics2D) g;
        reDraw(g2);
    }

    /**
     * Repaint the canvas, Redraw background, figures
     * @param g the canvas
     */
    public void reDraw(Graphics g) {
        g.drawImage(background, 0, 0, null);

        for (int y=0; y<board.length; y++) {
            int x = 0;
            for (Square s : board[y]) {
                if (s.isAllocated())
                    g.drawImage(s.getFigure().getImg(), 20 + 35 * x, 20 + 35 * y, null);
                x++;
            }
        }
    }

    private void initSquares() {
        for (int y=0; y<board.length; y++) {
            for (int x=0; x<board[y].length; x++) {
                board[y][x] = new Square(x, y);
            }
        }
    }

    private void buildBoard() {
        int y = 0;
        for (String s : position) {
            int x = 0;
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '8': x++;
                    case '7': x++;
                    case '6': x++;
                    case '5': x++;
                    case '4': x++;
                    case '3': x++;
                    case '2': x++;
                    case '1': x++;
                        break;
                    default:
                        Figure temp = figureFactory.getFigure(c, x, y);
                        board[y][x++].setFigure(temp);
                        if (temp.isWhite() && c!='K')
                            figures_w.add(temp);
                        else if (temp.isBlack() && c!='k')
                            figures_b.add(temp);
                        if (c=='k') king_b = (King) board[y][x-1].getFigure();
                        if (c=='K') king_w = (King) board[y][x-1].getFigure();
                }
            }
            y++;
        }
    }

    /**
     * Move figure with valid notation
     * @param notation Notation can be "e2e4" or "o-o"
     * @throws Exception Notification message
     */
    public void performMove(String notation) throws Exception {
        if (Pattern.matches(notationPattern, notation)) {
            notation = notation.toLowerCase();
            moveFigure(notation);
        }
        else if (Pattern.matches(castlingPattern, notation)) {
            performCastling(notation);
            resetEnPassat();
        } else {
            throw new Exception("Invalid input");
        }
    }

    private void moveFigure(String notation) throws Exception {
        int x = notation.charAt(0) - 'a';
        int y = 8 - Integer.parseInt("" + notation.charAt(1));
        int destX = notation.charAt(2) - 'a';
        int destY = 8 - Integer.parseInt("" + notation.charAt(3));
        Figure current = board[y][x].getFigure();

        if (current.getPlayer()!=activePlayer.charAt(0)) {
            throw new Exception(activePlayer.charAt(0)+"'s turn");
        }

        if (current.canMove(board, destX, destY) && ((current.isWhite())? (!whiteIsStillInCheck(current, x, y, destX, destY)) : (!blackIsStillInCheck(current, x, y, destX, destY)))) {
            removeFigureInList(destX, destY);
            board[destY][destX].setFigure(refreshFigure(current));
            updateCastlingFen(current);
            resetEnPassat();
            setEnPassat(current, notation);
            board[y][x].removeFigure();
        } else {
            throw new Exception("Illegal move");
        }
    }

    private Figure refreshFigure(Figure current) {
        return (current.getTurnIntoQueen()) ? figureFactory.getFigure((current.isWhite()) ? 'Q' : 'q', current.getX(), current.getY()) : current;
    }

    private void updateCastlingFen(Figure current) {
        current.moved();
        String temp_f = figureFactory.getFigureType(current);
        if (temp_f.equals("K")) {
            castling = castling.replace("K", "");
            castling = castling.replace("Q", "");
        }
        else if (temp_f.equals("k")) {
            castling = castling.replace("k", "");
            castling = castling.replace("q", "");
        }
        else if (temp_f.equals("R")) castling = castling.replace((current.getX()<king_w.getX()) ? "Q" : "K", "");
        else if (temp_f.equals("r")) castling = castling.replace((current.getX()<king_b.getX()) ? "q" : "k", "");
    }

    /**
     * Test if king is in check
     * @throws Exception Notification message: Check!
     */
    public void kingIsInCheck() throws Exception {
        if (whiteIsInCheck() || blackIsInCheck()) throw new Exception("Check!") ;
    }

    /**
     * Test if king is check-mate
     * @throws Exception Notification message: Checkmate!
     */
    public void kingIsCheckMate() throws Exception {
        if (whiteIsInCheck()) {
            int kx = king_w.getX();
            int ky = king_w.getY();
            for (int y=ky-1; y<=ky+1; y++)
                for (int x=kx-1; x<=kx+1; x++)
                    try {
                        if (!board[y][x].isAllocated() || !board[y][x].getFigure().isWhite()) {
                            if (!whiteIsStillInCheck(king_w, kx, ky, x, y)) return;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) { }
            Figure enemy = board[ky][kx].check(figures_b, board);
            if (kingCanBeSaved(figures_w, kx, ky, enemy.getX(), enemy.getY())) return;
            throw new Exception("Checkmate!");
        } else if (blackIsInCheck()) {
            int kx = king_b.getX();
            int ky = king_b.getY();
            for (int y=ky-1; y<=ky+1; y++)
                for (int x=kx-1; x<=kx+1; x++)
                    try {
                        if (board[y][x] != null && !board[y][x].isAllocated()) {
                            if (!blackIsStillInCheck(king_b, kx, ky, x, y)) return;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) { }
            Figure enemy = board[ky][kx].check(figures_w, board);
            if (kingCanBeSaved(figures_b, kx, ky, enemy.getX(), enemy.getY())) return;
            throw new Exception("Checkmate!");
        }

    }

    private boolean kingCanBeSaved(List<Figure> f, int king_x, int king_y, int enemy_x, int enemy_y) {
        if (enemy_x < king_x) {
            if (enemy_y < king_y) {
                int y= enemy_y;
                for (int x= enemy_x; x< king_x; x++)
                    if (board[y++][x].check(f, board)!=null) return true;
            } else if (enemy_y == king_y) {
                for (int x= enemy_x; x< king_y; x++)
                    if (board[enemy_y][x].check(f, board)!=null) return true;
            } else if (enemy_y > king_y) {
                int y= enemy_y;
                for (int x= enemy_x; x< king_x; x++)
                    if (board[y--][x].check(f, board)!=null) return true;
            }
        }
        else if (enemy_x == king_x) {
            if (enemy_y < king_y) {
                for (int y= enemy_y; y< king_y; y++)
                    if (board[y][enemy_x].check(f, board)!=null) return true;
            } else if (enemy_y > king_y) {
                for (int y= enemy_y; y< king_y; y--)
                    if (board[y][enemy_x].check(f, board)!=null) return true;
            }
        }
        else if (enemy_x > king_x) {
            if (enemy_y < king_y) {
                int y= enemy_y;
                for (int x= enemy_x; x< king_x; x--)
                    if (board[y++][x].check(f, board)!=null) return true;
            } else if (enemy_y == king_y) {
                for (int x= enemy_x; x< king_y; x--)
                    if (board[enemy_y][x].check(f, board)!=null) return true;
            } else if (enemy_y > king_y) {
                int y= enemy_y;
                for (int x= enemy_x; x< king_x; x--)
                    if (board[y--][x].check(f, board)!=null) return true;
            }
        }
        return false;
    }

    private boolean whiteIsInCheck() {
        return board[king_w.getY()][king_w.getX()].check(figures_b, board)!=null;
    }

    private boolean blackIsInCheck() {
        return board[king_b.getY()][king_b.getX()].check(figures_w, board)!=null;
    }

    private boolean whiteIsStillInCheck(Figure current, int x, int y, int destX, int destY) {
        Square[][] temp = cloneBoard();
        if (temp[destY][destX].isAllocated()) temp_b.remove(temp[destY][destX].getFigure());
        temp[destY][destX].setFigure(refreshFigure(current));
        temp[y][x].removeFigure();
        boolean inCheck = temp[king_w.getY()][king_w.getX()].check(temp_b, temp)!=null;
        temp[y][x].setFigure(refreshFigure(current));
        return inCheck;
    }

    private boolean blackIsStillInCheck(Figure current, int x, int y, int destX, int destY) {
        Square[][] temp = cloneBoard();
        if (temp[destY][destX].isAllocated()) temp_w.remove(temp[destY][destX].getFigure());
        temp[destY][destX].setFigure(refreshFigure(current));
        temp[y][x].removeFigure();
        boolean inCheck = temp[king_b.getY()][king_b.getX()].check(temp_w, temp)!=null;
        temp[y][x].setFigure(current);
        return inCheck;
    }

    private void removeFigureInList(int destX, int destY) {
        if (board[destY][destX].isAllocated())
            if (board[destY][destX].getFigure().isWhite())
                figures_w.remove(board[destY][destX].getFigure());
            else
                figures_b.remove(board[destY][destX].getFigure());
    }

    private void resetEnPassat() {
        if (!enPassant.equals("-")) {
            if (resetEnPassat.isAllocated()) // if figure is not beaten
                board[8-Integer.parseInt(""+((resetEnPassat.getFigure().isWhite()) ? (char)(enPassant.charAt(1)+1) : (char)(enPassant.charAt(1)-1)))][enPassant.charAt(0)-'a'].getFigure().setEnPassat(false);
            enPassant = "-";
            resetEnPassat = null;
        }
    }

    private void setEnPassat(Figure current, String notation) {
        if (current.getEnPassat()) {
            enPassant = notation.charAt(2) + "" + ((current.isWhite()) ? (char) (notation.charAt(3) - 1) : (char) (notation.charAt(3) + 1));
            resetEnPassat = board[8 - Integer.parseInt("" + notation.charAt(3))][notation.charAt(2) - 'a'];
        }
    }

    private void performCastling(String notation) throws Exception {
        if (Pattern.matches("([0Oo])([-]\\1)", notation)) {
            if ((activePlayer.equals("w")) ? king_w.shortCastling(board, activePlayer.charAt(0))
                                          && !whiteIsInCheck()
                                          && board[king_w.getY()][king_w.getX()+2].check(figures_b, board)==null
                                           : king_b.shortCastling(board, activePlayer.charAt(0))
                                          && !blackIsInCheck()
                                          && board[king_b.getY()][king_b.getX()+2].check(figures_w, board)==null) {
                if (activePlayer.equals("w")) {
                    board[7][6].setFigure(king_w);
                    king_w.moved();
                    castling = castling.replace('K', '\u0000');
                    castling = castling.replace('Q', '\u0000');
                    board[7][5].setFigure(board[7][7].getFigure());
                    board[7][7].removeFigure();
                    board[7][4].removeFigure();
                } else {
                    board[0][6].setFigure(king_b);
                    king_b.moved();
                    castling = castling.replace('k', '\u0000');
                    castling = castling.replace('q', '\u0000');
                    board[0][5].setFigure(board[0][7].getFigure());
                    board[0][7].removeFigure();
                    board[0][4].removeFigure();
                }
            }
            else {
                throw new Exception("Castling not possible");
            }
        } else {
            if ((activePlayer.equals("w")) ? king_w.longCastling(board, activePlayer.charAt(0))
                                          && !whiteIsInCheck()
                                          && board[king_w.getY()][king_w.getX()-2].check(figures_b, board)==null
                                           : king_b.longCastling(board, activePlayer.charAt(0))
                                          && !blackIsInCheck()
                                          && board[king_b.getY()][king_b.getX()-2].check(figures_w, board)==null) {
                if (activePlayer.equals("w")) {
                    board[7][2].setFigure(king_w);
                    king_w.moved();
                    castling = castling.replace('K', '\u0000');
                    castling = castling.replace('Q', '\u0000');
                    board[7][3].setFigure(board[7][0].getFigure());
                    board[7][0].removeFigure();
                    board[7][4].removeFigure();
                } else {
                    board[0][2].setFigure(king_b);
                    king_b.moved();
                    castling = castling.replace('k', '\u0000');
                    castling = castling.replace('q', '\u0000');
                    board[0][3].setFigure(board[0][0].getFigure());
                    board[0][0].removeFigure();
                    board[0][4].removeFigure();
                }
            }
            else {
                throw new Exception("Castling not possible");
            }
        }
    }

    private Square[][] cloneBoard() {
        Square[][] temp = new Square[8][8];
        temp_b.clear();
        temp_w.clear();
        for (int y=0; y<8; y++) {
            for (int x=0; x<8; x++) {
                temp[y][x] = new Square(x, y);
                if (board[y][x].isAllocated()) {
                    Figure tempFigure = board[y][x].getFigure();
                    temp[y][x].setFigure(tempFigure);
                    if (tempFigure.isWhite())
                        temp_w.add(tempFigure);
                    else temp_b.add(tempFigure);
                }
            }
        }
        return temp;
    }
}

