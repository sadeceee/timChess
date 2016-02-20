import board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by tim on 09.02.16.
 */
public class Game implements ActionListener {
    // rnb1qknr/Pppp1ppp/8/2P5/8/2BP6/PPP2PPP/RN5K b KQkq - 0 1
    // r2qk2r/Pppp1ppp/8/2P5/8/2PP6/PPP2PPP/R2QK2R b KQkq - 0 1
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
    private int count = 0;

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
        save.addActionListener(this);
        file.add(load);
        load.addActionListener(this);
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
        input = new JTextField(23);
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

        window.setResizable(false);
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
                notation.setText(notation.getText() + ((board.half<board.full)? ++board.half : board.full++) + ". " + input.getText() + "  ");
                if (count == 4) {
                    notation.setText(notation.getText() + "\n");
                    count = 0;
                }
                else count++;
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
            FenFrame fenFrame = new FenFrame(board);
        }
        else if (e.getSource().equals(getFen)) {
            GetFenFrame getFenFrame = new GetFenFrame(board.getFen());
        }
        else if (e.getSource().equals(save)) {
            try {
                Files.write(Paths.get("src/save.txt"), Arrays.asList(board.getFen()), Charset.forName("UTF-8"));
            } catch (IOException ex) {
                message.setText("File not found");
                message.setVisible(true);
            }
        }
        else if (e.getSource().equals(load)) {
            try {
                List<String> s = Files.readAllLines(Paths.get("src/save.txt"));
                board.newGame(s.get(0));
                board.reDraw(board.getGraphics());
            } catch (IOException ex) {
                message.setText("File not found");
                message.setVisible(true);
            }
        }
        else if (e.getSource().equals(exit)) {
            System.exit(0);
        }
    }
}

class FenFrame extends JFrame implements ActionListener {

    private JButton cancel;
    private JButton ok;
    private Board board;
    private String fen = "r2qk2r/Pppp1ppp/8/8/8/8/PPP1pPPP/R1pQK1pR b KQkq - 0 1";
    private JTextField customFen;
    private String fenPattern = "([PRNBKQprnbkq]|[12345678])+([//]([PRNBKQprnbkq]|[12345678])+){7}[ ][bw][ ](K[Q]{0,1}[k]{0,1}[q]{0,1}|Q[k]{0,1}[q]{0,1}|k[q]{0,1}|q|[-])[ ]([abcdefgh][36]|[-])[ ][12345]{0,1}[0123456789][ ][12345]{0,1}[0123456789]";

    public FenFrame(Board board) {
        this.board = board;
        setTitle("New Game");
        JPanel fenPanel = new JPanel(new BorderLayout(2, 1));
        customFen = new JTextField(20);
        cancel = new JButton("Canel");
        cancel.addActionListener(this);
        ok = new JButton("ok");
        ok.addActionListener(this);
        fenPanel.add(customFen, BorderLayout.NORTH);
        fenPanel.add(cancel, BorderLayout.WEST);
        fenPanel.add(ok, BorderLayout.EAST);

        add(fenPanel);
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(ok)) {
            if (Pattern.matches(fenPattern, customFen.getText())) {
                fen = customFen.getText();
                board.newGame(fen);
                board.reDraw(board.getGraphics());
                setVisible(false);
            }
        } else if (e.getSource().equals(cancel)) {
            setVisible(false);
        }
    }
}

class GetFenFrame extends JFrame {

    public GetFenFrame(String fen) {
        setTitle("Forsyth-Edwards-Notation");
        JLabel l = new JLabel(fen);
        add(l);
        pack();
        setVisible(true);
    }

}
