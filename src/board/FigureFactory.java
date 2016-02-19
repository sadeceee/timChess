package board;

import chessman.*;

/**
 * Created by tim on 09.02.16.
 */
public class FigureFactory {

    public Figure getFigure(char figureType, int x, int y) {
        if (figureType == 'k') {
            return new King('b', x, y);
        }
        else if (figureType == 'q') {
            return new Queen('b', x, y);
        }
        else if (figureType == 'r') {
            return new Rook('b', x, y);
        }
        else if (figureType == 'n') {
            return new Knight('b', x, y);
        }
        else if (figureType == 'b') {
            return new Bishop('b', x, y);
        }
        else if (figureType == 'p') {
            return new Pawn('b', x, y);
        }
        else if (figureType == 'K') {
            return new King('w', x, y);
        }
        else if (figureType == 'Q') {
            return new Queen('w', x, y);
        }
        else if (figureType == 'R') {
            return new Rook('w', x, y);
        }
        else if (figureType == 'N') {
            return new Knight('w', x, y);
        }
        else if (figureType == 'B') {
            return new Bishop('w', x, y);
        }
        else if (figureType == 'P') {
            return new Pawn('w', x, y);
        }

        return null;
    }
}
