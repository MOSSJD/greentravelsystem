package com.mossjd.greenTravelSystem.released;

/**
 * @author MOSSJD
 * @create 2025-05-17-11:16
 */
// MainFrame.java

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements CanBeReloaded{
    private JTabbedPane tabbedPane;
    private int currentUserId;

    public MainFrame(int userId) {
        this.currentUserId = userId;
        setTitle("绿色出行倡导与数据记录系统");
        setSize(800, 600);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("Main window closed.");
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // 添加各个功能选项卡
        tabbedPane.addTab("个人信息", new UserInfoPanel(currentUserId, this));
        tabbedPane.addTab("出行线路", new RouteInfoPanel());
        tabbedPane.addTab("共享交通", new SharedVehiclePanel());
        tabbedPane.addTab("环保资讯", new NewsPanel());
        tabbedPane.addTab("出行记录", new TravelRecordPanel(currentUserId, this));
        tabbedPane.addTab("积分查询", new PointsPanel(currentUserId, this));
        tabbedPane.addTab("碳减排", new CarbonReductionPanel(currentUserId, this));
        tabbedPane.addTab("排行榜", new RankingPanel(this));
        tabbedPane.addTab("数据分析", new DataAnalysisPanel(currentUserId, this));
        tabbedPane.addTab("好友动态", new FriendsPanel(currentUserId, this));

        add(tabbedPane);
    }
    @Override
    public void reloadData() {
        for (var tab : tabbedPane.getComponents()) {
            if (tab instanceof CanBeReloaded tabToBeReloaded) {
                tabToBeReloaded.reloadData();
            }
        }
    }
}