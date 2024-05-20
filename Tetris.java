import javax.swing.JFrame;

public class Tetris extends JFrame {
    private static final long serialVersionUID = 1L;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        add(new GamePanel());

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Tetris game = new Tetris();
                game.setVisible(true);
            }
        });
    }
}
