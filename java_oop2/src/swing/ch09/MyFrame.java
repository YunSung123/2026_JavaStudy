package swing.ch09;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyFrame extends JFrame {
    private JLabel backgroundMap;
    private JLabel player;
    private int count = 0;
    private ImageIcon playerIconL;
    private ImageIcon playerIconR;

    private final int MOVE_STEP = 10;
    private final int INIT_X = 100;
    private final int INIT_Y = 100;


    public MyFrame() {
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
        ImageIcon playerIcon = new ImageIcon("images/playerL.png");
        player = new JLabel(playerIcon);
        player.setSize(100, 100);
        player.setLocation(200, 200);

        // 플레이어 설정
        playerIconL = new ImageIcon("images/playerL.png");
        player = new JLabel(playerIconL);
        player.setSize(100, 100);
        player.setLocation(200, 200);

        // 플레이어 설정
        playerIconR = new ImageIcon("images/playerR.png");
        player = new JLabel(playerIconR);
        player.setSize(100, 100);
        player.setLocation(200, 200);
    }

    private void setInitLayout() {
        setLayout(null);
        backgroundMap.add(player);
        add(backgroundMap);
        setVisible(true);
    }

    private void addEventListener() {


        // 1. 프레임에 키 리스너를 등록
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                int x = player.getX(); // 현재 자신의 x 좌표값을 반환
                int y = player.getY(); // 현재 자신의 y 좌표값을 반환


                if (keyCode == KeyEvent.VK_LEFT) {
                    x -= MOVE_STEP;
                    player.setIcon(playerIconL);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    x += MOVE_STEP;
                    player.setIcon(playerIconR);
                } else if (keyCode == KeyEvent.VK_UP) {
                    y -= MOVE_STEP;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    y += MOVE_STEP;
                    // esc 키 눌리면 초기화
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    x = INIT_X;
                    y = INIT_Y;
                }

                player.setLocation(x, y);
                // 카운트 1 증가
                count++;
                // 타이틀 업데이트
                setTitle("별 움직이기 | 이동 횟수 : " + count + "/ 좌표 : X = " + x + ", Y = " + y);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        // 필수
        // 키보드 이벤트를 받기 위해 프레임이 포커스를 가질 수 있게 합니다.
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    // 테슼트 코드
    public static void main(String[] args) {
        new MyFrame();
    }


}
