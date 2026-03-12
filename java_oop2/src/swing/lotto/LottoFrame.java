package swing.lotto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LottoFrame extends JFrame implements ActionListener {

    private JButton btn1;
    private JLabel label;
    private boolean show = false;
    private int[] result;

    private final int[] BALL_LOCATION = {100, 250, 400, 550, 700, 850};


    public LottoFrame() {
        initData();
        setInitLayout();
        addEventListener();

    }

    private void initData() {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel("Game Start 버튼을 눌러주세요.");
        label.setFont(new Font("Arial ", Font.ITALIC, 30));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.GRAY);

        btn1 = new JButton("Game Start");
        btn1.setFont(new Font("Arial", Font.BOLD, 20));
        btn1.setPreferredSize(new Dimension(1000, 50));
    }

    private void setInitLayout() {
        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(1000, 50));
        add(btn1, BorderLayout.NORTH);
        add(label, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addEventListener() {
        btn1.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        remove(label);
        show = true;
        repaint();

        LottoRandomNumber lotto = new LottoRandomNumber();
        result = lotto.arr;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 1 ~ 10 노
        // 11 ~ 20 파
        // 21 ~ 30 빨
        // 31 ~ 40 검
        // 41 ~ 45 초

        if (show) {
            Arrays.sort(result);
            for (int i = 0; i < result.length; i++) {
                if (result[i] <= 10) g.setColor(Color.orange);
                else if (result[i] <= 20) g.setColor(new Color(0, 0, 150));
                else if (result[i] <= 30) g.setColor(new Color(200, 0, 0));
                else if (result[i] <= 40) g.setColor(Color.black);
                else if (result[i] <= 45) g.setColor(new Color(0, 200, 0));
                g.fillOval(BALL_LOCATION[i], 250, 100, 100);

                g.setColor(Color.black);
                g.drawOval(BALL_LOCATION[i], 250, 100, 100);


                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.setColor(Color.white);
                if (result[i] >= 10) {
                    g.drawString(String.valueOf(result[i]), BALL_LOCATION[i] + 18, 315);
                } else {
                    g.drawString(String.valueOf(result[i]), BALL_LOCATION[i] + 30, 315);
                }

            }
        }
    }

    public static void main(String[] args) {
        new LottoFrame();
    }
}