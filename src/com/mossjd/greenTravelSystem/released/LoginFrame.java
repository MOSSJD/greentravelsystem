package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:16
 */
// LoginFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 3;

    public LoginFrame() {
        setTitle("绿色出行系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 用户名标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // 密码标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        panel.add(loginButton, gbc);

        // 注册按钮
        gbc.gridy = 3;
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationForm();
            }
        });
        panel.add(registerButton, gbc);

        add(panel);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (AuthService.authenticate(username, password)) {
            int userId = AuthService.getUserId(username);
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(userId);
                mainFrame.setVisible(true);
                dispose();
            });
        } else {
            attemptCount++;
            if (attemptCount >= MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(this, "尝试次数过多，程序将退出", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误，还剩 " + (MAX_ATTEMPTS - attemptCount) + " 次尝试机会",
                        "登录失败", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showRegistrationForm() {
        JDialog registerDialog = new JDialog(this, "用户注册", true);
        registerDialog.setSize(400, 350);
        registerDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField regUsername = new JTextField(15);
        panel.add(regUsername, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField regPassword = new JPasswordField(15);
        panel.add(regPassword, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);

        // 地区
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("地区:"), gbc);

        gbc.gridx = 1;
        JTextField regionField = new JTextField(15);
        panel.add(regionField, gbc);

        // 注册按钮
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton registerBtn = new JButton("注册");
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = regUsername.getText();
                String password = new String(regPassword.getPassword());
                String name = nameField.getText();
                String phone = phoneField.getText();
                String region = regionField.getText();

                if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(registerDialog, "用户名、密码和姓名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (AuthService.registerUser(username, password, name, phone, region)) {
                    JOptionPane.showMessageDialog(registerDialog, "注册成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    registerDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(registerDialog, "注册失败，用户名可能已存在", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(registerBtn, gbc);

        registerDialog.add(panel);
        registerDialog.setVisible(true);
    }
}