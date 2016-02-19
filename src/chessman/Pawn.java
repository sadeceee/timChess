package chessman;

import board.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by tim on 09.02.16.
 */
public class Pawn extends Figure {

    public Pawn(char color, int x, int y) {
        super(x, y);
        readImage(color);
        player = color;
    }

    public boolean canMove(Square[][] board, int destX, int destY) {
        // is not the same figure
        if (x==destX && y==destY) return false;
        // is moving forward
        if ((player=='w') ? (destY-y)>0 : (destY-y)<0) return false;
        // enPassat move
        int range = ((isWhite) ? y==6: y==1) ? 2 : 1;
        enPassat = (Math.abs(destY-y)==2) ? true : false;
        // hit something
        if ((player=='w') ? ((destX+1==x && destY+1==y) || (destX-1==x && destY+1==y)) : ((destX-1==x && destY-1==y) || (destX+1==x && destY-1==y)))
            if (board[destY][destX].isAllocated() && board[destY][destX].getFigure().getPlayer() != player) return movingPossible(destY);
        // enPassat hit
        if (x<7 && (board[y][x+1].isAllocated()) && (board[y][x+1].getFigure().getEnPassat()) && ((isWhite) ? (destX==x+1&&destY==y-1) : (destX==x+1&&destY==y+1))) {
            if (!board[destY][destX].isAllocated()) {
                board[y][x+1].setFigure(null);
                return true;
            }
        } else if (x>0 && (board[y][x-1].isAllocated()) && (board[y][x-1].getFigure().getEnPassat()) && ((isWhite) ? (destX==x-1&&destY==y-1) : (destX==x-1&&destY==y+1))) {
            if (!board[destY][destX].isAllocated()) {
                board[y][x-1].setFigure(null);
                return true;
            }
        }
        // check if front is clear
        if (x==destX && range>=Math.abs(destY-y)) {
            for (int i = (y<=destY) ? y+1 : y-1; (y<=destY) ? i<=destY : i>=destY; i=(y<=destY) ? ++i : --i)
                if (board[i][x].isAllocated()) return false;
        } else {
            return false;
        }
        return movingPossible(destY);
    }

    private boolean movingPossible(int destY) {
        // turn into queen
        if ((isWhite) ? destY==0 : destY==8) turnIntoQueen = true;
        return true;
    }

    private void readImage(char color) {
        try {
            if (color == 'b') {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_b_p.png"));
                isBlack = true;
            } else {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_w_p.png"));
                isWhite = true;
            }
            img = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.printf("%s img readerror", getClass().getName());
            e.printStackTrace();
        }
    }
}
