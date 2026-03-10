package AI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class FosemPlacementTool extends JFrame {

    // 4 x 8, 가로 4
    private static final int BOARD_COLS = 4;
    private static final int BOARD_ROWS = 8;

    private static final int BENCH_COLS = 8;
    private static final int BENCH_ROWS = 2;
    private static final int BENCH_SIZE = BENCH_COLS * BENCH_ROWS;

    private final CellPanel[][] boardCells = new CellPanel[BOARD_ROWS][BOARD_COLS];
    private final CellPanel[] benchCells = new CellPanel[BENCH_SIZE];

    private final JLabel infoLabel = new JLabel("유닛을 드래그해서 배치하세요.", SwingConstants.CENTER);
    private final JTextArea synergyArea = new JTextArea();

    private CellPanel selectedCell;

    // 드래그 상태
    private CellPanel dragSource;
    private Point pressScreenPoint;
    private boolean dragging;
    private JPanel glass;
    private JLabel dragGhost;

    // 글에서 보이는 시너지 이름들
    private static final List<String> SYNERGY_ORDER = List.of(
            "거인", "검사", "격투", "나룻잎", "멸룡", "모래", "무녀", "물", "바람", "보호",
            "사신", "서번트", "소환사", "아카츠키", "악마", "암살자", "에스파다", "연금술",
            "요괴", "저격", "정령", "주술", "창술사", "천사", "초능력", "화염", "흡혈", "병사장"
    );

    public FosemPlacementTool() {
        setTitle("포샘디펜스 배치툴");
        setSize(1360, 920);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadSampleUnits();
        installGlobalDragListener();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(235, 235, 235));

        JLabel titleLabel = new JLabel("포샘디펜스 4x8 배치툴", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));

        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(18, 20, 28));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS, 8, 8));
        boardPanel.setBackground(new Color(18, 20, 28));

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                CellPanel cell = new CellPanel(CellType.BOARD, r, c);
                boardCells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        centerPanel.add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBackground(new Color(15, 16, 24));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(70, 74, 92), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel synergyTitle = new JLabel("현재 시너지");
        synergyTitle.setForeground(Color.WHITE);
        synergyTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        synergyArea.setEditable(false);
        synergyArea.setFocusable(false);
        synergyArea.setLineWrap(true);
        synergyArea.setWrapStyleWord(true);
        synergyArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        synergyArea.setBackground(new Color(15, 16, 24));
        synergyArea.setForeground(new Color(220, 220, 220));
        synergyArea.setText("배치된 유닛의 시너지가 여기에 표시됩니다.");

        JButton resetBtn = new JButton("초기화");
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resetBtn.addActionListener(e -> {
            clearSelection();
            clearBoardOnly();
            loadSampleUnits();
            refreshSynergyPanel();
            infoLabel.setText("초기화 완료");
        });

        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setBackground(new Color(15, 16, 24));
        rightTop.add(synergyTitle, BorderLayout.WEST);
        rightTop.add(resetBtn, BorderLayout.EAST);

        rightPanel.add(rightTop, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(synergyArea), BorderLayout.CENTER);

        centerPanel.add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        JPanel benchWrapper = new JPanel(new BorderLayout());
        benchWrapper.setBackground(new Color(12, 13, 20));
        benchWrapper.setBorder(BorderFactory.createEmptyBorder(12, 18, 18, 18));

        JLabel benchTitle = new JLabel("대기석");
        benchTitle.setForeground(Color.WHITE);
        benchTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        benchTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel benchPanel = new JPanel(new GridLayout(BENCH_ROWS, BENCH_COLS, 8, 8));
        benchPanel.setBackground(new Color(12, 13, 20));

        for (int i = 0; i < BENCH_SIZE; i++) {
            CellPanel cell = new CellPanel(CellType.BENCH, i / BENCH_COLS, i % BENCH_COLS);
            benchCells[i] = cell;
            benchPanel.add(cell);
        }

        benchWrapper.add(benchTitle, BorderLayout.NORTH);
        benchWrapper.add(benchPanel, BorderLayout.CENTER);

        add(benchWrapper, BorderLayout.SOUTH);

        // glass pane
        glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);

        dragGhost = new JLabel("", SwingConstants.CENTER);
        dragGhost.setOpaque(true);
        dragGhost.setVisible(false);
        dragGhost.setBorder(new LineBorder(Color.YELLOW, 2));
        dragGhost.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        glass.add(dragGhost);

        refreshSynergyPanel();
    }

    private void loadSampleUnits() {
        for (CellPanel cell : benchCells) {
            cell.setUnit(null);
        }

        // 대표 샘플. 여기 계속 추가하면 됨.
        List<Unit> sample = List.of(
                new Unit("노말", "슬로스", "거인", "격투"),
                new Unit("스페셜", "헤라클레스", "거인", "검사", "서번트"),
                new Unit("레전드", "마인부우", "거인", "연금술", "악마"),
                new Unit("노말", "란슬롯", "검사", "서번트"),
                new Unit("레어", "아토가미 토카", "검사", "정령"),
                new Unit("레전드", "쿠훌린", "서번트", "창술사"),
                new Unit("레어", "네지", "격투", "나룻잎", "바람"),
                new Unit("레전드", "나츠 드래그닐", "격투", "멸룡", "화염"),
                new Unit("노말", "사루토비", "나룻잎", "소환사"),
                new Unit("에픽", "사스케", "나룻잎", "암살자"),
                new Unit("레전드", "토비라마", "물", "나룻잎"),
                new Unit("스페셜", "웬디 마벨", "멸룡", "바람"),
                new Unit("레전드", "유클리프", "멸룡", "병사장"),
                new Unit("에픽", "테마리", "모래", "바람"),
                new Unit("신화", "치요", "모래", "소환사"),
                new Unit("스페셜", "엔마 아이", "무녀", "요괴"),
                new Unit("레전드", "키요우", "무녀", "초능력"),
                new Unit("노말", "요시노", "물", "정령"),
                new Unit("레어", "아쿠아", "물", "천사"),
                new Unit("신화", "리무르", "물", "악마"),
                new Unit("레어", "미츠카이", "보호", "천사"),
                new Unit("에픽", "레스티아", "보호", "정령"),
                new Unit("레전드", "프랑켄슈타인", "보호", "서번트"),
                new Unit("레전드", "토시로", "사신", "물"),
                new Unit("신화", "켄파치", "사신", "검사"),
                new Unit("스페셜", "잭 더 리퍼", "서번트", "암살자"),
                new Unit("레어", "모모", "소환사", "악마"),
                new Unit("레어", "카쿠즈", "아카츠키", "소환사"),
                new Unit("신화", "토비", "아카츠키", "암살자"),
                new Unit("에픽", "아케노", "악마", "무녀"),
                new Unit("스페셜", "시호인", "암살자", "사신", "격투"),
                new Unit("레전드", "코요테 스타크", "암살자", "에스파다"),
                new Unit("레어", "알폰스", "연금술", "거인"),
                new Unit("신화", "길가메쉬", "연금술", "서번트"),
                new Unit("신화", "셋쇼마루", "요괴", "검사"),
                new Unit("노말", "세라스 빅토리아", "저격", "흡혈"),
                new Unit("노말", "요시노", "정령", "물"),
                new Unit("노말", "메데이아", "주술", "서번트"),
                new Unit("레어", "히단", "창술사", "아카츠키"),
                new Unit("노말", "님프", "천사", "바람"),
                new Unit("신화", "타츠마키", "초능력", "바람"),
                new Unit("스페셜", "샤나", "화염", "천사"),
                new Unit("레어", "알케이드", "흡혈", "격투")
        );

        for (int i = 0; i < Math.min(sample.size(), BENCH_SIZE); i++) {
            benchCells[i].setUnit(sample.get(i));
        }
    }

    private void clearBoardOnly() {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                boardCells[r][c].setUnit(null);
            }
        }
    }

    private void refreshSynergyPanel() {
        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (String synergy : SYNERGY_ORDER) {
            countMap.put(synergy, 0);
        }

        List<Unit> boardUnits = new ArrayList<>();
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                Unit unit = boardCells[r][c].getUnit();
                if (unit != null) {
                    boardUnits.add(unit);
                    for (String trait : unit.traits) {
                        countMap.put(trait, countMap.getOrDefault(trait, 0) + 1);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("배치 유닛 수: ").append(boardUnits.size()).append("\n\n");

        List<Map.Entry<String, Integer>> active = countMap.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getValue(), a.getValue());
                    if (cmp != 0) return cmp;
                    return a.getKey().compareTo(b.getKey());
                })
                .collect(Collectors.toList());

        if (active.isEmpty()) {
            sb.append("아직 배치된 시너지가 없음");
        } else {
            sb.append("[활성 중인 시너지 개수 집계]\n");
            for (Map.Entry<String, Integer> e : active) {
                sb.append("• ").append(e.getKey()).append(" : ").append(e.getValue()).append("\n");
            }
        }

        sb.append("\n-------------------------\n");
        sb.append("글 기준 시너지 목록\n");
        for (String synergy : SYNERGY_ORDER) {
            sb.append("- ").append(synergy).append("\n");
        }

        synergyArea.setText(sb.toString());
        synergyArea.setCaretPosition(0);
    }

    private void installGlobalDragListener() {
        AWTEventListener globalListener = event -> {
            if (!(event instanceof MouseEvent e)) return;

            if (pressScreenPoint == null && dragSource == null) return;

            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                handleGlobalDragged(e);
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                handleGlobalReleased(e);
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(
                globalListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    private void handleGlobalDragged(MouseEvent e) {
        if (dragSource == null) return;
        if (dragSource.getUnit() == null) return;

        if (!dragging) {
            int dx = Math.abs(e.getXOnScreen() - pressScreenPoint.x);
            int dy = Math.abs(e.getYOnScreen() - pressScreenPoint.y);
            if (dx < 5 && dy < 5) return;
            startDragGhost(e.getXOnScreen(), e.getYOnScreen());
            dragging = true;
        } else {
            moveDragGhost(e.getXOnScreen(), e.getYOnScreen());
        }
    }

    private void handleGlobalReleased(MouseEvent e) {
        if (dragSource == null) {
            pressScreenPoint = null;
            return;
        }

        if (dragging) {
            CellPanel target = findCellAtScreen(e.getXOnScreen(), e.getYOnScreen());
            if (target != null && target != dragSource) {
                swapUnits(dragSource, target);
                infoLabel.setText("드래그 이동 완료");
            } else {
                infoLabel.setText("이동 취소");
            }
            stopDragGhost();
            clearSelection();
        }

        dragging = false;
        dragSource = null;
        pressScreenPoint = null;
    }

    private void startDragGhost(int screenX, int screenY) {
        Unit unit = dragSource.getUnit();
        if (unit == null) return;

        dragGhost.setText("<html><center>" + unit.name + "<br>[" + String.join("/", unit.traits) + "]</center></html>");
        dragGhost.setBackground(new Color(35, 35, 45, 220));
        dragGhost.setForeground(Color.WHITE);
        dragGhost.setSize(120, 64);

        glass.setVisible(true);
        dragGhost.setVisible(true);
        moveDragGhost(screenX, screenY);
    }

    private void moveDragGhost(int screenX, int screenY) {
        Point p = new Point(screenX, screenY);
        SwingUtilities.convertPointFromScreen(p, glass);
        dragGhost.setLocation(p.x - dragGhost.getWidth() / 2, p.y - dragGhost.getHeight() / 2);
        glass.repaint();
    }

    private void stopDragGhost() {
        dragGhost.setVisible(false);
        glass.setVisible(false);
    }

    private CellPanel findCellAtScreen(int screenX, int screenY) {
        Point p = new Point(screenX, screenY);
        SwingUtilities.convertPointFromScreen(p, getContentPane());
        Component comp = SwingUtilities.getDeepestComponentAt(getContentPane(), p.x, p.y);

        while (comp != null) {
            if (comp instanceof CellPanel) {
                return (CellPanel) comp;
            }
            comp = comp.getParent();
        }
        return null;
    }

    private void swapUnits(CellPanel a, CellPanel b) {
        Unit temp = a.getUnit();
        a.setUnit(b.getUnit());
        b.setUnit(temp);
        refreshSynergyPanel();
    }

    private void clearSelection() {
        if (selectedCell != null) {
            selectedCell.setSelected(false);
            selectedCell = null;
        }
    }

    private void handleCellClick(CellPanel clicked) {
        if (dragging) return;

        if (selectedCell == null) {
            if (clicked.getUnit() != null) {
                selectedCell = clicked;
                clicked.setSelected(true);
                infoLabel.setText(clicked.getUnit().name + " 선택됨");
            }
            return;
        }

        if (selectedCell == clicked) {
            clearSelection();
            infoLabel.setText("선택 해제");
            return;
        }

        swapUnits(selectedCell, clicked);
        clearSelection();
        infoLabel.setText("클릭 이동 완료");
    }

    enum CellType {
        BOARD, BENCH
    }

    static class Unit {
        String tier;
        String name;
        List<String> traits;

        public Unit(String tier, String name, String... traits) {
            this.tier = tier;
            this.name = name;
            this.traits = Arrays.asList(traits);
        }
    }

    class CellPanel extends JPanel {
        private final CellType type;
        private final int row;
        private final int col;
        private final JLabel label = new JLabel("", SwingConstants.CENTER);

        private Unit unit;
        private boolean selected;

        public CellPanel(CellType type, int row, int col) {
            this.type = type;
            this.row = row;
            this.col = col;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(120, 82));
            setOpaque(true);
            setBackground(type == CellType.BOARD ? new Color(72, 74, 90) : new Color(62, 64, 76));
            setBorder(new LineBorder(new Color(160, 165, 185), 2));

            label.setForeground(Color.WHITE);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            add(label, BorderLayout.CENTER);

            refreshView();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (unit != null) {
                        dragSource = CellPanel.this;
                        pressScreenPoint = e.getLocationOnScreen();
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleCellClick(CellPanel.this);
                }
            };

            addMouseListener(mouseAdapter);
            label.addMouseListener(mouseAdapter);
        }

        public Unit getUnit() {
            return unit;
        }

        public void setUnit(Unit unit) {
            this.unit = unit;
            refreshView();
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            refreshBorder();
        }

        private void refreshView() {
            if (unit == null) {
                label.setText("빈칸");
                setBackground(type == CellType.BOARD ? new Color(72, 74, 90) : new Color(62, 64, 76));
            } else {
                String traitText = String.join("/", unit.traits);
                label.setText("<html><center>" + unit.name + "<br>[" + traitText + "]</center></html>");
                setBackground(colorByTier(unit.tier));
            }
            refreshBorder();
            repaint();
        }

        private void refreshBorder() {
            if (selected) {
                setBorder(new LineBorder(Color.YELLOW, 4));
            } else {
                setBorder(new LineBorder(new Color(160, 165, 185), 2));
            }
        }

        private Color colorByTier(String tier) {
            return switch (tier) {
                case "노말" -> new Color(120, 120, 120);
                case "레어" -> new Color(66, 110, 180);
                case "스페셜" -> new Color(148, 86, 190);
                case "에픽" -> new Color(195, 128, 55);
                case "레전드" -> new Color(186, 72, 72);
                case "신화" -> new Color(220, 180, 70);
                case "울티" -> new Color(80, 170, 150);
                default -> new Color(100, 100, 100);
            };
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FosemPlacementTool::new);
    }
}