package swing.lotto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;


public class LottoFrame2 extends JFrame {

    public JButton button;
    private JTextArea area;
    private boolean showGraphics = false;

    public LottoFrame2() {
        initData();
        setInitData();
        setInitLayout();

    }



    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (showGraphics) {
            g.setColor(Color.yellow);
            g.fillOval(150, 300, 100, 100);
            g.setColor(Color.black);
            g.drawOval(150, 300, 100, 100);
            g.setColor(Color.red);
            g.fillOval(300, 300, 100, 100);
            g.setColor(Color.blue);
            g.fillOval(450, 300, 100, 100);
            g.fillOval(600, 300, 100, 100);
            g.fillOval(750, 300, 100, 100);
        }
        //showGraphics = false;
       // repaint();
    }


    public void initData() {
        setTitle("Lotto Game");
        setSize(1000, 600);
        setBackground(new Color(244, 244, 244));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        button = new JButton("Game start");
        area = new JTextArea("Game Start 버튼을 눌러주세요");
        button.setText("Game start");
        button.setSize(1000, 50);
        button.setLocation(0, 0);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("패인트 그리기 시작");
                area.setText("");
                showGraphics = true;
                repaint();
            }
        });

        area.setText("Game Start 버튼을 눌러주세요");
        area.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        area.setSize(450, 50);
        area.setBackground(new Color(244, 244, 244));
        area.setLocation(300, 250);

    }

    public void setInitLayout() {
        setLayout(null);
        super.add(button);
        super.add(area);
        setVisible(true);

    }


    public void setInitData() {


    }


    public static void main(String[] args) {
        new LottoFrame2();
    }


}