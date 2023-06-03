import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JFrame {
    private Snake snake;
    private Timer timer;
    private static final int DELAY = 100;
    private List<Cube> cubes;
    private Random random;
    private JLabel label1;

    int score = 0;
    public SnakeGame() {
        snake = new Snake();
        timer = new Timer(DELAY, new GameLoop());
        cubes = new ArrayList<>();
        random = new Random();



        setTitle("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);

        label1 = new JLabel("score: "+score);
        gamePanel.add(label1);
        label1.setForeground(Color.WHITE);

        addKeyListener(new SnakeController());
        pack();

        setVisible(true);
        timer.start();
    }

    private class GamePanel extends JPanel {
        private static final int WIDTH = 400;
        private static final int HEIGHT = 400;
        private static final int DOT_SIZE = 10;

        public GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawSnake(g);
            for (Cube cube : cubes) {
                cube.draw(g);
            }
        }

        private void drawSnake(Graphics g) {
            for (int i = 0; i < snake.size; i++) {
                g.setColor(Color.GREEN);
                g.fillRect(snake.x[i], snake.y[i], DOT_SIZE, DOT_SIZE);
            }
        }
    }

    private class GameLoop implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            snake.move();
            if (snake.collidesWithItself() || snake.hitsWall()) {
                timer.stop();
                JOptionPane.showMessageDialog(SnakeGame.this, "Game Over!\nScore: "+score);
                System.exit(0);
            }

            for (Cube cube : cubes) {
                if (snake.intersects(cube)) {
                    snake.setShouldGrow(true);
                    cubes.remove(cube);
                    score++;
                    label1.setText("Score: "+score);
                    break;
                }
            }

            if (cubes.size() < 3) {
                generateCube();
            }

            repaint();
        }
    }

    private class SnakeController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_UP && snake.getDirection() != Direction.DOWN) {
                snake.setDirection(Direction.UP);
            } else if (keyCode == KeyEvent.VK_DOWN && snake.getDirection() != Direction.UP) {
                snake.setDirection(Direction.DOWN);
            } else if (keyCode == KeyEvent.VK_LEFT && snake.getDirection() != Direction.RIGHT) {
                snake.setDirection(Direction.LEFT);
            } else if (keyCode == KeyEvent.VK_RIGHT && snake.getDirection() != Direction.LEFT) {
                snake.setDirection(Direction.RIGHT);
            }
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private class Snake {
        private static final int INITIAL_SIZE = 3;
        private static final int DOT_SIZE = 10;

        private int[] x;
        private int[] y;
        private int size;
        private Direction direction;
        private boolean shouldGrow;

        public Snake() {
            x = new int[100];
            y = new int[100];
            size = INITIAL_SIZE;
            direction = Direction.RIGHT;
            shouldGrow = false;

            for (int i = 0; i < size; i++) {
                x[i] = 50 - i * DOT_SIZE;
                y[i] = 50;
            }
        }

        public void move() {
            if (shouldGrow) {
                grow();
                shouldGrow = false;
            } else {
                for (int i = size - 1; i > 0; i--) {
                    x[i] = x[i - 1];
                    y[i] = y[i - 1];
                }
            }

            if (direction == Direction.UP) {
                y[0] -= DOT_SIZE;
            } else if (direction == Direction.DOWN) {
                y[0] += DOT_SIZE;
            } else if (direction == Direction.LEFT) {
                x[0] -= DOT_SIZE;
            } else if (direction == Direction.RIGHT) {
                x[0] += DOT_SIZE;
            }
        }

        private void grow() {
            int lastSegmentIndex = size - 1;
            x[size] = x[lastSegmentIndex];
            y[size] = y[lastSegmentIndex];
            size++;
        }

        public boolean collidesWithItself() {
            int headX = x[0];
            int headY = y[0];

            for (int i = 1; i < size; i++) {
                if (headX == x[i] && headY == y[i]) {
                    return true;
                }
            }

            return false;
        }

        public boolean hitsWall() {
            int gamePanelWidth = GamePanel.WIDTH;
            int gamePanelHeight = GamePanel.HEIGHT;

            return x[0] < 0 || x[0] >= gamePanelWidth || y[0] < 0 || y[0] >= gamePanelHeight;
        }

        public boolean intersects(Cube cube) {
            int headX = x[0];
            int headY = y[0];
            int cubeX = cube.getX();
            int cubeY = cube.getY();

            return headX == cubeX && headY == cubeY;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public void setShouldGrow(boolean shouldGrow) {
            this.shouldGrow = shouldGrow;
        }
    }

    private class Cube {
        private static final int SIZE = 10;

        private int x;
        private int y;

        public Cube(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillRect(x, y, SIZE, SIZE);
        }
    }

    private void generateCube() {
        int cubeX = random.nextInt(GamePanel.WIDTH / GamePanel.DOT_SIZE) * GamePanel.DOT_SIZE;
        int cubeY = random.nextInt(GamePanel.HEIGHT / GamePanel.DOT_SIZE) * GamePanel.DOT_SIZE;

        for (Cube cube : cubes) {
            if (cube.getX() == cubeX && cube.getY() == cubeY) {
                // Cube already exists at the generated position, try again
                generateCube();
                return;
            }
        }

        cubes.add(new Cube(cubeX, cubeY));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}
