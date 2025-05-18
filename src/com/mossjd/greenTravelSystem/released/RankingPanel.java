package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:23
 */
// RankingPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RankingPanel extends JPanel implements CanBeReloaded{
    private MainFrame mainFrame;
    private JTable rankingTable;
    private JComboBox<String> timeRangeComboBox;

    public RankingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        initUI();
        loadRankingData("all");
    }
    @Override
    public void reloadData() {
        loadRankingData("all");
    }

    private void initUI() {
        // 顶部筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("时间范围:"));
        timeRangeComboBox = new JComboBox<>(new String[]{"全部时间", "本月", "本周"});
        timeRangeComboBox.addActionListener(e -> {
            String selected = (String)timeRangeComboBox.getSelectedItem();
            String range = "all";
            if ("本月".equals(selected)) range = "month";
            else if ("本周".equals(selected)) range = "week";
            loadRankingData(range);
        });
        filterPanel.add(timeRangeComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // 中间表格
        rankingTable = new JTable();
        rankingTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRankingData(String timeRange) {
        String sql = "SELECT u.username, SUM(tr.carbon_reduction) AS total_reduction " +
                "FROM travel_records tr JOIN users u ON tr.user_id = u.user_id ";

        switch (timeRange) {
            case "month":
                sql += "WHERE tr.travel_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) ";
                break;
            case "week":
                sql += "WHERE tr.travel_date >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK) ";
                break;
        }

        sql += "GROUP BY u.user_id ORDER BY total_reduction DESC LIMIT 50";

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"排名", "用户名", "累计碳减排(kg)"});

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rank++,
                        rs.getString("username"),
                        String.format("%.2f", rs.getDouble("total_reduction"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载排行榜数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        rankingTable.setModel(model);
    }
}