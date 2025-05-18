package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:17
 */
// SharedVehiclePanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SharedVehiclePanel extends JPanel implements CanBeReloaded{
    private MainFrame mainFrame;
    private JTable vehicleTable;
    private JComboBox<String> typeComboBox, regionComboBox;

    public SharedVehiclePanel() {
        setLayout(new BorderLayout());

        initUI();
        loadVehicleData(null, null);
    }
    @Override
    public void reloadData() {
        loadVehicleData(null, null);
    }

    private void initUI() {
        // 顶部筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("类型:"));
        typeComboBox = new JComboBox<>(new String[]{"全部", "共享单车", "共享电动车"});
        typeComboBox.addActionListener(e -> applyFilters());
        filterPanel.add(typeComboBox);

        filterPanel.add(new JLabel("地区:"));
        regionComboBox = new JComboBox<>(new String[]{"全部", "象山区", "七星区", "临桂区", "秀峰区", "灵川县"});
        regionComboBox.addActionListener(e -> applyFilters());
        filterPanel.add(regionComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // 中间表格
        vehicleTable = new JTable();
        vehicleTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void applyFilters() {
        String type = (String)typeComboBox.getSelectedItem();
        String region = (String)regionComboBox.getSelectedItem();

        loadVehicleData("全部".equals(type) ? null : type,
                "全部".equals(region) ? null : region);
    }

    private void loadVehicleData(String type, String region) {
        StringBuilder sql = new StringBuilder("SELECT vehicle_type, location, available_count, region, last_update FROM shared_vehicles WHERE 1=1");

        if (type != null) {
            sql.append(" AND vehicle_type = ?");
        }
        if (region != null) {
            sql.append(" AND region = ?");
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"类型", "位置", "可用数量", "地区", "最后更新时间"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (type != null) {
                pstmt.setString(paramIndex++, type);
            }
            if (region != null) {
                pstmt.setString(paramIndex++, region);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("vehicle_type"),
                            rs.getString("location"),
                            rs.getInt("available_count"),
                            rs.getString("region"),
                            rs.getTimestamp("last_update")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载共享交通工具数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        vehicleTable.setModel(model);
    }
}