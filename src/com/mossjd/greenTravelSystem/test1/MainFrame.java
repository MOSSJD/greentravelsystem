package com.mossjd.greenTravelSystem.test1;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:39
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // 主页面组件
    private HomePanel homePanel;
    private JPanel travelRecordPanel;
    private JPanel carbonReductionPanel;
    private JPanel rankingPanel;
    private JPanel dynamicPanel;

    public MainFrame() {
        setTitle("碳足迹地图应用");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // 创建菜单栏
        createMenuBar();

        // 创建主面板
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建各个页面
        homePanel = new HomePanel();
        travelRecordPanel = createPanel("出行记录");
        carbonReductionPanel = createPanel("碳减排数据");
        rankingPanel = createPanel("排行榜");
        dynamicPanel = createPanel("动态");

        // 添加页面到主面板
        mainPanel.add(homePanel, "主页");
        mainPanel.add(travelRecordPanel, "出行记录");
        mainPanel.add(carbonReductionPanel, "碳减排数据");
        mainPanel.add(rankingPanel, "排行榜");
        mainPanel.add(dynamicPanel, "动态");

        add(mainPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        String[] menuItems = {"主页", "出行记录", "碳减排数据", "排行榜", "动态"};

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, item);
                }
            });
            menuBar.add(button);
        }

        setJMenuBar(menuBar);
    }

    private JPanel createPanel(String title) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(title + "页面"));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}