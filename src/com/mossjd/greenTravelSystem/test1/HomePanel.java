package com.mossjd.greenTravelSystem.test1;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:43
 */
import com.mossjd.greenTravelSystem.released.NewsPanel;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private MapPanel mapPanel;
    private NewsPanel newsPanel;

    public HomePanel() {
        setLayout(new BorderLayout(10, 10));

        // 创建地图面板
        mapPanel = new MapPanel();

        // 创建资讯面板
        newsPanel = new NewsPanel();

        // 添加组件
        add(mapPanel, BorderLayout.CENTER);
        add(newsPanel, BorderLayout.EAST);
    }
}