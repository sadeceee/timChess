package chessman;

import board.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by tim on 09.02.16.
 */
public class Queen extends Figure {

    public Queen(char color, int x, int y) {
        super(x, y);
        readImage(color);
        player = color;
    }

    public boolean canMove(Square[][] board, int destX, int destY) {
        return canMoveVertically(board, destX, destY) ||
               canMoveHorizontally(board, destX, destY) ||
               canMoveDiagonally(board, destX, destY);
    }

    private void readImage(char color) {
        try {
            if (color == 'b') {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_b_q.png"));
                isBlack = true;
            } else {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_w_q.png"));
                isWhite = true;
            }
            img = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.printf("%s img readerror", getClass().getName());
            e.printStackTrace();
        }
    }
}
