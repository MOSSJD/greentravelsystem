package com.mossjd.greenTravelSystem.test2;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:16
 */
// MainFrame.java

import javax.swing.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private int currentUserId;

    public MainFrame(int userId) {
        this.currentUserId = userId;
        setTitle("绿色出行倡导与数据记录系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // 添加各个功能选项卡
        tabbedPane.addTab("个人信息", new UserInfoPanel(currentUserId));
        tabbedPane.addTab("出行线路", new RouteInfoPanel());
        tabbedPane.addTab("共享交通", new SharedVehiclePanel());
        tabbedPane.addTab("环保资讯", new NewsPanel());
        tabbedPane.addTab("出行记录", new TravelRecordPanel(currentUserId));
        tabbedPane.addTab("积分查询", new PointsPanel(currentUserId));
        tabbedPane.addTab("碳减排", new CarbonReductionPanel(currentUserId));
        tabbedPane.addTab("排行榜", new RankingPanel());
        tabbedPane.addTab("数据分析", new DataAnalysisPanel(currentUserId));
        tabbedPane.addTab("好友动态", new FriendsPanel(currentUserId));

        add(tabbedPane);
    }
}