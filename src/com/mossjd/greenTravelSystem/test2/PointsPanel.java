package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:16
 */
// PointsPanel.java
import com.mossjd.greenTravelSystem.test2.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PointsPanel extends JPanel {
    private int userId;
    private JLabel totalPointsLabel;
    private JTable pointsHistoryTable;

    public PointsPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        initUI();
        loadPointsData();
    }

    private void initUI() {
        // 顶部面板 - 总积分
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("您的当前积分:"));
        totalPointsLabel = new JLabel("加载中...");
        totalPointsLabel.setFont(new Font("Serif", Font.BOLD, 24));
        topPanel.add(totalPointsLabel);

        add(topPanel, BorderLayout.NORTH);

        // 中间表格 - 积分历史
        pointsHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(pointsHistoryTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPointsData() {
        // 加载总积分
        String totalPointsSql = "SELECT total_points FROM user_points WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(totalPointsSql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalPointsLabel.setText(String.valueOf(rs.getInt("total_points")));
                } else {
                    totalPointsLabel.setText("0");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalPointsLabel.setText("加载失败");
        }

        // 加载积分历史
        String historySql = "SELECT travel_date, travel_mode, points_earned FROM travel_records " +
                "WHERE user_id = ? ORDER BY travel_date DESC";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"日期", "出行方式", "获得积分"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(historySql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getTimestamp("travel_date"),
                            rs.getString("travel_mode"),
                            rs.getInt("points_earned")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载积分历史失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        pointsHistoryTable.setModel(model);
    }
}