package swing.ch09;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 버블보블 스타일 미니 게임
 *
 * 사용 이미지
 * - images/backgroundMap.png
 * - images/playerL.png
 * - images/playerR.png
 * - images/playerDie.png
 * - images/playerRDie.png
 * - images/enemyL.png
 * - images/enemyR.png
 * - images/bubble.png
 * - images/bubbled.png
 * - images/bomb.png
 *
 * 조작
 * - ← → : 이동
 * - ↑ : 점프
 * - Space : 버블 발사
 * - R : 재시작
 */
public class MyFrame2 extends JFrame {

    public MyFrame2() {
        setTitle("버블보블 - Swing 미니게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyFrame2::new);
    }
}

class GamePanel extends JPanel implements ActionListener {

    // ---------------- 화면 ----------------
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    // ---------------- 물리/게임 설정 ----------------
    private static final int FLOOR_Y = 500;
    private static final int PLAYER_W = 70;
    private static final int PLAYER_H = 70;
    private static final int ENEMY_W = 60;
    private static final int ENEMY_H = 60;
    private static final int BUBBLE_W = 36;
    private static final int BUBBLE_H = 36;

    private static final int PLAYER_SPEED = 5;
    private static final int JUMP_POWER = 18;
    private static final double GRAVITY = 0.9;

    // ---------------- 이미지 ----------------
    private final Image backgroundImg = new ImageIcon("images/backgroundMap.png").getImage();

    private final Image playerL = getScaledImage("images/playerL.png", PLAYER_W, PLAYER_H);
    private final Image playerR = getScaledImage("images/playerR.png", PLAYER_W, PLAYER_H);
    private final Image playerDieL = getScaledImage("images/playerDie.png", PLAYER_W, PLAYER_H);
    private final Image playerDieR = getScaledImage("images/playerRDie.png", PLAYER_W, PLAYER_H);

    private final Image enemyL = getScaledImage("images/enemyL.png", ENEMY_W, ENEMY_H);
    private final Image enemyR = getScaledImage("images/enemyR.png", ENEMY_W, ENEMY_H);

    private final Image bubbleImg = getScaledImage("images/bubble.png", BUBBLE_W, BUBBLE_H);
    private final Image bubbledImg = getScaledImage("images/bubbled.png", ENEMY_W, ENEMY_H);
    private final Image bombImg = getScaledImage("images/bomb.png", 28, 28);

    // ---------------- 입력 ----------------
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean spacePressed;

    // ---------------- 게임 상태 ----------------
    private final Timer timer = new Timer(16, this); // 약 60FPS
    private Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<BubbleShot> bubbles = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();

    private int score = 0;
    private int stage = 1;
    private int shootCooldown = 0;
    private int bombSpawnTick = 0;

    private boolean gameOver = false;
    private boolean gameClear = false;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setDoubleBuffered(true);

        initGame();
        initKeyBindings();

        timer.start();
    }

    private void initGame() {
        player = new Player(120, FLOOR_Y - PLAYER_H);

        enemies.clear();
        bubbles.clear();
        bombs.clear();

        enemies.add(new Enemy(650, FLOOR_Y - ENEMY_H, -2));
        enemies.add(new Enemy(780, FLOOR_Y - ENEMY_H, 2));
        enemies.add(new Enemy(500, FLOOR_Y - ENEMY_H, -3));

        score = 0;
        shootCooldown = 0;
        bombSpawnTick = 0;
        gameOver = false;
        gameClear = false;
    }

    private void resetGame() {
        initGame();
    }

