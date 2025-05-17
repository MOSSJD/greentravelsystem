package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:22
 */
// CarbonReductionPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CarbonReductionPanel extends JPanel {
    private int userId;
    private JLabel totalReductionLabel;
    private JTable reductionTable;

    public CarbonReductionPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        initUI();
        loadCarbonReductionData();
    }

    private void initUI() {
        // 顶部面板 - 总碳减排
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("您的累计碳减排:"));
        totalReductionLabel = new JLabel("加载中...");
        totalReductionLabel.setFont(new Font("Serif", Font.BOLD, 24));
        topPanel.add(totalReductionLabel);
        topPanel.add(new JLabel("kg CO₂"));

        add(topPanel, BorderLayout.NORTH);

        // 中间表格 - 碳减排历史
        reductionTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(reductionTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCarbonReductionData() {
        // 加载总碳减排量
        String totalSql = "SELECT SUM(carbon_reduction) AS total FROM travel_records WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(totalSql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    totalReductionLabel.setText(String.format("%.2f", total));
                } else {
                    totalReductionLabel.setText("0.00");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalReductionLabel.setText("加载失败");
        }

        // 加载碳减排历史
        String historySql = "SELECT travel_date, travel_mode, carbon_reduction FROM travel_records " +
                "WHERE user_id = ? ORDER BY travel_date DESC";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"日期", "出行方式", "碳减排量(kg)"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(historySql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getTimestamp("travel_date"),
                            rs.getString("travel_mode"),
                            String.format("%.2f", rs.getDouble("carbon_reduction"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载碳减排历史失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        reductionTable.setModel(model);
    }
}