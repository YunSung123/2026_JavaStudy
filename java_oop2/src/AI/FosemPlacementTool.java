package AI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FosemPlacementTool extends JFrame {

    private final List<Unit> allUnits = UnitData.createUnits();
    private final DefaultListModel<Unit> listModel = new DefaultListModel<>();
    private final JList<Unit> unitJList = new JList<>(listModel);

    private final JTextField searchField = new JTextField();
    private final JLabel selectedInfoLabel = new JLabel("선택된 캐릭터: 없음");
    private final JLabel synergyInfoLabel = new JLabel("시너지: 없음");

    private Unit selectedUnit = null;

    private final int ROWS = 8;
    private final int COLS = 4;
    private final CellButton[][] board = new CellButton[ROWS][COLS];

    public FosemPlacementTool() {
        setTitle("포샘디펜스 배치툴");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createLeftPanel(), BorderLayout.WEST);
        add(createBoardPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshUnitList("");

        setSize(1200, 850);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setPreferredSize(new Dimension(350, 800));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("캐릭터 목록");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshUnitList(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshUnitList(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshUnitList(searchField.getText());
            }
        });

        unitJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unitJList.setFixedCellHeight(70);
        unitJList.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        unitJList.setCellRenderer(new UnitListRenderer());

        unitJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedUnit = unitJList.getSelectedValue();
                updateSelectedInfo();
            }
        });

        JScrollPane scrollPane = new JScrollPane(unitJList);

        leftPanel.add(title, BorderLayout.NORTH);
        leftPanel.add(searchField, BorderLayout.CENTER);
        leftPanel.add(scrollPane, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(searchField, BorderLayout.CENTER);
        wrapper.add(scrollPane, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createBoardPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("4 x 8 배치판", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        outer.add(title, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 6, 6));
        gridPanel.setBackground(new Color(35, 35, 35));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                CellButton cell = new CellButton(r, c);
                cell.addActionListener(e -> onCellClicked(cell));
                board[r][c] = cell;
                gridPanel.add(cell);
            }
        }

        outer.add(gridPanel, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        selectedInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        synergyInfoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        panel.add(selectedInfoLabel);
        panel.add(synergyInfoLabel);

        return panel;
    }

    private void onCellClicked(CellButton cell) {
        if (selectedUnit != null) {
            cell.setUnit(selectedUnit);
        } else {
            cell.clearUnit();
        }
        updateSynergySummary();
    }

    private void updateSelectedInfo() {
        if (selectedUnit == null) {
            selectedInfoLabel.setText("선택된 캐릭터: 없음");
            synergyInfoLabel.setText("시너지: 없음");
        } else {
            selectedInfoLabel.setText("선택된 캐릭터: " + selectedUnit.getName() + " [" + selectedUnit.getTier() + "]");
            synergyInfoLabel.setText("시너지: " + selectedUnit.getSynergyText());
        }
    }

    private void refreshUnitList(String keyword) {
        listModel.clear();
        String lower = keyword.trim().toLowerCase();

        for (Unit unit : allUnits) {
            String target = (unit.getName() + " " + unit.getTier() + " " + unit.getSynergyText()).toLowerCase();
            if (lower.isEmpty() || target.contains(lower)) {
                listModel.addElement(unit);
            }
        }
    }

    private void updateSynergySummary() {
        List<String> allSynergies = new ArrayList<>();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Unit u = board[r][c].getUnit();
                if (u != null) {
                    allSynergies.addAll(u.getSynergies());
                }
            }
        }

        if (allSynergies.isEmpty()) {
            synergyInfoLabel.setText("배치 시너지 합계: 없음");
            return;
        }

        List<String> unique = new ArrayList<>();
        for (String s : allSynergies) {
            if (!unique.contains(s)) {
                unique.add(s);
            }
        }

        synergyInfoLabel.setText("배치 시너지: " + String.join(", ", unique));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FosemPlacementTool::new);
    }

    static class CellButton extends JButton {
        private Unit unit;
        private final int row;
        private final int col;

        public CellButton(int row, int col) {
            this.row = row;
            this.col = col;

            setFont(new Font("맑은 고딕", Font.BOLD, 14));
            setFocusPainted(false);
            setBackground(new Color(55, 55, 55));
            setForeground(Color.WHITE);
            setBorder(new LineBorder(Color.GRAY, 1));
            setPreferredSize(new Dimension(90, 90));
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.CENTER);
            updateView();
        }

        public void setUnit(Unit unit) {
            this.unit = unit;
            updateView();
        }

        public void clearUnit() {
            this.unit = null;
            updateView();
        }

        public Unit getUnit() {
            return unit;
        }

        private void updateView() {
            if (unit == null) {
                setText("<html><center>빈 칸<br>(" + row + "," + col + ")</center></html>");
                setToolTipText(null);
                setBackground(new Color(55, 55, 55));
            } else {
                setText("<html><center>" + unit.getName() + "</center></html>");
                setToolTipText(unit.getTier() + " / " + unit.getSynergyText());
                setBackground(getTierColor(unit.getTier()));
            }
        }

        private Color getTierColor(String tier) {
            switch (tier) {
                case "노말":
                    return new Color(90, 90, 90);
                case "레어":
                    return new Color(70, 110, 180);
                case "스페셜":
                    return new Color(125, 85, 190);
                case "에픽":
                    return new Color(180, 100, 60);
                case "레전드":
                    return new Color(200, 150, 50);
                case "신화":
                    return new Color(180, 60, 60);
                case "울티":
                    return new Color(40, 140, 120);
                default:
                    return new Color(80, 80, 80);
            }
        }
    }

    static class UnitListRenderer extends JPanel implements ListCellRenderer<Unit> {
        private final JLabel nameLabel = new JLabel();
        private final JLabel infoLabel = new JLabel();

        public UnitListRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

            add(nameLabel, BorderLayout.NORTH);
            add(infoLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Unit> list, Unit value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            nameLabel.setText(value.getName() + " [" + value.getTier() + "]");
            infoLabel.setText(value.getSynergyText());

            if (isSelected) {
                setBackground(new Color(70, 90, 130));
                nameLabel.setForeground(Color.WHITE);
                infoLabel.setForeground(Color.WHITE);
            } else {
                setBackground(new Color(245, 245, 245));
                nameLabel.setForeground(Color.BLACK);
                infoLabel.setForeground(Color.DARK_GRAY);
            }

            setOpaque(true);
            return this;
        }
    }
}
