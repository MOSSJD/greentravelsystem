package com.mossjd.greenTravelSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:03
 */
public class MyFrame extends JFrame {
    private Component component;
    private Panel mainPanel;
    private static final Panel menuBar;
    private static final ArrayList<Panel> mainPanels = new ArrayList<>(4);
    static {
        // Initialize main panels



        // Initialize menu bar
        menuBar = new Panel();
        menuBar.setLayout(new FlowLayout());
//        menuBar.add()
    }
    MyFrame() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        component = getContentPane();

        setLayout(new BorderLayout());

        // Initialize main panel
        mainPanel = mainPanels.get(0);
        add(mainPanel, BorderLayout.CENTER);

        // Add menu bar
//        add()

    }
}