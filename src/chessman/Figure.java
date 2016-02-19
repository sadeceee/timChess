package chessman;

import board.Square;

import java.awt.*;

/**
 * Created by tim on 09.02.16.
 */
public abstract class Figure {
    protected Image img;
    protected int x;
    protected int y;
    protected char player;
    protected boolean enPassat = false;
    protected boolean turnIntoQueen = false;
    protected boolean moved = false;
    protected boolean isBlack = false;
    protected boolean isWhite = false;

    public Figure(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Image getImg() {
        return img;
    }

    public char getPlayer() {
        return player;
    }

    public boolean getEnPassat() {
        return enPassat;
    }

    public boolean getTurnIntoQueen() {
        return turnIntoQueen;
    }

    public abstract boolean canMove(Square[][] board, int destX, int destY);

    public void moved() {
        moved = true;
    }

    protected boolean canMoveHorizontally(Square[][] board, int destX, int destY) {
        if (x==destX && y==destY) return false;
        if (y==destY) {
            for (int i = (x<=destX) ? x+1 : x-1; (i<=destX) ? i<destX : i>destX; i=(i<=destX) ? i+1 : i-1)
                if (board[y][i].isAllocated()) return false;
            if (board[y][destX].isAllocated() && board[y][destX].getFigure().player == player) return false;
        } else {
            return false;
        }
        return true;
    }

    protected boolean canMoveVertically(Square[][] board, int destX, int destY) {
        if (x==destX && y==destY) return false;
        if (x==destX) {
            for (int i = (y<=destY) ? y+1 : y-1; (y<=destY) ? i<destY : i>destY; i=(y<=destY) ? ++i : --i)
                if (board[i][x].isAllocated()) return false;
            if (board[destY][x].isAllocated() && board[destY][x].getFigure().getPlayer() == player) return false;
        } else {
            return false;
        }
        return true;
    }

    protected boolean canMoveDiagonally(Square[][] board, int destX, int destY) {
        if (x==destX && y==destY) return false;
        int dX = Math.abs(destX-x);
        int dY = Math.abs(destY-y);
        if (dX==dY) {
            int i = (destX > x) ? x + 1 : x - 1;
            int j = (destY > y) ? y + 1 : y - 1;
            for (; (x < destX) ? i<destX : i>destX; i = ((x < destX) ? ++i : --i), j = ((y < destY) ? ++j : --j))
                if (board[j][i].isAllocated()) return false;
            if (board[destY][destX].isAllocated() && board[destY][destX].getFigure().getPlayer() == player) return false;
        } else {
            return false;
        }
        return true;
    }

    protected boolean canJump(Square[][] board, int destX, int destY) {
        if (x==destX && y==destY) return false;
        int jumpX = Math.abs(destX-x);
        int jumpY = Math.abs(destY-y);
        if ((jumpX == 1 && jumpY == 2) || (jumpX == 2 && jumpY == 1))
            if (!board[destY][destX].isAllocated() || board[destY][destX].getFigure().getPlayer() != player)
                return true;
        return false;
    }

    public void setEnPassat(boolean enPassat) {
        this.enPassat = enPassat;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
