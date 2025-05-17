package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:17
 */
// UserInfoPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserInfoPanel extends JPanel {
    private int userId;
    private JLabel nameLabel, phoneLabel, regionLabel, usernameLabel;
    private JButton editButton;

    public UserInfoPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        initUI();
        loadUserInfo();
    }

    private void initUI() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameLabel = new JLabel();
        infoPanel.add(usernameLabel, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        nameLabel = new JLabel();
        infoPanel.add(nameLabel, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        phoneLabel = new JLabel();
        infoPanel.add(phoneLabel, gbc);

        // 地区
        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(new JLabel("地区:"), gbc);

        gbc.gridx = 1;
        regionLabel = new JLabel();
        infoPanel.add(regionLabel, gbc);

        // 编辑按钮
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        editButton = new JButton("编辑信息");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditDialog();
            }
        });
        infoPanel.add(editButton, gbc);

        add(infoPanel, BorderLayout.CENTER);
    }

    private void loadUserInfo() {
        String sql = "SELECT username, name, phone, region FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usernameLabel.setText(rs.getString("username"));
                    nameLabel.setText(rs.getString("name"));
                    phoneLabel.setText(rs.getString("phone"));
                    regionLabel.setText(rs.getString("region"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载用户信息失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog() {
        JDialog editDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "编辑个人信息", true);
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(nameLabel.getText(), 15);
        panel.add(nameField, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(phoneLabel.getText(), 15);
        panel.add(phoneField, gbc);

        // 地区
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("地区:"), gbc);

        gbc.gridx = 1;
        JTextField regionField = new JTextField(regionLabel.getText(), 15);
        panel.add(regionField, gbc);

        // 保存按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String region = regionField.getText();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "姓名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (AuthService.updateUserInfo(userId, name, phone, region)) {
                    JOptionPane.showMessageDialog(editDialog, "信息更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadUserInfo();
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "信息更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(saveButton, gbc);

        editDialog.add(panel);
        editDialog.setVisible(true);
    }
}