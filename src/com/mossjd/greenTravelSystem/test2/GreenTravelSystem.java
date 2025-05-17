package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:19
 */
// GreenTravelSystem.java
import javax.swing.*;

public class GreenTravelSystem {
    public static void main(String[] args) {
        // 设置外观为系统默认
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}