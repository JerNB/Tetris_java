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

    public Shape shapeAt(int x, int y) {
        return board[x][y];
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = 0; i < 20; i++) {
            boolean lineIsFull = true;

            for (int j = 0; j < 10; j++) {
                if (board[j][i] == Shape.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < 19; k++) {
                    for (int j = 0; j < 10; j++) {
                        board[j][k] = board[j][k + 1];
                    }
                }

                for (int j = 0; j < 10; j++) {
                    board[j][19] = Shape.NoShape;
                }
            }
        }

        if (numFullLines > 0) {
            gamePanel.repaint();
        }
    }
}
