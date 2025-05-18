package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:43
 */
// NewsPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class NewsPanel extends JPanel implements CanBeReloaded{
    private JTable newsTable;
    private JTextArea contentArea;
    private JComboBox<String> categoryCombo;

    public NewsPanel() {
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadNewsData(null);
    }
    @Override
    public void reloadData() {
        loadNewsData(null);
    }

    private void initUI() {
        // 顶部筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("资讯分类:"));
        categoryCombo = new JComboBox<>(new String[]{"全部", "环保知识", "政策法规", "活动通知"});
        categoryCombo.addActionListener(e -> {
            String selected = (String)categoryCombo.getSelectedItem();
            loadNewsData("全部".equals(selected) ? null : selected);
        });
        filterPanel.add(categoryCombo);

        add(filterPanel, BorderLayout.NORTH);

        // 中间分割面板 - 左侧表格，右侧内容
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // 左侧 - 资讯列表表格
        newsTable = new JTable();
        newsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedNewsContent();
            }
        });
        JScrollPane tableScroll = new JScrollPane(newsTable);
        splitPane.setLeftComponent(tableScroll);

        // 右侧 - 资讯内容区域
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder("资讯内容"));

        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentPanel.add(contentScroll, BorderLayout.CENTER);

        // 添加发布日期标签
        JLabel dateLabel = new JLabel(" ", SwingConstants.RIGHT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(dateLabel, BorderLayout.SOUTH);

        splitPane.setRightComponent(contentPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadNewsData(String category) {
        String sql = "SELECT news_id, title, publish_date, category FROM news";
        if (category != null) {
            sql += " WHERE category = ?";
        }
        sql += " ORDER BY publish_date DESC";

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        model.setColumnIdentifiers(new String[]{"ID", "标题", "发布日期", "分类"});

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (category != null) {
                pstmt.setString(1, category);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("news_id"),
                            rs.getString("title"),
                            sdf.format(rs.getTimestamp("publish_date")),
                            rs.getString("category")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载资讯数据失败", "错误", JOptionPane.ERROR_MESSAGE);
        }

        newsTable.setModel(model);

        // 隐藏ID列
        newsTable.removeColumn(newsTable.getColumnModel().getColumn(0));

        // 设置列宽
        newsTable.getColumnModel().getColumn(0).setPreferredWidth(200); // 标题
        newsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 日期
        newsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // 分类
    }

    private void showSelectedNewsContent() {
        int selectedRow = newsTable.getSelectedRow();
        if (selectedRow < 0) return;

        int newsId = (Integer)newsTable.getModel().getValueAt(selectedRow, 0);

        String sql = "SELECT title, content, publish_date FROM news WHERE news_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newsId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 显示标题和内容
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    contentArea.setText(title + "\n\n" + content);

                    // 更新底部日期标签
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                    String dateStr = "发布日期: " + sdf.format(rs.getTimestamp("publish_date"));

                    // 查找底部标签并更新
                    Component[] comps = ((JPanel)((JSplitPane)getComponent(1)).getRightComponent()).getComponents();
                    for (Component comp : comps) {
                        if (comp instanceof JLabel label) {
                            label.setText(dateStr);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载资讯内容失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}