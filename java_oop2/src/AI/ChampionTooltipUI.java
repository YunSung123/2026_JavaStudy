package AI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChampionTooltipUI extends JFrame {

    public ChampionTooltipUI() {
        setTitle("포샘디펜스 배치툴 - 챔피언 목록");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(20, 20, 24));
        setContentPane(root);

        JLabel title = new JLabel("챔피언 목록");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(0, 8, 10, 10));
        listPanel.setBackground(new Color(20, 20, 24));
        listPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        List<ChampionData> champions = createDummyChampions();

        for (ChampionData data : champions) {
            listPanel.add(new ChampionCard(data));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(20, 20, 24));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private List<ChampionData> createDummyChampions() {
        List<ChampionData> list = new ArrayList<>();

        list.add(new ChampionData("림블", "전사", "화염", "근거리 딜러", "human.png"));
        list.add(new ChampionData("루루", "마법사", "자연", "보조 / 버프", "human.png"));
        list.add(new ChampionData("브라이어", "광전사", "어둠", "돌진형 딜러", "human.png"));
        list.add(new ChampionData("블리츠", "기계", "강철", "탱커 / CC", "human.png"));
        list.add(new ChampionData("비에고", "망령", "어둠", "흡혈형 딜러", "human.png"));
        list.add(new ChampionData("소나", "음율사", "빛", "힐 / 지원", "human.png"));
        list.add(new ChampionData("쉔", "닌자", "대지", "방어형 탱커", "human.png"));
        list.add(new ChampionData("애니비아", "빙결", "물", "광역 마법 딜러", "human.png"));
        list.add(new ChampionData("가렌", "기사", "빛", "브루저", "human.png"));
        list.add(new ChampionData("카이사", "공허", "어둠", "원거리 딜러", "human.png"));
        list.add(new ChampionData("아리", "여우령", "바람", "마법 딜러", "human.png"));
        list.add(new ChampionData("세트", "투사", "대지", "근거리 브루저", "human.png"));

        return list;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChampionTooltipUI::new);
    }
}

// 챔피언 데이터
class ChampionData {
    String name;
    String synergy1;
    String synergy2;
    String desc;
    String imagePath;

    public ChampionData(String name, String synergy1, String synergy2, String desc, String imagePath) {
        this.name = name;
        this.synergy1 = synergy1;
        this.synergy2 = synergy2;
        this.desc = desc;
        this.imagePath = imagePath;
    }
}

// 챔피언 카드 UI
class ChampionCard extends JPanel {

    private final ChampionData data;

    public ChampionCard(ChampionData data) {
        this.data = data;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(95, 110));
        setBackground(new Color(34, 36, 44));
        setBorder(new LineBorder(new Color(90, 100, 120), 1, true));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(95, 75));

        ImageIcon icon = new ImageIcon(data.imagePath);
        Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));

        JLabel nameLabel = new JLabel(data.name, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));

        add(imageLabel, BorderLayout.CENTER);
        add(nameLabel, BorderLayout.SOUTH);

        // 툴팁
        setToolTipText(makeTooltipText());

        // 마우스 올렸을 때 테두리 강조
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBorder(new LineBorder(new Color(255, 170, 80), 2, true));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBorder(new LineBorder(new Color(90, 100, 120), 1, true));
            }
        });
    }

    private String makeTooltipText() {
        return "<html>"
                + "<div style='padding:6px;'>"
                + "<b style='font-size:13px;'>" + data.name + "</b><br><br>"
                + "계열: " + data.synergy1 + "<br>"
                + "직업: " + data.synergy2 + "<br>"
                + "설명: " + data.desc
                + "</div>"
                + "</html>";
    }
}
