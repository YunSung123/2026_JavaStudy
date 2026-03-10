package swing.ch05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorFrame extends JFrame implements ActionListener {
    // 이전 코드 한번 더 복습
    private JPanel panel1; // 판넬

    private  JButton button1; // 버튼 생성하기
    private  JButton button2; // 버튼 생성하기

    // 생성자 : 밑 함수 실행
    public ColorFrame() {
        initData(); // 데이터 초기화
        setInitLayout(); // 화면 배치
        addEventListener(); // 이벤트 등록

    }

    // 객체 생성
    private void initData(){
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 이게 있어야 창닫으면 꺼짐

        panel1 = new JPanel();
        button1 = new JButton("button1");
        button2 = new JButton("button2");
    }

    // 화면 구성
    private void setInitLayout(){
        setLayout(new BorderLayout()); // 컴포넌트를 위/아래/좌/우/중앙으로 배치하는 방식 사용
        panel1.setBackground(Color.YELLOW);
        add(panel1); // 판넬 붙이기, 노랑색

        // 반넬에 버튼 붙히기
        panel1.add(button1);
        panel1.add(button2);
        setVisible(true); // 실제 화면에 적용
    }

    // 이벤트 연결 - 버튼에 이벤트 넣기
    private void addEventListener(){

        // 버튼1, 2에. 엑션을 넣는다.
        button1.addActionListener(this);
        button2.addActionListener(this);
    }

    // 버튼 액션 내용
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("안녕하세요");

        if(e.getSource() == button1){
            System.out.println("button1에 이벤트가 발생했습니다");
            panel1.setBackground(Color.BLUE);
        }else if (e.getSource() == button2){
            System.out.println("button2에 이벤트가 발생했습니다");
            panel1.setBackground(Color.black);
        }
        JButton selectedButton = (JButton) e.getSource();
        System.out.println(selectedButton.getText());
        // ------------ 1번 > 파란색 , 2번 검은색 동작할 수 있도록 코드를 완성해 주세요
        panel1.setBackground(Color.BLUE);
    }

    //actionPer
}
