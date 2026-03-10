package AI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class FosemPlacementTool extends JFrame {

    // ===== 보드 설정 =====
    private static final int BOARD_COLS = 4; // 가로 4
    private static final int BOARD_ROWS = 8; // 세로 8

    private final CellPanel[][] boardCells = new CellPanel[BOARD_ROWS][BOARD_COLS];

    // ===== 챔피언 목록 =====
    private final List<Unit> allUnits = new ArrayList<>();
    private final JPanel championListPanel = new JPanel(new GridLayout(0, 8, 10, 10));

    // ===== 우측 정보 =====
    private final JTextArea synergyArea = new JTextArea();
    private final JLabel infoLabel = new JLabel("캐릭터를 클릭하거나 드래그해서 배치하세요.", SwingConstants.CENTER);

    // ===== 선택/드래그 =====
    private CellPanel selectedBoardCell;
    private Unit selectedListUnit;

    private CellPanel dragSourceCell;
    private Point pressScreenPoint;
    private boolean dragging;
    private JPanel glass;
    private JLabel dragGhost;

    // ===== 시너지 순서 =====
    private static final List<String> SYNERGY_ORDER = List.of(
            "거인", "검사", "격투", "나룻잎", "멸룡", "모래", "무녀", "물", "바람", "보호",
            "사신", "서번트", "소환사", "아카츠키", "악마", "암살자", "에스파다", "연금술",
            "요괴", "저격", "정령", "주술", "창술사", "천사", "초능력", "화염", "흡혈", "병사장"
    );

    public FosemPlacementTool() {
        setTitle("포샘디펜스 배치 툴");
        setSize(1450, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadUnitsFromText("units.txt"); // 여기 텍스트 파일 불러옴
        refreshChampionList();
        installGlobalDragListener();
        refreshSynergyPanel();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== 상단 =====
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

        // ===== 중앙 =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(18, 20, 28));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ===== 배치판 =====
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS, 8, 8));
        boardPanel.setBackground(new Color(18, 20, 28));

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                CellPanel cell = new CellPanel(r, c);
                boardCells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        centerPanel.add(boardPanel, BorderLayout.CENTER);

        // ===== 우측 시너지 =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(320, 0));
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

        JButton resetBtn = new JButton("초기화");
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resetBtn.addActionListener(e -> resetBoard());

        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setBackground(new Color(15, 16, 24));
        rightTop.add(synergyTitle, BorderLayout.WEST);
        rightTop.add(resetBtn, BorderLayout.EAST);

        rightPanel.add(rightTop, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(synergyArea), BorderLayout.CENTER);

        centerPanel.add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // ===== 하단 챔피언 목록 =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(12, 13, 20));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 18, 18));

        JLabel championTitle = new JLabel("캐릭터 목록");
        championTitle.setForeground(Color.WHITE);
        championTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        championTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        championListPanel.setBackground(new Color(20, 20, 24));
        championListPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(championListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(20, 20, 24));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        bottomPanel.add(championTitle, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        bottomPanel.setPreferredSize(new Dimension(0, 300));
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== glass pane =====
        glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);

        dragGhost = new JLabel("", SwingConstants.CENTER);
        dragGhost.setOpaque(true);
        dragGhost.setVisible(false);
        dragGhost.setBorder(new LineBorder(Color.YELLOW, 2));
        dragGhost.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        glass.add(dragGhost);
    }

    private void loadUnitsFromText(String fileName) {
        allUnits.clear();

        File file = new File(fileName);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "units.txt 파일이 없습니다.\n프로젝트 실행 폴더에 units.txt를 넣어주세요.",
                    "파일 없음", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String tier = parts[0].trim();
                String name = parts[1].trim();

                List<String> traits = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    String trait = parts[i].trim();
                    if (!trait.isEmpty()) {
                        traits.add(trait);
                    }
                }

                allUnits.add(new Unit(tier, name, traits, "human.png"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "units.txt 읽기 실패: " + e.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshChampionList() {
        championListPanel.removeAll();

        for (Unit unit : allUnits) {
            ChampionCard card = new ChampionCard(unit);
            championListPanel.add(card);
        }

        championListPanel.revalidate();
        championListPanel.repaint();
    }

    private void resetBoard() {
        clearSelection();

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                boardCells[r][c].setUnit(null);
            }
        }

        selectedListUnit = null;
        refreshSynergyPanel();
        infoLabel.setText("초기화 완료");
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
            sb.append("[활성 시너지]\n");
            for (Map.Entry<String, Integer> e : active) {
                sb.append("• ").append(e.getKey()).append(" : ").append(e.getValue()).append("\n");
            }
        }

        synergyArea.setText(sb.toString());
        synergyArea.setCaretPosition(0);
    }

    private void installGlobalDragListener() {
        AWTEventListener globalListener = event -> {
            if (!(event instanceof MouseEvent e)) return;

            if (pressScreenPoint == null && dragSourceCell == null) return;

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
        if (dragSourceCell == null) return;
        if (dragSourceCell.getUnit() == null) return;

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
        if (dragSourceCell == null) {
            pressScreenPoint = null;
            return;
        }

        if (dragging) {
            CellPanel target = findCellAtScreen(e.getXOnScreen(), e.getYOnScreen());
            if (target != null && target != dragSourceCell) {
                swapUnits(dragSourceCell, target);
                infoLabel.setText("드래그 이동 완료");
            } else {
                infoLabel.setText("이동 취소");
            }
            stopDragGhost();
            clearSelection();
        }

        dragging = false;
        dragSourceCell = null;
        pressScreenPoint = null;
    }

    private void startDragGhost(int screenX, int screenY) {
        Unit unit = dragSourceCell.getUnit();
        if (unit == null) return;

        dragGhost.setText("<html><center>" + unit.name + "<br>[" + String.join("/", unit.traits) + "]</center></html>");
        dragGhost.setBackground(new Color(35, 35, 45, 220));
        dragGhost.setForeground(Color.WHITE);
        dragGhost.setSize(130, 68);

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
        if (selectedBoardCell != null) {
            selectedBoardCell.setSelected(false);
            selectedBoardCell = null;
        }
        selectedListUnit = null;
    }

    private void placeUnitFromList(Unit unit, CellPanel targetCell) {
        if (targetCell == null) return;
        targetCell.setUnit(unit.copy());
        refreshSynergyPanel();
        infoLabel.setText(unit.name + " 배치 완료");
    }

    // ===================== 데이터 클래스 =====================
    static class Unit {
        String tier;
        String name;
        List<String> traits;
        String imagePath;

        public Unit(String tier, String name, List<String> traits, String imagePath) {
            this.tier = tier;
            this.name = name;
            this.traits = traits;
            this.imagePath = imagePath;
        }

        public Unit copy() {
            return new Unit(tier, name, new ArrayList<>(traits), imagePath);
        }
    }

    // ===================== 배치 칸 =====================
    class CellPanel extends JPanel {
        private final int row;
        private final int col;
        private final JLabel label = new JLabel("", SwingConstants.CENTER);

        private Unit unit;
        private boolean selected;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(110, 110)); // 정사각형
            setOpaque(true);
            setBackground(new Color(72, 74, 90));
            setBorder(new LineBorder(new Color(160, 165, 185), 2));

            label.setForeground(Color.WHITE);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            add(label, BorderLayout.CENTER);

            refreshView();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (unit != null) {
                        dragSourceCell = CellPanel.this;
                        pressScreenPoint = e.getLocationOnScreen();
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleBoardCellClick(CellPanel.this);
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
                setBackground(new Color(72, 74, 90));
                setToolTipText(null);
            } else {
                String traitText = String.join("/", unit.traits);
                label.setText("<html><center>" + unit.name + "<br>[" + traitText + "]</center></html>");
                setBackground(colorByTier(unit.tier));
                setToolTipText(makeTooltipText(unit));
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
    }

    private void handleBoardCellClick(CellPanel clicked) {
        if (dragging) return;

        if (selectedListUnit != null) {
            placeUnitFromList(selectedListUnit, clicked);
            selectedListUnit = null;
            return;
        }

        if (selectedBoardCell == null) {
            if (clicked.getUnit() != null) {
                selectedBoardCell = clicked;
                clicked.setSelected(true);
                infoLabel.setText(clicked.getUnit().name + " 선택됨");
            }
            return;
        }

        if (selectedBoardCell == clicked) {
            clearSelection();
            infoLabel.setText("선택 해제");
            return;
        }

        swapUnits(selectedBoardCell, clicked);
        clearSelection();
        infoLabel.setText("클릭 이동 완료");
    }

    // ===================== 챔피언 카드 =====================
    class ChampionCard extends JPanel {
        private final Unit unit;

        public ChampionCard(Unit unit) {
            this.unit = unit;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(95, 110));
            setBackground(new Color(34, 36, 44));
            setBorder(new LineBorder(new Color(90, 100, 120), 1, true));

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(95, 75));

            ImageIcon icon = loadChampionIcon(unit.imagePath);
            if (icon != null) {
                Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("IMG");
                imageLabel.setForeground(Color.LIGHT_GRAY);
            }

            JLabel nameLabel = new JLabel(unit.name, SwingConstants.CENTER);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));

            add(imageLabel, BorderLayout.CENTER);
            add(nameLabel, BorderLayout.SOUTH);

            setToolTipText(makeTooltipText(unit));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorder(new LineBorder(new Color(255, 170, 80), 2, true));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(new LineBorder(new Color(90, 100, 120), 1, true));
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    clearSelection();
                    selectedListUnit = unit;
                    infoLabel.setText(unit.name + " 선택됨 → 배치판 빈칸 클릭");
                }
            });
        }
    }

    private ImageIcon loadChampionIcon(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) return null;
        return new ImageIcon(imagePath);
    }

    private String makeTooltipText(Unit unit) {
        return "<html>"
                + "<div style='padding:6px;'>"
                + "<b style='font-size:13px;'>" + unit.name + "</b><br><br>"
                + "등급: " + unit.tier + "<br>"
                + "시너지: " + String.join(", ", unit.traits)
                + "</div>"
                + "</html>";
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FosemPlacementTool::new);
    }
}