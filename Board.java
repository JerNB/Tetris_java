import java.util.ArrayList;
import java.util.List;

public class Board {
    private Shape[][] board;
    private int score = 0;
    private GamePanel gamePanel;
    private boolean isTSpin = false;
    private static final int T_SPIN_BONUS = 400;
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;

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
        switch (linesRemoved) {
            case 1:
                score += 1000;
                break;
            case 2:
                score += 3000;
                break;
            case 3:
                score += 5000;
                break;
            case 4:
                score += 8000;
                break;
            default:
                return;
        }

        int bonus = 0;
        // Add bonus for T-Spin, etc.
        // ...
    }

    public int getScore() {
        return score;
    }

    public void updateScore(int points) {
        score += points;
    }

    public void updateScoreWithTSpin(int lines, boolean isTSpin) {
        int points = 0;
        
        if (isTSpin) {
            points += T_SPIN_BONUS;
            switch (lines) {
                case 1: points += 800; break;
                case 2: points += 1200; break;
                case 3: points += 1600; break;
            }
        } else {
            switch (lines) {
                case 1: points += 100; break;
                case 2: points += 300; break;
                case 3: points += 500; break;
                case 4: points += 800; break;
            }
        }
        
        score += points;
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

    public void resetScore() {
        score = 0;
    }

    public boolean checkTSpin(Piece piece, int pieceX, int pieceY) {
        if (piece.getShape() != Shape.TShape) {
            return false;
        }
        
        // T-Spin 检测需要满足两个条件：
        // 1. 最后一个操作是旋转
        // 2. T块的三个角被占用
        
        int filledCorners = 0;
        int totalCorners = 0;
        
        // 检查T块四个角的位置
        int[][] cornerChecks = {
            {pieceX - 1, pieceY - 1}, // 左上
            {pieceX + 1, pieceY - 1}, // 右上
            {pieceX - 1, pieceY + 1}, // 左下
            {pieceX + 1, pieceY + 1}  // 右下
        };
        
        for (int[] pos : cornerChecks) {
            if (pos[0] >= 0 && pos[0] < BOARD_WIDTH && 
                pos[1] >= 0 && pos[1] < BOARD_HEIGHT) {
                totalCorners++;
                if (shapeAt(pos[0], pos[1]) != Shape.NoShape) {
                    filledCorners++;
                }
            }
        }
        
        // 至少三个角被占用，且最后一个操作是旋转
        return filledCorners >= 3 && totalCorners >= 4;
    }
}
