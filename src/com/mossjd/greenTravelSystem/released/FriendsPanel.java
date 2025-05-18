package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:24
 */
// FriendsPanel.java
// FriendsPanel.java

// FriendsPanel.java

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FriendsPanel extends JPanel implements CanBeReloaded{
    private MainFrame mainFrame;
    private int userId;
    private JTable friendsTable;
    private JTable postsTable;
    private JTable commentsTable;
    private JTextArea postContentArea;
    private JTextArea commentContentArea;
    private JComboBox<String> friendsComboBox;
    private DefaultTableModel friendsModel;
    private DefaultTableModel postsModel;
    private DefaultTableModel commentsModel;
    private int currentSelectedPostId = -1;

    public FriendsPanel(int userId, MainFrame mainFrame) {
        this.userId = userId;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadFriendsData();
    }
    @Override
    public void reloadData() {
        loadFriendsData();
    }

    private void initUI() {
        JTabbedPane friendsTabbedPane = new JTabbedPane();

        // 1. 好友列表选项卡
        JPanel friendsListPanel = createFriendsListPanel();
        friendsTabbedPane.addTab("好友列表", friendsListPanel);

        // 2. 动态互动选项卡
        JPanel postPanel = createPostAndCommentPanel();
        friendsTabbedPane.addTab("动态互动", postPanel);

        add(friendsTabbedPane, BorderLayout.CENTER);
    }

    private JPanel createFriendsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 好友表格
        friendsModel = new DefaultTableModel(new String[]{"好友ID", "用户名", "最近活跃"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        friendsTable = new JTable(friendsModel);
        friendsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = friendsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String friendName = (String) friendsModel.getValueAt(selectedRow, 1);
                    friendsComboBox.setSelectedItem(friendName);
                    loadFriendPosts();
                }
            }
        });

        panel.add(new JScrollPane(friendsTable), BorderLayout.CENTER);

        // 添加好友按钮
        JButton addFriendButton = new JButton("添加好友");
        addFriendButton.addActionListener(e -> showAddFriendDialog());
        panel.add(addFriendButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPostAndCommentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 发布动态区域
        JPanel postInputPanel = new JPanel(new BorderLayout());
        postInputPanel.setBorder(BorderFactory.createTitledBorder("发布新动态"));

        postContentArea = new JTextArea(5, 30);
        postContentArea.setLineWrap(true);
        postContentArea.setWrapStyleWord(true);

        JButton postButton = new JButton("发布");
        postButton.addActionListener(e -> postNewContent());

        postInputPanel.add(new JScrollPane(postContentArea), BorderLayout.CENTER);
        postInputPanel.add(postButton, BorderLayout.SOUTH);

        // 查看好友动态区域
        JPanel viewPanel = new JPanel(new BorderLayout());

        // 好友选择区域
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("选择好友:"));

        friendsComboBox = new JComboBox<>();
        friendsComboBox.addActionListener(e -> loadFriendPosts());
        filterPanel.add(friendsComboBox);

        viewPanel.add(filterPanel, BorderLayout.NORTH);

        // 动态和评论分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.5);

        // 动态表格
        postsModel = new DefaultTableModel(new String[]{"ID", "时间", "内容"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        postsTable = new JTable(postsModel);
        postsTable.getColumnModel().getColumn(0).setMinWidth(0); // 隐藏ID列
        postsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        postsTable.getColumnModel().getColumn(0).setWidth(0);
        postsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && postsTable.getSelectedRow() >= 0) {
                currentSelectedPostId = (int) postsModel.getValueAt(postsTable.getSelectedRow(), 0);
                loadCommentsForPost(currentSelectedPostId);
            }
        });

        splitPane.setTopComponent(new JScrollPane(postsTable));

        // 评论区域
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createTitledBorder("评论"));

        // 评论表格
        commentsModel = new DefaultTableModel(new String[]{"用户", "时间", "内容"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        commentsTable = new JTable(commentsModel);
        commentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        commentsTable.getColumnModel().getColumn(1).setPreferredWidth(120);

        commentPanel.add(new JScrollPane(commentsTable), BorderLayout.CENTER);

        // 发表评论区域
        JPanel commentInputPanel = new JPanel(new BorderLayout());
        commentContentArea = new JTextArea(3, 30);
        commentContentArea.setLineWrap(true);
        commentContentArea.setWrapStyleWord(true);

        JButton commentButton = new JButton("发表评论");
        commentButton.addActionListener(e -> postNewComment());

        commentInputPanel.add(new JScrollPane(commentContentArea), BorderLayout.CENTER);
        commentInputPanel.add(commentButton, BorderLayout.SOUTH);

        commentPanel.add(commentInputPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(commentPanel);
        viewPanel.add(splitPane, BorderLayout.CENTER);

        panel.add(postInputPanel, BorderLayout.NORTH);
        panel.add(viewPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadFriendsData() {
        friendsModel.setRowCount(0);
        friendsComboBox.removeAllItems();

        String sql = "SELECT u.user_id, u.username, MAX(p.post_time) AS last_active " +
                "FROM users u JOIN friendships f ON (u.user_id = f.user1_id OR u.user_id = f.user2_id) " +
                "LEFT JOIN posts p ON u.user_id = p.user_id " +
                "WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.user_id != ? " +
                "GROUP BY u.user_id, u.username";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getTimestamp("last_active") != null ?
                                    sdf.format(rs.getTimestamp("last_active")) : "从未发帖"
                    };
                    friendsModel.addRow(row);
                    friendsComboBox.addItem(rs.getString("username"));
                }
            }

            if (friendsModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "您还没有好友，请先添加好友",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载好友列表失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFriendPosts() {
        postsModel.setRowCount(0);
        commentsModel.setRowCount(0);
        currentSelectedPostId = -1;

        String selectedFriend = (String) friendsComboBox.getSelectedItem();
        if (selectedFriend == null || selectedFriend.isEmpty()) return;

        String sql = "SELECT p.post_id, p.content, p.post_time, u.username FROM posts p " +
                "JOIN users u ON p.user_id = u.user_id " +
                "WHERE u.username = ? ORDER BY p.post_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, selectedFriend);

            try (ResultSet rs = pstmt.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                while (rs.next()) {
                    postsModel.addRow(new Object[]{
                            rs.getInt("post_id"),
                            sdf.format(rs.getTimestamp("post_time")),
                            rs.getString("content")
                    });
                }
            }

            if (postsModel.getRowCount() == 0) {
                postsModel.addRow(new Object[]{-1, "", "该好友还没有发布任何动态"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载好友动态失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCommentsForPost(int postId) {
        commentsModel.setRowCount(0);

        if (postId <= 0) return;

        String sql = "SELECT c.content, c.comment_time, u.username " +
                "FROM comments c JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ? ORDER BY c.comment_time";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                while (rs.next()) {
                    commentsModel.addRow(new Object[]{
                            rs.getString("username"),
                            sdf.format(rs.getTimestamp("comment_time")),
                            rs.getString("content")
                    });
                }
            }

            if (commentsModel.getRowCount() == 0) {
                commentsModel.addRow(new Object[]{"", "", "暂无评论"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载评论失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void postNewContent() {
        String content = postContentArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "动态内容不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO posts (user_id, content) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, content);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "动态发布成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                postContentArea.setText("");

                // 如果是查看自己的动态，刷新显示
                if (friendsComboBox.getSelectedItem() != null &&
                        friendsComboBox.getSelectedItem().equals(getCurrentUsername())) {
                    loadFriendPosts();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "动态发布失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void postNewComment() {
        if (currentSelectedPostId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条动态", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String content = commentContentArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "评论内容不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentSelectedPostId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, content);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "评论发布成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                commentContentArea.setText("");
                loadCommentsForPost(currentSelectedPostId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "评论发布失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getCurrentUsername() {
        String sql = "SELECT username FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showAddFriendDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "添加好友", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel userLabel = new JLabel("用户名:");
        JTextField userField = new JTextField();
        JLabel msgLabel = new JLabel("验证消息:");
        JTextField msgField = new JTextField();

        inputPanel.add(userLabel);
        inputPanel.add(userField);
        inputPanel.add(msgLabel);
        inputPanel.add(msgField);
        inputPanel.add(new JLabel()); // 占位

        JButton submitButton = new JButton("发送请求");
        submitButton.addActionListener(e -> {
            String friendName = userField.getText().trim();
            String message = msgField.getText().trim();

            if (friendName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入用户名", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (addFriendRequest(friendName, message)) {
                JOptionPane.showMessageDialog(dialog, "好友请求已发送", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                mainFrame.reloadData();
            }
        });

        inputPanel.add(submitButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean addFriendRequest(String friendName, String message) {
        String sql = "INSERT INTO friendships (user1_id, user2_id) " +
                "SELECT ?, user_id FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, friendName);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "未找到用户: " + friendName, "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                JOptionPane.showMessageDialog(this, "你们已经是好友了", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加好友失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }
}