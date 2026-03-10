package swing.ch08;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// 키 리스너를 구현하여 키보드 입력을 받아서 라벨을 움직이는 클래스
public class MoveCharFrame2 extends JFrame {

    private JLabel label = new JLabel();
    private int count = 0;

    private final int MOVE_STEP = 10;
    private final int INIT_X = 225;
    private final int INIT_Y = 200;
    private final int FRAME_SIZE = 500;

    public MoveCharFrame2() {
        initData();
        setInitLayOut();
        addEventListener();
    }

    private void initData() {
        setTitle("방향키로 별 움직이기 실습 | 이동 횟수 :");
        setSize(FRAME_SIZE, FRAME_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        label = new JLabel("★");
        label.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        label.setSize(50, 50);
        label.setLocation(INIT_X, INIT_Y); // 초기 시작 위치 (중앙 부근)
    }

    // 페인트 기능
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ///  응용 - 테두리 선 그리기
        g.drawLine(0, 50, 500, 50); // 위
        g.drawLine(0, 450, 500, 450); // 아래
        g.drawLine(50, 0, 50, 500); // 좌
        g.drawLine(450, 0, 450, 500); // 우


    }

    private void setInitLayOut() {
        // 중요
        setLayout(null);
        add(label);
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
                int x = label.getX(); // 현재 자신의 x 좌표값을 반환
                int y = label.getY(); // 현재 자신의 y 좌표값을 반환


                if (keyCode == KeyEvent.VK_LEFT) {
                    x -= MOVE_STEP;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    x += MOVE_STEP;
                } else if (keyCode == KeyEvent.VK_UP) {
                    y -= MOVE_STEP;

                } else if (keyCode == KeyEvent.VK_DOWN) {
                    y += MOVE_STEP;

                    // esc 키 눌리면 초기화
                } else if (keyCode == KeyEvent.VK_ESCAPE) {
                    x = INIT_X;
                    y = INIT_Y;
                }

                if (x > 350) {
                    x = 350;
                }

                if (y > 350) {
                    y = 350;
                }

                if (x < 50) {
                    x = 50;
                }

                if (y < 50) {
                    y = 50;
                }

                label.setLocation(x, y);

                label.setText("☆");
                // 카운트 1 증가
                count++;
                // 타이틀 업데이트
                setTitle("별 움직이기 | 이동 횟수 : " + count + "/ 좌표 : X = " + x + ", Y = " + y);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                label.setText("★");
            }
        });
        // 필수
        // 키보드 이벤트를 받기 위해 프레임이 포커스를 가질 수 있게 합니다.
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new MoveCharFrame2();
    }
}
