import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Tetris extends JFrame {
    private static final long serialVersionUID = 1L;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        setFocusable(true);
        setVisible(true);
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        setTitle("Tetris");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Tetris game = new Tetris();
                game.setVisible(true);

                // 延迟请求焦点
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        game.getContentPane().getComponent(0).requestFocusInWindow();
                        game.getContentPane().getComponent(0).requestFocus();
                    }
                });
            }
        });
    }
}
