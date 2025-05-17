package com.mossjd.greenTravelSystem.test1;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:43
 */
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class RoutePanel extends JPanel {
    private JTextArea routeTextArea;

    public RoutePanel() {
        setPreferredSize(new Dimension(300, 600));
        setBorder(new TitledBorder("路线信息"));
        setLayout(new BorderLayout());

        routeTextArea = new JTextArea();
        routeTextArea.setEditable(false);
        routeTextArea.setLineWrap(true);
        routeTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(routeTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateRoutes(List<List<MapPanel.Node>> paths) {
        StringBuilder sb = new StringBuilder();

        if (paths.isEmpty()) {
            sb.append("请选择起点和终点\n");
            sb.append("1. 点击第一个点设为起点(绿色)\n");
            sb.append("2. 点击第二个点设为终点(红色)\n");
            sb.append("3. 系统将自动计算路线\n");
        } else {
            sb.append("找到 ").append(paths.size()).append(" 条路线:\n\n");

            for (int i = 0; i < paths.size(); i++) {
                sb.append("路线 ").append(i + 1).append(":\n");

                List<MapPanel.Node> path = paths.get(i);
                double totalDistance = 0;

                // 计算总距离 (简化版)
                for (int j = 0; j < path.size() - 1; j++) {
                    MapPanel.Node node1 = path.get(j);
                    MapPanel.Node node2 = path.get(j + 1);
                    totalDistance += Math.sqrt(
                            Math.pow(node2.x - node1.x, 2) +
                                    Math.pow(node2.y - node1.y, 2)
                    );
                }

                // 步行时间 (假设步行速度 5km/h ≈ 1.39m/s)
                double walkTime = totalDistance / 1.39 / 60; // 分钟

                // 公交时间 (假设公交速度 30km/h ≈ 8.33m/s)
                double busTime = totalDistance / 8.33 / 60; // 分钟

                sb.append(" - 距离: ").append(String.format("%.1f", totalDistance)).append(" 米\n");
                sb.append(" - 步行时间: ").append(String.format("%.1f", walkTime)).append(" 分钟\n");
                sb.append(" - 公交时间: ").append(String.format("%.1f", busTime)).append(" 分钟\n");

                sb.append(" - 途径: ");
                for (MapPanel.Node node : path) {
                    sb.append(node.id).append(" -> ");
                }
                sb.delete(sb.length() - 4, sb.length()); // 移除最后的 " -> "
                sb.append("\n\n");
            }
        }

        routeTextArea.setText(sb.toString());
    }
}