package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:17
 */
// RouteInfoPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RouteInfoPanel extends JPanel implements CanBeReloaded{
    private JTable routeTable;
    private JComboBox<String> regionComboBox;

    public RouteInfoPanel() {
        setLayout(new BorderLayout());

        initUI();
        loadRouteData(null);
    }
    @Override
    public void reloadData() {
        loadRouteData(null);
    }

    private void initUI() {
        // 顶部筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("选择地区:"));
        regionComboBox = new JComboBox<>(new String[]{"全部", "象山区", "七星区", "临桂区", "秀峰区", "灵川县"});
        regionComboBox.addActionListener(e -> {
            String selectedRegion = (String)regionComboBox.getSelectedItem();
            loadRouteData("全部".equals(selectedRegion) ? null : selectedRegion);
        });
        filterPanel.add(regionComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // 中间表格
        routeTable = new JTable();
        routeTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(routeTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRouteData(String region) {
        String sql = "SELECT route_name, route_type, stations, operating_hours FROM travel_routes";
        if (region != null) {
            sql += " WHERE region = ?";
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"线路名称", "类型", "站点信息", "运营时间"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (region != null) {
                pstmt.setString(1, region);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("route_name"),
                            rs.getString("route_type"),
                            rs.getString("stations"),
                            rs.getString("operating_hours")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载线路数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        routeTable.setModel(model);
    }
}