import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Connect4 extends JFrame {

    private static final int ROWS = 6;
    private static final int COLS = 7;

    private JButton[][] buttons;
    private int[][] board;

    private int[][] blueAge;
    private int[][] redAge;

    private int clickCount;

    public Connect4() {
        super("Connect4");

        buttons = new JButton[ROWS][COLS];
        board = new int[ROWS][COLS];

        blueAge = new int[ROWS][COLS];
        redAge = new int[ROWS][COLS];

        clickCount = 0;

        setLayout(new GridLayout(ROWS, COLS));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                JButton button = new JButton();

                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.WHITE);
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));

                final int selectedColumn = col;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playMove(selectedColumn);
                    }
                });

                buttons[row][col] = button;
                add(button);
            }
        }

        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void playMove(int column) {

        int freeRow = -1;

        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][column] == 0) {
                freeRow = r;
                break;
            }
        }

        if (freeRow == -1) {
            return;
        }

        clickCount++;

        int player;

        if (clickCount % 2 == 1) {
            player = 1;
        } else {
            player = 2;
        }

        if (player == 1) {

            ageBlueTokens();

            board[freeRow][column] = 1;
            blueAge[freeRow][column] = 1;

        } else {

            ageRedTokens();

            board[freeRow][column] = 2;
            redAge[freeRow][column] = 1;
        }

        removeBlue11();
        removeRed11();

        updateLabels();

        boolean blueWins = checkWin(1);
        boolean redWins = checkWin(2);

        if (blueWins && redWins) {

            JOptionPane.showMessageDialog(
                this,
                "Neodloceno! Oba igralca imata 4 v vrsto."
            );

            resetBoard();
        }
        else if (blueWins) {

            JOptionPane.showMessageDialog(
                this,
                "Modri igralec je zmagal!"
            );

            resetBoard();
        }
        else if (redWins) {

            JOptionPane.showMessageDialog(
                this,
                "Rdeci igralec je zmagal!"
            );

            resetBoard();
        }
    }

    private void ageBlueTokens() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (board[row][col] == 1) {
                    blueAge[row][col]++;
                }
            }
        }
    }

    private void ageRedTokens() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (board[row][col] == 2) {
                    redAge[row][col]++;
                }
            }
        }
    }

    private void removeBlue11() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (blueAge[row][col] == 11) {

                    board[row][col] = 0;
                    blueAge[row][col] = 0;

                    applyGravity(col);
                    return;
                }
            }
        }
    }

    private void removeRed11() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (redAge[row][col] == 11) {

                    board[row][col] = 0;
                    redAge[row][col] = 0;

                    applyGravity(col);
                    return;
                }
            }
        }
    }

    private void applyGravity(int col) {

        boolean moved = true;

        while (moved) {

            moved = false;

            for (int row = ROWS - 1; row > 0; row--) {

                if (board[row][col] == 0 &&
                    board[row - 1][col] != 0) {

                    board[row][col] = board[row - 1][col];
                    board[row - 1][col] = 0;

                    blueAge[row][col] = blueAge[row - 1][col];
                    blueAge[row - 1][col] = 0;

                    redAge[row][col] = redAge[row - 1][col];
                    redAge[row - 1][col] = 0;

                    moved = true;
                }
            }
        }
    }

    private void updateLabels() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (board[row][col] == 0) {

                    buttons[row][col].setText("");
                    buttons[row][col].setBackground(Color.LIGHT_GRAY);

                }
                else if (board[row][col] == 1) {

                    buttons[row][col].setBackground(Color.BLUE);

                    buttons[row][col].setText(
                        String.valueOf(blueAge[row][col])
                    );

                }
                else {

                    buttons[row][col].setBackground(Color.RED);

                    buttons[row][col].setText(
                        String.valueOf(redAge[row][col])
                    );
                }
            }
        }
    }

    private boolean checkWin(int player) {

        // vodoravno
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {

                if (board[row][col] == player &&
                    board[row][col + 1] == player &&
                    board[row][col + 2] == player &&
                    board[row][col + 3] == player) {

                    return true;
                }
            }
        }

        // navpicno
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col < COLS; col++) {

                if (board[row][col] == player &&
                    board[row + 1][col] == player &&
                    board[row + 2][col] == player &&
                    board[row + 3][col] == player) {

                    return true;
                }
            }
        }

        // diagonala \
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {

                if (board[row][col] == player &&
                    board[row + 1][col + 1] == player &&
                    board[row + 2][col + 2] == player &&
                    board[row + 3][col + 3] == player) {

                    return true;
                }
            }
        }

        // diagonala /
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col <= COLS - 4; col++) {

                if (board[row][col] == player &&
                    board[row - 1][col + 1] == player &&
                    board[row - 2][col + 2] == player &&
                    board[row - 3][col + 3] == player) {

                    return true;
                }
            }
        }

        return false;
    }

    private void resetBoard() {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                board[row][col] = 0;

                blueAge[row][col] = 0;
                redAge[row][col] = 0;

                buttons[row][col].setBackground(Color.LIGHT_GRAY);
                buttons[row][col].setText("");
            }
        }

        clickCount = 0;
    }

    public static void main(String[] args) {
        new Connect4();
    }
}