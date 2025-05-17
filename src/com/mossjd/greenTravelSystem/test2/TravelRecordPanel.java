package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:17
 */
// TravelRecordPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TravelRecordPanel extends JPanel {
    private JTable recordTable;
    private int userId;
    private JComboBox<String> modeComboBox;

    public TravelRecordPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        initUI();
        loadTravelRecords();
    }

    private void initUI() {
        // 顶部面板 - 添加记录
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 出行方式
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("出行方式:"), gbc);

        gbc.gridx = 1;
        modeComboBox = new JComboBox<>(new String[]{"公交", "地铁", "共享单车", "共享电动车", "步行"});
        topPanel.add(modeComboBox, gbc);

        // 出发地
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("出发地:"), gbc);

        gbc.gridx = 1;
        JTextField startField = new JTextField(15);
        topPanel.add(startField, gbc);

        // 目的地
        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("目的地:"), gbc);

        gbc.gridx = 1;
        JTextField endField = new JTextField(15);
        topPanel.add(endField, gbc);

        // 距离
        gbc.gridx = 0;
        gbc.gridy = 3;
        topPanel.add(new JLabel("距离(公里):"), gbc);

        gbc.gridx = 1;
        JTextField distanceField = new JTextField(15);
        topPanel.add(distanceField, gbc);

        // 添加按钮
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton addButton = new JButton("添加记录");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mode = (String)modeComboBox.getSelectedItem();
                String start = startField.getText();
                String end = endField.getText();
                String distanceStr = distanceField.getText();

                if (start.isEmpty() || end.isEmpty()) {
                    JOptionPane.showMessageDialog(TravelRecordPanel.this, "出发地和目的地不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double distance = 0;
                try {
                    if (!distanceStr.isEmpty()) {
                        distance = Double.parseDouble(distanceStr);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TravelRecordPanel.this, "请输入有效的距离数值", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                addTravelRecord(mode, start, end, distance);
                startField.setText("");
                endField.setText("");
                distanceField.setText("");
                loadTravelRecords();
            }
        });
        topPanel.add(addButton, gbc);

        add(topPanel, BorderLayout.NORTH);

        // 中间表格
        recordTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(recordTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addTravelRecord(String mode, String start, String end, double distance) {
        // 计算积分和碳减排
        int points = calculatePoints(mode, distance);
        double carbonReduction = calculateCarbonReduction(mode, distance);

        String sql = "INSERT INTO travel_records (user_id, travel_mode, start_location, end_location, distance_km, points_earned, carbon_reduction) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, mode);
            pstmt.setString(3, start);
            pstmt.setString(4, end);
            if (distance > 0) {
                pstmt.setDouble(5, distance);
            } else {
                pstmt.setNull(5, Types.DOUBLE);
            }
            pstmt.setInt(6, points);
            pstmt.setDouble(7, carbonReduction);

            pstmt.executeUpdate();

            // 更新用户总积分
            updateUserPoints(points);

            JOptionPane.showMessageDialog(this, "出行记录添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加出行记录失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculatePoints(String mode, double distance) {
        switch (mode) {
            case "公交":
                return 2; // 每次公交积2分
            case "地铁":
                return 3; // 每次地铁积3分
            case "共享单车":
            case "共享电动车":
                return distance > 0 ? (int)distance : 1; // 每公里积1分，不足1公里按1分算
            case "步行":
                return distance > 0 ? (int)(distance * 0.5) : 1; // 每公里积0.5分，不足1公里按1分算
            default:
                return 0;
        }
    }

    private double calculateCarbonReduction(String mode, double distance) {
        switch (mode) {
            case "公交":
                return 0.5; // 每次公交减少0.5kg碳排放
            case "地铁":
                return 0.6; // 每次地铁减少0.6kg碳排放
            case "共享单车":
                return distance > 0 ? distance * 0.2 : 0.2; // 每公里减少0.2kg碳排放
            case "共享电动车":
                return distance > 0 ? distance * 0.15 : 0.15; // 每公里减少0.15kg碳排放
            case "步行":
                return distance > 0 ? distance * 0.1 : 0.1; // 每公里减少0.1kg碳排放
            default:
                return 0;
        }
    }

    private void updateUserPoints(int points) {
        String sql = "INSERT INTO user_points (user_id, total_points) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE total_points = total_points + ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, points);
            pstmt.setInt(3, points);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTravelRecords() {
        String sql = "SELECT travel_date, travel_mode, start_location, end_location, distance_km, points_earned, carbon_reduction " +
                "FROM travel_records WHERE user_id = ? ORDER BY travel_date DESC";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"日期", "方式", "出发地", "目的地", "距离(km)", "获得积分", "碳减排(kg)"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("travel_date");
                    String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);

                    model.addRow(new Object[]{
                            dateStr,
                            rs.getString("travel_mode"),
                            rs.getString("start_location"),
                            rs.getString("end_location"),
                            rs.getObject("distance_km"),
                            rs.getInt("points_earned"),
                            String.format("%.2f", rs.getDouble("carbon_reduction"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载出行记录失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        recordTable.setModel(model);
    }
}