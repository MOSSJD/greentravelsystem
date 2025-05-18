package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:19
 */
// GreenTravelSystem.java

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class GreenTravelSystem {
    public static void main(String[] args) {
        // 设置外观为系统默认
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // Set up flatLightLaf theme
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}