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
    private JMenuBar menuBar;
    private JMenuItem game;
    private JMenuItem startWithFen;
    private JMenuItem getFen;
    private JMenuItem save;
    private JMenuItem load;
    private JMenuItem exit;

    private Board board;
    private final String newGame = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private JTextArea notation;
    private JTextField input;
    private JButton button;
    private JLabel message;
    private int halfMoves = 0;
    private int fullMoves = 0;
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

        menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        game = new JMenuItem("New Game");
        startWithFen = new JMenuItem("Start with custom FEN");
        getFen = new JMenuItem("Get FEN");
        save = new JMenuItem("Save Game");
        load = new JMenuItem("Load Game");
        exit = new JMenuItem("Exit Game");
        file.add(game);
        game.addActionListener(this);
        file.add(startWithFen);
        startWithFen.addActionListener(this);
        file.add(getFen);
        getFen.addActionListener(this);
        file.add(save);
        file.add(load);
        file.add(exit);
        exit.addActionListener(this);
        JMenu edit = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem surrender = new JMenuItem("Surrender");
        edit.add(undo);
        edit.add(surrender);
        menuBar.add(file);
        menuBar.add(edit);

        board = new Board(newGame);
        window.add(board);
        panel = new JPanel(new BorderLayout(2, 2));
        notation = new JTextArea(17, 20);
        notation.setEditable(false);
        panel.add(menuBar, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(2, 1));
        contentPanel.add(notation, BorderLayout.NORTH);

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
        contentPanel.add(notationPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.SOUTH);


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
                notation.setText(notation.getText() + ((board.half<board.full)? ++board.half : board.full++) + ". " + input.getText() + "  \n");
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
        else if (e.getSource().equals(game)) {
            board.newGame(newGame);
            board.reDraw(board.getGraphics());
        }
        else if (e.getSource().equals(startWithFen)) {
            board.newGame("r2qk2r/Pppp1ppp/8/2P5/8/2PP6/PPP2PPP/R2QK2R b KQkq - 0 1");
            board.reDraw(board.getGraphics());
        }
        else if (e.getSource().equals(getFen)) {
            message.setText(board.getFen());
            message.setVisible(true);
        }
        else if (e.getSource().equals(exit)) {
            System.exit(0);
        }
    }
}
