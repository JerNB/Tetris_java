import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

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
        setTitle("俄罗斯方块");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 添加重启快捷键
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restart");
        gamePanel.getActionMap().put("restart", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.restart();
            }
        });
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