    private void initKeyBindings() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver || gameClear) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        resetGame();
                    }
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = true;
                    case KeyEvent.VK_RIGHT -> rightPressed = true;
                    case KeyEvent.VK_UP -> upPressed = true;
                    case KeyEvent.VK_SPACE -> spacePressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = false;
                    case KeyEvent.VK_RIGHT -> rightPressed = false;
                    case KeyEvent.VK_UP -> upPressed = false;
                    case KeyEvent.VK_SPACE -> spacePressed = false;
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gameClear) {
            updatePlayer();
            updateBubbles();
            updateEnemies();
            updateBombs();
            checkCollisions();
            spawnBombs();

            if (shootCooldown > 0) {
                shootCooldown--;
            }

            boolean allDead = true;
            for (Enemy enemy : enemies) {
                if (!enemy.dead) {
                    allDead = false;
                    break;
                }
            }
            if (allDead) {
                gameClear = true;
            }
        }

        repaint();
    }

    // ---------------- 업데이트 ----------------

    private void updatePlayer() {
        if (player.dead) {
            return;
        }

        // 좌우 이동
        player.vx = 0;
        if (leftPressed && !rightPressed) {
            player.vx = -PLAYER_SPEED;
            player.faceRight = false;
        } else if (rightPressed && !leftPressed) {
            player.vx = PLAYER_SPEED;
            player.faceRight = true;
        }

        // 점프
        if (upPressed && player.onGround) {
            player.vy = -JUMP_POWER;
            player.onGround = false;
        }

        // 버블 발사
        if (spacePressed && shootCooldown == 0) {
            fireBubble();
            shootCooldown = 18;
        }

        // 중력
        player.vy += GRAVITY;

        // 위치 반영
        player.x += player.vx;
        player.y += player.vy;

        // 좌우 벽
        if (player.x < 0) player.x = 0;
        if (player.x > WIDTH - PLAYER_W) player.x = WIDTH - PLAYER_W;

        // 바닥
        if (player.y >= FLOOR_Y - PLAYER_H) {
            player.y = FLOOR_Y - PLAYER_H;
            player.vy = 0;
            player.onGround = true;
        } else {
            player.onGround = false;
        }
    }

    private void fireBubble() {
        int dir = player.faceRight ? 1 : -1;
        int bx = player.faceRight ? player.x + PLAYER_W - 10 : player.x - BUBBLE_W + 10;
        int by = player.y + 15;
        bubbles.add(new BubbleShot(bx, by, dir));
    }

    private void updateBubbles() {
        Iterator<BubbleShot> it = bubbles.iterator();
        while (it.hasNext()) {
            BubbleShot bubble = it.next();
            bubble.update();

            if (bubble.x < -50 || bubble.x > WIDTH + 50 || bubble.life <= 0) {
                it.remove();
            }
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.dead) continue;

            if (enemy.trapped) {
                enemy.trappedTimer--;
                enemy.floatY += Math.sin(enemy.trappedTimer * 0.2) * 0.2;

                if (enemy.trappedTimer <= 0) {
                    enemy.trapped = false;
                }
                continue;
            }

            enemy.x += enemy.vx;

            if (enemy.x <= 0) {
                enemy.x = 0;
                enemy.vx *= -1;
            }
            if (enemy.x >= WIDTH - ENEMY_W) {
                enemy.x = WIDTH - ENEMY_W;
                enemy.vx *= -1;
            }

            if (enemy.vx < 0) {
                enemy.faceRight = false;
            } else {
                enemy.faceRight = true;
            }

            // 약간의 추적 느낌
            int distance = Math.abs(enemy.x - player.x);
            if (distance < 180) {
                if (player.x < enemy.x) {
                    enemy.vx = -Math.max(2, Math.abs(enemy.vx));
                } else {
                    enemy.vx = Math.max(2, Math.abs(enemy.vx));
                }
            }
        }
    }

    private void updateBombs() {
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb bomb = it.next();
            bomb.y += bomb.speedY;
            bomb.rotation += 0.15;

            if (bomb.y > HEIGHT) {
                it.remove();
            }
        }
    }

    private void spawnBombs() {
        bombSpawnTick++;
        if (bombSpawnTick >= 120) { // 약 2초마다
            bombSpawnTick = 0;
            int x = 80 + (int) (Math.random() * (WIDTH - 160));
            bombs.add(new Bomb(x, 0));
        }
    }

    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();

        // 버블 vs 적
        for (BubbleShot bubble : bubbles) {
            Rectangle bubbleRect = bubble.getBounds();
            for (Enemy enemy : enemies) {
                if (enemy.dead || enemy.trapped) continue;

                if (bubbleRect.intersects(enemy.getBounds())) {
                    enemy.trapped = true;
                    enemy.trappedTimer = 240; // 약 4초
                    bubble.life = 0;
                    score += 50;
                }
            }
        }

        // 플레이어 vs 적
        for (Enemy enemy : enemies) {
            if (enemy.dead) continue;

            if (playerRect.intersects(enemy.getBounds())) {
                if (enemy.trapped) {
                    enemy.dead = true;
                    score += 200;
                } else {
                    killPlayer();
                    return;
                }
            }
        }

        // 플레이어 vs 폭탄
        for (Bomb bomb : bombs) {
            if (playerRect.intersects(bomb.getBounds())) {
                killPlayer();
                return;
            }
        }
    }

    private void killPlayer() {
        if (player.dead) return;

        player.dead = true;
        gameOver = true;
    }

    // ---------------- 그리기 ----------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);
        drawFloor(g);
        drawBombs(g);
        drawBubbles(g);
        drawEnemies(g);
        drawPlayer(g);
        drawHud(g);

        if (gameOver) {
            drawCenterMessage(g, "GAME OVER", "R 키를 눌러 다시 시작");
        } else if (gameClear) {
            drawCenterMessage(g, "STAGE CLEAR!", "R 키를 눌러 다시 플레이");
        }
    }

    private void drawBackground(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, WIDTH, HEIGHT, this);
    }

    private void drawFloor(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRect(0, FLOOR_Y + 55, WIDTH, HEIGHT - FLOOR_Y);
        g2.dispose();
    }

    private void drawPlayer(Graphics g) {
        Image currentImage;
        if (player.dead) {
            currentImage = player.faceRight ? playerDieR : playerDieL;
        } else {
            currentImage = player.faceRight ? playerR : playerL;
        }
        g.drawImage(currentImage, player.x, player.y, this);
    }

    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.dead) continue;

            if (enemy.trapped) {
                g.drawImage(bubbledImg, enemy.x, (int) (enemy.y + enemy.floatY), this);
            } else {
                g.drawImage(enemy.faceRight ? enemyR : enemyL, enemy.x, enemy.y, this);
            }
        }
    }

    private void drawBubbles(Graphics g) {
        for (BubbleShot bubble : bubbles) {
            g.drawImage(bubbleImg, bubble.x, bubble.y, this);
        }
    }

    private void drawBombs(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        for (Bomb bomb : bombs) {
            int centerX = bomb.x + 14;
            int centerY = bomb.y + 14;
            g2.rotate(bomb.rotation, centerX, centerY);
            g2.drawImage(bombImg, bomb.x, bomb.y, this);
            g2.rotate(-bomb.rotation, centerX, centerY);
        }
        g2.dispose();
    }

    private void drawHud(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(15, 15, 270, 95, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("SCORE : " + score, 30, 45);
        g2.drawString("STAGE : " + stage, 30, 75);
        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        g2.drawString("← → 이동   ↑ 점프   SPACE 버블", 30, 100);

        g2.dispose();
    }

    private void drawCenterMessage(Graphics g, String title, String subText) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(WIDTH / 2 - 220, HEIGHT / 2 - 90, 440, 180, 30, 30);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 42));
        FontMetrics fm1 = g2.getFontMetrics();
        int x1 = (WIDTH - fm1.stringWidth(title)) / 2;
        g2.drawString(title, x1, HEIGHT / 2 - 10);

        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
        FontMetrics fm2 = g2.getFontMetrics();
        int x2 = (WIDTH - fm2.stringWidth(subText)) / 2;
        g2.drawString(subText, x2, HEIGHT / 2 + 40);

        g2.dispose();
    }

    // ---------------- 유틸 ----------------

    private Image getScaledImage(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    // ---------------- 내부 클래스 ----------------

    static class Player {
        int x, y;
        int vx, vy;
        boolean faceRight = true;
        boolean onGround = true;
        boolean dead = false;

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 8, y + 8, PLAYER_W - 16, PLAYER_H - 10);
        }
    }

    static class Enemy {
        int x, y;
        int vx;
        boolean faceRight = false;
        boolean trapped = false;
        int trappedTimer = 0;
        boolean dead = false;
        double floatY = 0;

        public Enemy(int x, int y, int vx) {
            this.x = x;
            this.y = y;
            this.vx = vx;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 6, y + 6, ENEMY_W - 12, ENEMY_H - 12);
        }
    }

    static class BubbleShot {
        int x, y;
        int dir;
        int life = 70;

        public BubbleShot(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        void update() {
            x += dir * 9;
            y -= 1;
            life--;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 4, y + 4, BUBBLE_W - 8, BUBBLE_H - 8);
        }
    }

    static class Bomb {
        int x, y;
        int speedY = 5;
        double rotation = 0;

        public Bomb(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Rectangle getBounds() {
            return new Rectangle(x + 4, y + 4, 20, 20);
        }
    }
}