package swing.ch10;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 플레이어 이동 + 적군 자동 이동(Thread 활용)
 * <p>
 * -Thread : 적군을 백그라운드에서 자동으로 움직이게 하는 별도 작업자
 * -Runnable : Thread가 실행할 작업을 정의하는 인터페이스
 */
public class MyFrame2 extends JFrame {

    // -- 배경 & 플레이어 -------------------------
    private JLabel backgroundMap;
    private JLabel player;
    // 플레이어의 왼쪽 방향 이미지
    ImageIcon playerIconL = new ImageIcon("images/playerL.png");
    ImageIcon playerIconR = new ImageIcon("images/playerR.png");

    // -- 적군 &  -------------------------
    private JLabel enemy;
    private ImageIcon enemyIconL = new ImageIcon("images/enemyL.png");
    private ImageIcon enemyIconR = new ImageIcon("images/enemyR.png");

    private final int MOVE_STEP = 10; // 플레이어 이동 픽셀
    private final int ENEMY_STEP = 5;
    private final int DELAY_MS = 50; // 적군 이동 간격(ms) - 숫자가 적을 수록 빠름

    // 이동 가능한 범위
    private final int MAX_X = 1000 - 100;
    private final int MAX_Y = 600 - 100;

    // MIN_X, MINY == 0;
    private final int MIN_POS = 0;

    public MyFrame2() {
        initData();
        setInitLayout();
        addEventListener();
        startEnemyThread();
    }

    private void startEnemyThread() {
        Runnable enemyTask = new Runnable() {
            @Override
            public void run() {
                boolean movingRight = true;

                while (true) { // 게임이 끝날 때 까지 계속 반복
                    int x = enemy.getX();
                    if (movingRight) {
                        x += ENEMY_STEP;
                        enemy.setIcon(enemyIconR);
                    } else {
                        x -= ENEMY_STEP;
                        enemy.setIcon(enemyIconL);
                    }

                    if (x >= 800) {
                        movingRight = false;
                    } else if (x <= 0) {
                        movingRight = true;
                    }
                    // 변경된 x 값을 다시 설정
                    enemy.setLocation(x, enemy.getY());

                    try {
                        Thread.sleep(DELAY_MS / 10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        Thread thread = new Thread(enemyTask);
        thread.start();
    }

    private void initData() {
        setTitle("이미지 사용 연습");
        setSize(1000, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 배경 이미지 설정
        ImageIcon backgroundIcon = new ImageIcon("images/backgroundMap.png");
        backgroundMap = new JLabel(backgroundIcon);
        backgroundMap.setSize(1000, 600);
        backgroundMap.setLocation(0, 0);

        // 플레이어 설정
        player = new JLabel(playerIconL); // 초기 상태값
        player.setSize(100, 100);
        player.setLocation(200, 200);

        enemy = new JLabel(enemyIconL);
        enemy.setSize(100, 100);
        enemy.setLocation(200, 500);
    }

    private void setInitLayout() {
        setLayout(null); // 좌표 기반
        backgroundMap.add(player);
        backgroundMap.add(enemy);
        add(backgroundMap);
        setVisible(true);
    }

    private void addEventListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int x = player.getX();
                int y = player.getY();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        y -= MOVE_STEP;
                        break;
                    case KeyEvent.VK_LEFT:
                        player.setIcon(playerIconL);
                        x -= MOVE_STEP;
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.setIcon(playerIconR);
                        x += MOVE_STEP;
                        break;
                    case KeyEvent.VK_DOWN:
                        y += MOVE_STEP;
                        break;
                    default:
                        return;
                }

                x = Math.max(MIN_POS, Math.min(x, MAX_X));
                y = Math.max(MIN_POS, Math.min(y, MAX_Y));

                player.setLocation(x, y);
            } // end of Key pressed

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        // 추천 옵션 - 필수
        setFocusable(true);
        requestFocusInWindow();
    }

    // 테스트 코드 (메인 쓰레드)
    public static void main(String[] args) {
        new MyFrame2();
    }

}
