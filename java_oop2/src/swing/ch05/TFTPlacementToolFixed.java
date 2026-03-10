package swing.ch05;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TFTPlacementToolFixed extends JFrame {

    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 7;
    private static final int BENCH_SIZE = 8;

    private final CellPanel[][] boardCells = new CellPanel[BOARD_ROWS][BOARD_COLS];
    private final CellPanel[] benchCells = new CellPanel[BENCH_SIZE];

    private CellPanel selectedCell = null;
    private JLabel infoLabel;

    public TFTPlacementToolFixed() {
        setTitle("배치 툴");
        setSize(1250, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        resetUnits();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(235, 235, 235));

        JLabel titleLabel = new JLabel("포샘디펜스 TFT형 배치툴", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        infoLabel = new JLabel("클릭 선택 후 클릭 배치, 또는 드래그로 이동 가능", SwingConstants.CENTER);
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(new Color(18, 19, 26));
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS, 8, 8));
        boardPanel.setBackground(new Color(18, 19, 26));

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                CellPanel cell = new CellPanel(CellType.BOARD, r, c);
                boardCells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(18, 19, 26));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JButton resetBtn = new JButton("초기화");
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resetBtn.setMaximumSize(new Dimension(90, 32));
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBtn.addActionListener(e -> {
            clearSelection();
            resetUnits();
            infoLabel.setText("초기화 완료");
        });

        rightPanel.add(resetBtn);
        rightPanel.add(Box.createVerticalGlue());

        centerWrapper.add(boardPanel, BorderLayout.CENTER);
        centerWrapper.add(rightPanel, BorderLayout.EAST);

        add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(15, 16, 24));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel benchTitle = new JLabel("대기석");
        benchTitle.setForeground(Color.WHITE);
        benchTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        benchTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel benchGrid = new JPanel(new GridLayout(1, BENCH_SIZE, 8, 8));
        benchGrid.setBackground(new Color(15, 16, 24));

        for (int i = 0; i < BENCH_SIZE; i++) {
            CellPanel cell = new CellPanel(CellType.BENCH, 0, i);
            benchCells[i] = cell;
            benchGrid.add(cell);
        }

        bottomPanel.add(benchTitle, BorderLayout.NORTH);
        bottomPanel.add(benchGrid, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void resetUnits() {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                boardCells[r][c].setUnit(null);
            }
        }

        String[][] units = {
                {"ㅁ", "1"},
                {"ㅠ", "2"},
                {"ㅠ", "3"},
                {"ㅁ", "4"},
                {"ㅁ", "5"},
                {"ㅁ", "6"},
                {"ㅁ", "7"},
                {"ㅁ", "8"}
        };

        for (int i = 0; i < BENCH_SIZE; i++) {
            benchCells[i].setUnit(new UnitData(units[i][0], units[i][1]));
        }
    }

    private void onCellClicked(CellPanel clickedCell) {
        // 아무것도 선택 안 된 상태
        if (selectedCell == null) {
            if (clickedCell.getUnit() != null) {
                selectedCell = clickedCell;
                clickedCell.setSelected(true);
                infoLabel.setText(clickedCell.getUnit().name + " 선택됨");
            }
            return;
        }

        // 같은 칸 다시 누르면 선택 해제
        if (selectedCell == clickedCell) {
            clearSelection();
            infoLabel.setText("선택 해제");
            return;
        }

        // 선택된 유닛을 다른 칸으로 이동/교체
        swapUnits(selectedCell, clickedCell);
        clearSelection();
        infoLabel.setText("이동 완료");
    }

    private void swapUnits(CellPanel a, CellPanel b) {
        UnitData temp = a.getUnit();
        a.setUnit(b.getUnit());
        b.setUnit(temp);
    }

    private void clearSelection() {
        if (selectedCell != null) {
            selectedCell.setSelected(false);
            selectedCell = null;
        }
    }

    enum CellType {
        BOARD, BENCH
    }

    static class UnitData {
        String name;
        String role;

        public UnitData(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String serialize() {
            return name + "|" + role;
        }

        public static UnitData deserialize(String s) {
            String[] parts = s.split("\\|");
            return new UnitData(parts[0], parts[1]);
        }
    }

    class CellPanel extends JPanel {
        private UnitData unit;
        private final CellType type;
        private final int row;
        private final int col;

        private final JLabel textLabel;
        private boolean selected = false;
        private Point dragStartPoint;

        public CellPanel(CellType type, int row, int col) {
            this.type = type;
            this.row = row;
            this.col = col;

            setLayout(new BorderLayout());
            setOpaque(true);
            setPreferredSize(new Dimension(120, 100));
            setBackground(type == CellType.BOARD ? new Color(77, 77, 92) : new Color(65, 65, 74));
            setBorder(new LineBorder(new Color(170, 170, 190), 2));

            textLabel = new JLabel("빈칸", SwingConstants.CENTER);
            textLabel.setForeground(Color.WHITE);
            textLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            add(textLabel, BorderLayout.CENTER);

            setTransferHandler(new CellTransferHandler());

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragStartPoint = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    dragStartPoint = null;
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    onCellClicked(CellPanel.this);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (unit == null) return;
                    if (dragStartPoint == null) return;

                    int dx = Math.abs(e.getX() - dragStartPoint.x);
                    int dy = Math.abs(e.getY() - dragStartPoint.y);

                    if (dx > 5 || dy > 5) {
                        getTransferHandler().exportAsDrag(CellPanel.this, e, TransferHandler.MOVE);
                        dragStartPoint = null;
                    }
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        public UnitData getUnit() {
            return unit;
        }

        public void setUnit(UnitData unit) {
            this.unit = unit;
            refreshView();
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            refreshBorder();
        }

        private void refreshView() {
            if (unit == null) {
                textLabel.setText("빈칸");
                setBackground(type == CellType.BOARD
                        ? new Color(77, 77, 92)
                        : new Color(65, 65, 74));
            } else {
                textLabel.setText("<html><center>" + unit.name + "<br>[" + unit.role + "]</center></html>");
                setBackground(getRoleColor(unit.role));
            }
            refreshBorder();
            repaint();
        }

        private void refreshBorder() {
            if (selected) {
                setBorder(new LineBorder(Color.YELLOW, 4));
            } else {
                setBorder(new LineBorder(new Color(170, 170, 190), 2));
            }
        }

        private Color getRoleColor(String role) {
            return switch (role) {
                case "딜러" -> new Color(196, 74, 74);
                case "골드" -> new Color(190, 149, 52);
                case "힐러" -> new Color(74, 168, 126);
                case "보조" -> new Color(202, 151, 73);
                case "마법" -> new Color(139, 88, 190);
                case "탱커" -> new Color(84, 113, 187);
                case "원딜" -> new Color(166, 95, 95);
                case "전사" -> new Color(140, 140, 140);
                default -> new Color(110, 110, 110);
            };
        }
    }

    class CellTransferHandler extends TransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            CellPanel source = (CellPanel) c;
            if (source.getUnit() == null) return null;
            return new StringSelection(source.getUnit().serialize());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;

            try {
                // CellPanel target = (CellPanel) support.getComponent();
                // CellPanel source = (CellPanel) support.getComponent().getClientProperty("dragSource");

                JComponent comp = (JComponent) support.getComponent();
                CellPanel target = (CellPanel) comp;
                CellPanel source = (CellPanel) comp.getClientProperty("dragSource");

                if (source == null || source == target || source.getUnit() == null) {
                    return false;
                }

                swapUnits(source, target);
                clearSelection();
                infoLabel.setText("드래그 이동 완료");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
            comp.putClientProperty("dragSource", comp);
            super.exportAsDrag(comp, e, action);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            source.putClientProperty("dragSource", null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TFTPlacementToolFixed::new);
    }
}