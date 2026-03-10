package swing.ch05;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

public class TFTDragPlacementTool extends JFrame {

    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 7;
    private static final int BENCH_SIZE = 8;

    private final UnitData[][] boardData = new UnitData[BOARD_ROWS][BOARD_COLS];
    private final UnitLabel[][] boardCells = new UnitLabel[BOARD_ROWS][BOARD_COLS];
    private final UnitLabel[] benchCells = new UnitLabel[BENCH_SIZE];

    public TFTDragPlacementTool() {
        setTitle("포샘디펜스 드래그 배치툴");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("포샘디펜스 TFT형 드래그 배치툴", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS, 8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        centerPanel.setBackground(new Color(35, 35, 40));

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                UnitLabel cell = new UnitLabel(CellType.BOARD, r, c);
                cell.setTransferHandler(new UnitTransferHandler());
                cell.setDropTarget(null); // 기본 드롭 타겟 허용용
                boardCells[r][c] = cell;
                centerPanel.add(cell);
            }
        }

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomWrapper.setBackground(new Color(25, 25, 30));

        JLabel benchTitle = new JLabel("대기석");
        benchTitle.setForeground(Color.WHITE);
        benchTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        benchTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel benchPanel = new JPanel(new GridLayout(1, BENCH_SIZE, 8, 8));
        benchPanel.setBackground(new Color(25, 25, 30));

        // 초기 벤치 유닛 세팅
        String[][] units = {
                {"이름1", "역할1"},
                {"이름2", "역할2"},
                {"이름3", "역할3"},
                {"이름4", "역할4"},
                {"이름5", "역할5"},
                {"이름6", "역할6"},
                {"이름7", "역할7"},
                {"이름8", "역할8"}
        };

        for (int i = 0; i < BENCH_SIZE; i++) {
            UnitLabel cell = new UnitLabel(CellType.BENCH, 0, i);
            cell.setTransferHandler(new UnitTransferHandler());
            cell.setDropTarget(null);

            UnitData data = new UnitData(units[i][0], units[i][1]);
            cell.setUnit(data);

            benchCells[i] = cell;
            benchPanel.add(cell);
        }

        bottomWrapper.add(benchTitle, BorderLayout.NORTH);
        bottomWrapper.add(benchPanel, BorderLayout.CENTER);

        add(bottomWrapper, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(30, 30, 35));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JButton resetBtn = new JButton("초기화");
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        resetBtn.addActionListener(e -> resetBoard());

        rightPanel.add(resetBtn);

        add(rightPanel, BorderLayout.EAST);
    }

    private void resetBoard() {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                boardData[r][c] = null;
                boardCells[r][c].setUnit(null);
            }
        }

        String[][] units = {
                {"미소기", "딜러"},
                {"나즈린", "골드"},
                {"호시노", "힐러"},
                {"스바루", "보조"},
                {"오티누스", "마법"},
                {"마다라", "탱커"},
                {"요정포수", "원딜"},
                {"암흑기사", "전사"}
        };

        for (int i = 0; i < BENCH_SIZE; i++) {
            benchCells[i].setUnit(new UnitData(units[i][0], units[i][1]));
        }
    }

    enum CellType {
        BOARD, BENCH
    }

    static class UnitData implements Serializable {
        String name;
        String role;

        public UnitData(String name, String role) {
            this.name = name;
            this.role = role;
        }

        @Override
        public String toString() {
            return name + "|" + role;
        }

        public static UnitData fromString(String text) {
            String[] split = text.split("\\|");
            return new UnitData(split[0], split[1]);
        }
    }

    class UnitLabel extends JLabel {
        private UnitData unit;
        private final CellType cellType;
        private final int row;
        private final int col;

        public UnitLabel(CellType cellType, int row, int col) {
            this.cellType = cellType;
            this.row = row;
            this.col = col;

            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setFont(new Font("맑은 고딕", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(120, 100));
            setBorder(new LineBorder(new Color(150, 150, 170), 2));
            setBackground(new Color(70, 70, 80));
            setText("빈칸");

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    if (unit != null) {
                        JComponent comp = (JComponent) e.getSource();
                        TransferHandler handler = comp.getTransferHandler();
                        handler.exportAsDrag(comp, e, TransferHandler.MOVE);
                    }
                }
            });
        }

        public void setUnit(UnitData unit) {
            this.unit = unit;

            if (unit == null) {
                setText("빈칸");
                setBackground(cellType == CellType.BOARD
                        ? new Color(70, 70, 80)
                        : new Color(55, 55, 60));
            } else {
                setText("<html><center>" + unit.name + "<br>[" + unit.role + "]</center></html>");
                setBackground(getRoleColor(unit.role));
            }
        }

        public UnitData getUnit() {
            return unit;
        }

        public CellType getCellType() {
            return cellType;
        }

        public int getGridRow() {
            return row;
        }

        public int getGridCol() {
            return col;
        }

        private Color getRoleColor(String role) {
            return switch (role) {
                case "딜러" -> new Color(180, 70, 70);
                case "탱커" -> new Color(80, 110, 180);
                case "힐러" -> new Color(70, 160, 120);
                case "마법" -> new Color(140, 90, 180);
                case "보조" -> new Color(200, 150, 70);
                case "골드" -> new Color(180, 140, 50);
                case "원딜" -> new Color(160, 90, 90);
                case "전사" -> new Color(120, 120, 120);
                default -> new Color(100, 100, 100);
            };
        }
    }

    class UnitTransferHandler extends TransferHandler {
        private final DataFlavor flavor = DataFlavor.stringFlavor;
        private UnitLabel sourceCell;

        @Override
        protected Transferable createTransferable(JComponent c) {
            sourceCell = (UnitLabel) c;
            if (sourceCell.getUnit() == null) return null;
            return new StringSelection(sourceCell.getUnit().toString());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDrop()) return false;
            return support.isDataFlavorSupported(flavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;

            try {
                UnitLabel targetCell = (UnitLabel) support.getComponent();
                String data = (String) support.getTransferable().getTransferData(flavor);
                UnitData draggedUnit = UnitData.fromString(data);

                if (sourceCell == null || sourceCell == targetCell) return false;

                UnitData sourceUnit = sourceCell.getUnit();
                UnitData targetUnit = targetCell.getUnit();

                if (sourceUnit == null) return false;

                // 서로 교환
                targetCell.setUnit(sourceUnit);
                sourceCell.setUnit(targetUnit);

                // boardData 동기화
                syncBoardData(sourceCell);
                syncBoardData(targetCell);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void syncBoardData(UnitLabel cell) {
            if (cell.getCellType() == CellType.BOARD) {
                boardData[cell.getGridRow()][cell.getGridCol()] = cell.getUnit();
            }
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            sourceCell = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TFTDragPlacementTool::new);
    }
}
