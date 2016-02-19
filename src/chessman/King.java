package chessman;

import board.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by tim on 09.02.16.
 */
public class King extends Figure {

    public King(char color, int x, int y) {
        super(x, y);
        readImage(color);
        player = color;
    }

    public boolean canMove(Square[][] board, int destX, int destY) {
        if (Math.abs(destX-x)>1 || Math.abs(destY-y)>1) return false;
        return canMoveVertically(board, destX, destY) ||
               canMoveHorizontally(board, destX, destY) ||
               canMoveDiagonally(board, destX, destY);
    }

    public boolean shortCastling(Square[][] board, char c) {
        return (c=='w') ? (!board[7][6].isAllocated() && !board[7][5].isAllocated() && !board[7][7].getFigure().moved && !this.moved):
                          (!board[0][6].isAllocated() && !board[0][5].isAllocated() && !board[0][7].getFigure().moved && !this.moved);
    }

    public boolean longCastling(Square[][] board, char c) {
        return (c=='w') ? (!board[7][1].isAllocated() && !board[7][2].isAllocated() && !board[7][3].isAllocated() && !this.moved && !board[7][0].getFigure().moved):
                          (!board[0][1].isAllocated() && !board[0][2].isAllocated() && !board[0][3].isAllocated() && !this.moved && !board[0][0].getFigure().moved);
    }

    private void readImage(char color) {
        try {
            if (color == 'b') {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_b_k.png"));
                isBlack = true;
            } else {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_w_k.png"));
                isWhite = true;
            }
            img = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.printf("%s img readerror", getClass().getName());
            e.printStackTrace();
        }
    }
}
