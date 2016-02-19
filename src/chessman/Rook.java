package chessman;

import board.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by tim on 09.02.16.
 */
public class Rook extends Figure {

    public Rook(char color, int x, int y) {
        super(x, y);
        readImage(color);
        player = color;
    }

    public boolean canMove(Square[][] board, int destX, int destY) {
        return canMoveHorizontally(board, destX, destY) ||
               canMoveVertically(board, destX, destY);
    }

    private void readImage(char color) {
        try {
            if (color == 'b') {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_b_r.png"));
                isBlack = true;
            } else {
                img = ImageIO.read(Bishop.class.getResource("/img/chess_w_r.png"));
                isWhite = true;
            }
            img = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.printf("%s img readerror", getClass().getName());
            e.printStackTrace();
        }
    }
}
