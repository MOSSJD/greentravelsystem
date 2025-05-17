package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:23
 */
// DataAnalysisPanel.java

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAnalysisPanel extends JPanel {
    private int userId;
    private JTabbedPane analysisTabbedPane;

    public DataAnalysisPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        initUI();
        loadAnalysisData();
    }

    private void initUI() {
        analysisTabbedPane = new JTabbedPane();

        // 添加分析选项卡
        analysisTabbedPane.addTab("出行方式统计", createTravelModePanel());
        analysisTabbedPane.addTab("碳减排趋势", createCarbonTrendPanel());
        analysisTabbedPane.addTab("出行距离分布", createDistancePanel());

        add(analysisTabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTravelModePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 表格数据
        String sql = "SELECT travel_mode, COUNT(*) AS count, SUM(distance_km) AS total_distance, " +
                "SUM(carbon_reduction) AS total_reduction FROM travel_records " +
                "WHERE user_id = ? GROUP BY travel_mode";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"出行方式", "次数", "总距离(km)", "总碳减排(kg)"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("travel_mode"),
                            rs.getInt("count"),
                            rs.getObject("total_distance") != null ?
                                    String.format("%.2f", rs.getDouble("total_distance")) : "N/A",
                            String.format("%.2f", rs.getDouble("total_reduction"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载出行方式数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 添加图表
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < model.getRowCount(); i++) {
            String mode = (String)model.getValueAt(i, 0);
            int count = (Integer)model.getValueAt(i, 1);
            dataset.addValue(count, "出行次数", mode);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "出行方式统计",
                "出行方式",
                "次数",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);


        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCarbonTrendPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 按月份统计碳减排
        String sql = "SELECT DATE_FORMAT(travel_date, '%Y-%m') AS month, " +
                "SUM(carbon_reduction) AS monthly_reduction " +
                "FROM travel_records WHERE user_id = ? " +
                "GROUP BY DATE_FORMAT(travel_date, '%Y-%m') " +
                "ORDER BY month";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dataset.addValue(
                            rs.getDouble("monthly_reduction"),
                            "碳减排量",
                            rs.getString("month")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载碳减排趋势数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "碳减排趋势",
                "月份",
                "碳减排量(kg)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDistancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 出行距离分布
        String sql = "SELECT travel_mode, AVG(distance_km) AS avg_distance, " +
                "MAX(distance_km) AS max_distance, MIN(distance_km) AS min_distance " +
                "FROM travel_records WHERE user_id = ? AND distance_km IS NOT NULL " +
                "GROUP BY travel_mode";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"出行方式", "平均距离(km)", "最大距离(km)", "最小距离(km)"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("travel_mode"),
                            String.format("%.2f", rs.getDouble("avg_distance")),
                            String.format("%.2f", rs.getDouble("max_distance")),
                            String.format("%.2f", rs.getDouble("min_distance"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载出行距离数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.NORTH);

        // 添加饼图
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < model.getRowCount(); i++) {
            String mode = (String)model.getValueAt(i, 0);
            double avg = Double.parseDouble(model.getValueAt(i, 1).toString());
            dataset.addValue(avg, "平均距离", mode);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "各出行方式平均距离",
                "出行方式",
                "距离(km)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadAnalysisData() {
        // 数据已在各子面板中加载
    }
}