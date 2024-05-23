import java.util.ArrayList;
import java.util.List;

public class Board {
    private Shape[][] board;
    private GamePanel gamePanel;

    public Board(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        board = new Shape[10][20];
        clear();
    }

    public void clear() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                board[j][i] = Shape.NoShape;
            }
        }
    }

    public void addPiece(Piece piece, int x, int y) {
        for (int i = 0; i < 4; i++) {
            int px = x + piece.x(i);
            int py = y - piece.y(i);
            board[px][py] = piece.getShape();
        }

        removeFullLines();
    }

    public void addScore(int linesRemoved) {
        int baseScore;
        switch (linesRemoved) {
            case 1:
                baseScore = 100;
                break;
            case 2:
                baseScore = 300;
                break;
            case 3:
                baseScore = 500;
                break;
            case 4:
                baseScore = 800;
                break;
            default:
                return;
        }

        int bonus = 0;
        // Add bonus for T-Spin, etc.
        // ...
    }

    public Shape shapeAt(int x, int y) {
        return board[x][y];
    }

    private void removeFullLines() {
        List<Integer> fullLines = new ArrayList<>();

        for (int i = 19; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < 10; j++) {
                if (board[j][i] == Shape.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                fullLines.add(i);
            }
        }

        for (int i : fullLines) {
            for (int k = i; k < 19; k++) {
                for (int j = 0; j < 10; j++) {
                    board[j][k] = board[j][k + 1];
                }
            }

            for (int j = 0; j < 10; j++) {
                board[j][19] = Shape.NoShape;
            }
        }

        if (!fullLines.isEmpty()) {
            gamePanel.repaint();
            addScore(fullLines.size()); // Add score based on the number of lines removed
        }
    }
}
