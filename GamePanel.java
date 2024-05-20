import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int INITIAL_DELAY = 400;
    private final int PERIOD_INTERVAL = 300;
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private Piece curPiece;
    private Piece holdPiece;
    private boolean holdUsed;
    private Board board;

    public GamePanel() {
        initBoard();
    }

    private void initBoard() {
        setFocusable(true);
        setVisible(true);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        setFocusTraversalKeysEnabled(false);

        board = new Board(this);
        setupKeyBinding("pressed P", KeyEvent.VK_P, this::pause);
        setupKeyBinding("pressed A", KeyEvent.VK_A, () -> tryMove(curPiece, curX - 1, curY));
        setupKeyBinding("pressed D", KeyEvent.VK_D, () -> tryMove(curPiece, curX + 1, curY));
        setupKeyBinding("pressed W", KeyEvent.VK_W, () -> tryMove(curPiece.rotateRight(), curX, curY));
        setupKeyBinding("pressed S", KeyEvent.VK_S, () -> tryMove(curPiece.rotateLeft(), curX, curY));
        setupKeyBinding("pressed SPACE", KeyEvent.VK_SPACE, this::dropDown);
        setupKeyBinding("pressed SHIFT", KeyEvent.VK_SHIFT, this::hold);
        setupKeyBinding("pressed C", KeyEvent.VK_C, this::hold);

        // 尝试请求焦点
        requestFocus();
        requestFocusInWindow();
        requestFocus(true);
        System.out.println("Focus obtained: " + isFocusOwner());
        curPiece = new Piece();
        holdPiece = new Piece();
        holdPiece.setShape(Shape.NoShape);
        holdUsed = false;
        timer = new Timer(PERIOD_INTERVAL, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        board.clear();

        newPiece();
        timer.start();
    }

    private void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;

        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }

        repaint();
    }

    private void doDrawing(Graphics g) {
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Shape shape = board.shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Shape.NoShape) {
                    drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Shape.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void dropDown() {
        int newY = curY;

        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }

            newY--;
        }

        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void pieceDropped() {
        board.addPiece(curPiece, curX, curY);

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();
        holdUsed = false;

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Shape.NoShape);
            timer.stop();
            isStarted = false;
        }
    }

    private boolean tryMove(Piece newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }

            if (board.shapeAt(x, y) != Shape.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();
        return true;
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private void drawSquare(Graphics g, int x, int y, Shape shape) {
        Color colors[] = { Color.BLACK, Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.PINK,
                Color.RED };
        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x, y, squareWidth(), squareHeight());
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    private void hold() {
        if (holdUsed) {
            return;
        }

        Piece temp = new Piece();
        temp.setShape(curPiece.getShape());
        if (holdPiece.getShape() == Shape.NoShape) {
            newPiece();
        } else {
            curPiece.setShape(holdPiece.getShape());
            curX = BOARD_WIDTH / 2;
            curY = BOARD_HEIGHT - 1 + curPiece.minY();
        }
        holdPiece.setShape(temp.getShape());
        holdUsed = true;
        repaint();
    }

    private void setupKeyBinding(String name, int keyEvent, Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, false), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
