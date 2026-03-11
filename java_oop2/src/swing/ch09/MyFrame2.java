package swing.ch09;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyFrame2 extends JFrame {

    private JLabel backgroundMap;
    private JLabel player;
    // 플레이어의 왼쪽 방향 이미지
    ImageIcon playerIconL = new ImageIcon("images/playerL.png");
    ImageIcon playerIconR = new ImageIcon("images/playerR.png");

    private final int MOVE_STEP = 10;
    // 이동 가능한 범위
    private final int MAX_X = 1000 - 100;
    private final int MAX_Y = 600 - 100;

    // MIN_X, MINY == 0;
    private final int MIN_POS = 0;

    public MyFrame2() {
        initData();
        setInitLayout();
        addEventListener();
    }

    private void initData() {
        setTitle("이미지 사용 연습");
        setSize(1000, 600);
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

    }

    private void setInitLayout() {
        setLayout(null); // 좌표 기반
        backgroundMap.add(player);
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

                x = Math.max(MIN_POS,Math.min(x, MAX_X));
                y = Math.max(MIN_POS,Math.min(y, MAX_Y));

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
