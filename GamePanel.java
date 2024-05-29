import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int INITIAL_DELAY = 400;
    private final int PERIOD_INTERVAL = 600;
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
    private List<Piece> pieceList;
    private Piece preservedPiece;
    private Timer moveTimer;

    public GamePanel() {
        pieceList = new ArrayList<>();
        initBoard();
    }

    public List<Piece> getPieceList() {
        return pieceList;
    }

    private void initBoard() {
        setFocusable(true);
        setVisible(true);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        setFocusTraversalKeysEnabled(false);

        board = new Board(this);
        setupKeyBinding("pressed P", KeyEvent.VK_P, this::pause);
        setupKeySpeedBinding("pressed LEFT", KeyEvent.VK_LEFT, () -> tryMove(curPiece, curX - 1, curY));
        setupKeySpeedBinding("pressed RIGHT", KeyEvent.VK_RIGHT, () -> tryMove(curPiece, curX + 1, curY));
        setupKeySpeedBinding("pressed DOWN", KeyEvent.VK_DOWN, () -> tryMoveWithUpdate(curPiece, curX, curY - 1));
        setupKeyBinding("pressed UP", KeyEvent.VK_UP, () -> tryMove(curPiece.rotateRight(), curX, curY));
        setupKeyBinding("Shift", KeyEvent.VK_SHIFT, () -> preserve()); // DOWN
        setupKeyBinding("pressed SPACE", KeyEvent.VK_SPACE, this::dropDown); // SPACE -> confirm
        // setupKeyBinding("pressed SHIFT", KeyEvent.VK_SHIFT, this::speedDown); //
        // SHIFT -> dropDown
        setupKeyBinding("pressed Z", KeyEvent.VK_Z, () -> tryMove(curPiece.rotateLeft(), curX, curY)); // Z -> tSpin

        // 尝试请求焦点
        requestFocus();
        requestFocusInWindow();
        requestFocus(true);
        preservedPiece = new Piece();
        preservedPiece.setShape(Shape.NoShape);
        curPiece = new Piece();
        holdPiece = new Piece();
        holdPiece.setShape(Shape.NoShape);
        holdUsed = false;
        timer = new Timer(PERIOD_INTERVAL, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused) {
            return;
        }
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    // private void start() {
    // if (isPaused) {
    // return;
    // }

    // isStarted = true;
    // isFallingFinished = false;
    // numLinesRemoved = 0;
    // board.clear();

    // newPiece();
    // timer.start();
    // }

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

    private void preserve() {
        if (preservedPiece.getShape() != Shape.NoShape) {
            Piece temp = curPiece;
            curPiece = preservedPiece;
            preservedPiece = temp;
        } else {
            preservedPiece = curPiece;
            curPiece = new Piece();
        }
    }

    private void doDrawing(Graphics g) {
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i <= BOARD_WIDTH; i++) {
            g.drawLine(i * squareWidth(), 0, i * squareWidth(), BOARD_HEIGHT * squareHeight());
        }
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            g.drawLine(0, i * squareHeight(), BOARD_WIDTH * squareWidth(), i * squareHeight());
        }

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
         // 如果游戏暂停，显示暂停标志
         if (isPaused) {
             g.setColor(Color.RED);
             g.drawString("Paused", getWidth() - 60, 20);
         }
    }

    public void displayScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + board.getScore(), 5, 15);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        drawGhostPiece(g);
        displayScore(g);
            // drawGhostPiece(g);
            // 如果游戏暂停，显示暂停标志
            if (isPaused) {
                g.setColor(Color.RED);
                g.drawString("Paused", getWidth() - 60, 20);
            }
    }

    private void dropDown() {
    int newY = curY;
    while (newY > 0) {
        if (!tryMove(curPiece, curX, newY - 1)) {
            break;
        }

        newY--;
    }

    board.updateScore(70);
    pieceDropped();
}



    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void pieceDropped() {
        board.addPiece(curPiece, curX, curY);
        board.updateScore(10);
        if (!isFallingFinished) {
            newPiece();
        }
    }

    public void newPiece() {
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

    private boolean tryMoveWithUpdate(Piece newPiece, int newX, int newY) {
        board.updateScore(5);
        return tryMove(newPiece, newX, newY);
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

    private boolean checkMove(Piece newPiece, int newX, int newY) {
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

        return true;
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private void drawSquare(Graphics g, int x, int y, Shape shape) {
        // 使用默认颜色
        Color defaultColor = Color.BLACK;
        drawSquare(g, x, y, shape, defaultColor);
    }

    private void drawSquare(Graphics g, int x, int y, Shape shape, Color diyColor) {
        Color purple = new Color(92, 68, 212);
        Color cyan = new Color(3, 252, 198);
        Color red = new Color(252, 88, 88);
        Color yellow = new Color(250, 235, 25);
        Color green = new Color(177, 237, 81);
        Color orange = new Color(247, 167, 87);
        Color pink = new Color(243, 115, 250);
        Color colors[] = { Color.BLACK, red, green, cyan, pink, yellow, orange, purple };
        Color color = (diyColor != Color.BLACK) ? diyColor : colors[shape.ordinal()];

        // 绘制方块主体
        g.setColor(color);
        g.fillRect(x, y, squareWidth(), squareHeight());

        // 绘制方块边框
        g.setColor(color.darker().darker());
        g.drawRect(x, y, squareWidth(), squareHeight());

        // 绘制 3D 效果
        g.setColor(color.brighter().brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker().darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);

        // 绘制内部方块
        if (color.getAlpha() == 255) {
            int centerX = x + squareWidth() / 2;
            int centerY = y + squareHeight() / 2;
            int innerSquareSize = Math.min(squareWidth(), squareHeight()) / 3;

            g.setColor(color.darker().darker());
            g.drawLine(centerX - innerSquareSize / 2, centerY - innerSquareSize / 2, centerX + innerSquareSize / 2,
                    centerY - innerSquareSize / 2);
            g.drawLine(centerX - innerSquareSize / 2, centerY - innerSquareSize / 2, centerX - innerSquareSize / 2,
                    centerY + innerSquareSize / 2);
            g.setColor(color.brighter().brighter());
            g.drawLine(centerX + innerSquareSize / 2, centerY - innerSquareSize / 2, centerX + innerSquareSize / 2,
                    centerY + innerSquareSize / 2);
            g.drawLine(centerX - innerSquareSize / 2, centerY + innerSquareSize / 2, centerX + innerSquareSize / 2,
                    centerY + innerSquareSize / 2);

            g.setColor(color);
            g.fillRect(centerX - innerSquareSize / 2, centerY - innerSquareSize / 2, innerSquareSize, innerSquareSize);
        }
    }
    // private void hold() {
    // if (holdUsed) {
    // return;
    // }

    // Piece temp = new Piece();
    // temp.setShape(curPiece.getShape());
    // if (holdPiece.getShape() == Shape.NoShape) {
    // newPiece();
    // } else {
    // curPiece.setShape(holdPiece.getShape());
    // curX = BOARD_WIDTH / 2;
    // curY = BOARD_HEIGHT - 1 + curPiece.minY();
    // }
    // holdPiece.setShape(temp.getShape());
    // holdUsed = true;
    // repaint();
    // }

    private void drawGhostPiece(Graphics g) {
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();
        g.setColor(new Color(255, 255, 255));

        if (curPiece.getShape() != Shape.NoShape) {
            int ghostY = curY;
            while (ghostY > 0) {
                if (!checkMove(curPiece, curX, ghostY - 1)) {
                    break;
                }
                ghostY--;
            }

            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = ghostY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape(), new Color(255, 255, 255, 128));
            }
        }
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

    private void setupKeySpeedBinding(String name, int keyEvent, Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, false), name + "Pressed");
        actionMap.put(name + "Pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                if (moveTimer != null) {
                    moveTimer.stop();
                }
                moveTimer = new Timer(100, evt -> action.run());
                moveTimer.start();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, true), name + "Released");
        actionMap.put(name + "Released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (moveTimer != null) {
                    moveTimer.stop();
                    moveTimer = null;
                }
            }
        });
    }
}

