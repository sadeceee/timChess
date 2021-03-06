package board;

import chessman.Figure;

import java.util.List;

/**
 * Created by tim on 11.02.16.
 */
public class Square {
    private Figure figure;
    private int x;
    private int y;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Figure check(List<Figure> figures, Square[][] board) {
        for (Figure f : figures)
            if (f.canMove(board, x, y)) return f;
        return null;
    }

    public boolean isAllocated() {
        return figure!=null;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        figure.setX(x);
        figure.setY(y);
        this.figure = figure;
    }

    public void removeFigure() {
        this.figure = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
