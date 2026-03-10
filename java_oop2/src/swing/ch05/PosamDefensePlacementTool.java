package swing.ch05;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PosamDefensePlacementTool extends JFrame {

    // 보드 크기 (TFT 느낌)
    private static final int BOARD_ROWS = 4;
    private static final int BOARD_COLS = 7;

    // 벤치 크기
    private static final int BENCH_SIZE = 8;

    private final Unit[][] boardUnits = new Unit[BOARD_ROWS][BOARD_COLS];
    private final JButton[][] boardButtons = new JButton[BOARD_ROWS][BOARD_COLS];

    private final List<Unit> benchUnits = new ArrayList<>();
    private final JButton[] benchButtons = new JButton[BENCH_SIZE];

    private Unit selectedUnit = null;
    private int selectedBenchIndex = -1;

    private JLabel infoLabel;

    public PosamDefensePlacementTool() {
        setTitle("포샘디펜스 배치툴 예시");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUnits();
        initUI();

        setVisible(true);
    }

    private void initUnits() {
        benchUnits.add(new Unit("미소기", "딜러"));
        benchUnits.add(new Unit("나즈린", "골드"));
        benchUnits.add(new Unit("호시노", "힐러"));
        benchUnits.add(new Unit("스바루", "보조"));
        benchUnits.add(new Unit("오티누스", "마법"));
        benchUnits.add(new Unit("마다라", "탱커"));
        benchUnits.add(new Unit("요정포수", "원딜"));
        benchUnits.add(new Unit("암흑기사", "전사"));
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 상단 정보 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 30));

        JLabel titleLabel = new JLabel("포샘디펜스 TFT형 배치툴", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        infoLabel = new JLabel("아래 벤치 유닛을 선택한 뒤, 보드 칸을 클릭해 배치하세요.", SwingConstants.CENTER);
        infoLabel.setForeground(new Color(220, 220, 220));
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 보드
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS, 8, 8));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        boardPanel.setBackground(new Color(40, 40, 45));

        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                JButton cell = new JButton();
                cell.setFocusPainted(false);
                cell.setFont(new Font("맑은 고딕", Font.BOLD, 13));
                cell.setBackground(new Color(70, 70, 80));
                cell.setForeground(Color.WHITE);
                cell.setBorder(new LineBorder(new Color(140, 140, 160), 2));
                final int r = row;
                final int c = col;

                cell.addActionListener(e -> handleBoardClick(r, c));
                boardButtons[row][col] = cell;
                boardPanel.add(cell);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // 하단 벤치
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(new Color(30, 30, 35));
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel benchTitle = new JLabel("대기석");
        benchTitle.setForeground(Color.WHITE);
        benchTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        benchTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel benchPanel = new JPanel(new GridLayout(1, BENCH_SIZE, 8, 8));
        benchPanel.setBackground(new Color(30, 30, 35));

        for (int i = 0; i < BENCH_SIZE; i++) {
            JButton benchBtn = new JButton();
            benchBtn.setFocusPainted(false);
            benchBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            benchBtn.setBackground(new Color(85, 85, 95));
            benchBtn.setForeground(Color.WHITE);
            benchBtn.setBorder(new LineBorder(new Color(150, 150, 170), 2));

            final int index = i;
            benchBtn.addActionListener(e -> handleBenchClick(index));

            benchButtons[i] = benchBtn;
            benchPanel.add(benchBtn);
        }

        bottomWrapper.add(benchTitle, BorderLayout.NORTH);
        bottomWrapper.add(benchPanel, BorderLayout.CENTER);

        add(bottomWrapper, BorderLayout.SOUTH);

        // 우측 버튼 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(35, 35, 40));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JButton clearBoardBtn = new JButton("전체 회수");
        JButton resetBtn = new JButton("초기화");

        styleSideButton(clearBoardBtn);
        styleSideButton(resetBtn);

        clearBoardBtn.addActionListener(e -> collectAllUnitsToBench());
        resetBtn.addActionListener(e -> resetAll());

        rightPanel.add(clearBoardBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(resetBtn);

        add(rightPanel, BorderLayout.EAST);

        refreshUI();
    }

    private void styleSideButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBackground(new Color(90, 120, 180));
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(120, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void handleBenchClick(int index) {
        if (index >= benchUnits.size()) return;

        selectedUnit = benchUnits.get(index);
        selectedBenchIndex = index;
        infoLabel.setText("선택됨: " + selectedUnit.name + " (" + selectedUnit.role + ") → 보드 칸을 클릭하세요.");
        refreshUI();
    }

    private void handleBoardClick(int row, int col) {
        Unit boardUnit = boardUnits[row][col];

        // 벤치에서 유닛을 선택한 상태
        if (selectedUnit != null && selectedBenchIndex != -1) {
            if (boardUnit == null) {
                // 빈칸이면 배치
                boardUnits[row][col] = selectedUnit;
                benchUnits.remove(selectedBenchIndex);
                infoLabel.setText(selectedUnit.name + " 배치 완료");
            } else {
                // 이미 유닛이 있으면 교체
                benchUnits.set(selectedBenchIndex, boardUnit);
                boardUnits[row][col] = selectedUnit;
                infoLabel.setText(selectedUnit.name + " 와(과) " + boardUnit.name + " 교체");
            }

            selectedUnit = null;
            selectedBenchIndex = -1;
            refreshUI();
            return;
        }

        // 아무것도 선택 안 된 상태에서 보드 클릭
        if (boardUnit != null) {
            // 벤치 자리 있으면 회수
            if (benchUnits.size() < BENCH_SIZE) {
                benchUnits.add(boardUnit);
                boardUnits[row][col] = null;
                infoLabel.setText(boardUnit.name + " 를 대기석으로 회수했습니다.");
            } else {
                infoLabel.setText("대기석이 가득 차서 회수할 수 없습니다.");
            }
        } else {
            infoLabel.setText("빈 칸입니다.");
        }

        refreshUI();
    }

    private void collectAllUnitsToBench() {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                if (boardUnits[r][c] != null) {
                    if (benchUnits.size() < BENCH_SIZE) {
                        benchUnits.add(boardUnits[r][c]);
                        boardUnits[r][c] = null;
                    }
                }
            }
        }
        selectedUnit = null;
        selectedBenchIndex = -1;
        infoLabel.setText("가능한 유닛을 모두 대기석으로 회수했습니다.");
        refreshUI();
    }

    private void resetAll() {
        selectedUnit = null;
        selectedBenchIndex = -1;

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                boardUnits[r][c] = null;
            }
        }

        benchUnits.clear();
        initUnits();

        infoLabel.setText("초기화 완료");
        refreshUI();
    }

    private void refreshUI() {
        // 보드 UI 반영
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                JButton btn = boardButtons[r][c];
                Unit unit = boardUnits[r][c];

                if (unit == null) {
                    btn.setText("<html><center>배치<br>가능</center></html>");
                    btn.setBackground(new Color(70, 70, 80));
                } else {
                    btn.setText("<html><center>" + unit.name + "<br>[" + unit.role + "]</center></html>");
                    btn.setBackground(getRoleColor(unit.role));
                }
            }
        }

        // 벤치 UI 반영
        for (int i = 0; i < BENCH_SIZE; i++) {
            JButton btn = benchButtons[i];

            if (i < benchUnits.size()) {
                Unit unit = benchUnits.get(i);
                btn.setText("<html><center>" + unit.name + "<br>[" + unit.role + "]</center></html>");
                btn.setBackground(getRoleColor(unit.role));

                if (i == selectedBenchIndex) {
                    btn.setBorder(new LineBorder(Color.YELLOW, 4));
                } else {
                    btn.setBorder(new LineBorder(new Color(150, 150, 170), 2));
                }
            } else {
                btn.setText("빈칸");
                btn.setBackground(new Color(55, 55, 60));
                btn.setBorder(new LineBorder(new Color(110, 110, 125), 2));
            }
        }
    }

    private Color getRoleColor(String role) {
        switch (role) {
            case "딜러":
                return new Color(180, 70, 70);
            case "탱커":
                return new Color(80, 110, 180);
            case "힐러":
                return new Color(70, 160, 120);
            case "마법":
                return new Color(140, 90, 180);
            case "보조":
                return new Color(200, 150, 70);
            case "골드":
                return new Color(180, 140, 50);
            case "원딜":
                return new Color(160, 90, 90);
            case "전사":
                return new Color(120, 120, 120);
            default:
                return new Color(100, 100, 100);
        }
    }

    static class Unit {
        String name;
        String role;

        public Unit(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PosamDefensePlacementTool::new);
    }
}