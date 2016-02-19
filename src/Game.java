import board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by tim on 09.02.16.
 */
public class Game implements ActionListener {
    // rnb1qknr/Pppp1ppp/8/2P5/8/2BP6/PPP2PPP/RN5K b KQkq - 0 1
    private JFrame window;
    private JPanel panel;
    private Board board;
    private final String newGame = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private JLabel notation;
    private JTextField input;
    private JButton button;
    private JLabel message;
    private int totalMoves = 0;

    public static void main(String[] args) {
        Game chess = new Game();
    }

    /**
     * Constructor building the frame
     */
    public Game() {
        window = new JFrame("TimChess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400, 320);
        window.setLayout(new GridLayout(1, 2));

        board = new Board(newGame);
        window.add(board);
        panel = new JPanel(new BorderLayout(2, 2));
        notation = new JLabel("");
        panel.add(notation, BorderLayout.NORTH);

        JPanel notationPanel = new JPanel();
        notationPanel.setLayout(new BorderLayout(2, 1));
        input = new JTextField(10);
        button = new JButton("GO");
        button.addActionListener(this);
        message = new JLabel("Sample input");
        message.setForeground(Color.red);
        message.setVisible(false);
        notationPanel.add(message, BorderLayout.NORTH);
        notationPanel.add(button, BorderLayout.EAST);
        notationPanel.add(input, BorderLayout.WEST);
        panel.add(notationPanel, BorderLayout.SOUTH);

        window.add(panel);
        window.pack();
        window.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(button)) {
            try {
                message.setVisible(false);
                board.performMove(input.getText());
                board.activePlayer = (board.activePlayer.equals("w")) ? "b" : "w";
                notation.setText(notation.getText() + ++totalMoves + ". " + input.getText() + "  ");
                board.kingIsCheckMate();
                board.kingIsInCheck();
            } catch (Exception ex) {
                if (ex.getMessage() != null)
                    message.setText(ex.getMessage());
                else
                    message.setText("Error");
                message.setVisible(true);
            }

            input.setText("");
            board.reDraw(board.getGraphics());
        }
    }
}
