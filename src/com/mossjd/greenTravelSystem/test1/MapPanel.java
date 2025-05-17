package com.mossjd.greenTravelSystem.test1;

/**
 * @author MOSSJD
 * @create 2025-05-17-10:43
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class MapPanel extends JPanel {
    private static final int NODE_RADIUS = 10;
    private static final Color NODE_COLOR = Color.BLUE;
    private static final Color EDGE_COLOR = Color.BLACK;
    private static final Color SELECTED_NODE_COLOR1 = Color.GREEN;
    private static final Color SELECTED_NODE_COLOR2 = Color.RED;
    private static final Color PATH_COLOR = new Color(0, 100, 255);

    private double scale = 1.0;
    private Point2D.Double viewOffset = new Point2D.Double(0, 0);
    private Point lastMousePos;

    private Map<String, Node> nodes = new HashMap<>();
    private List<Edge> edges = new ArrayList<>();
    private List<BusRoute> busRoutes = new ArrayList<>();

    private Node selectedNode1;
    private Node selectedNode2;
    private List<List<Node>> calculatedPaths = new ArrayList<>();

    private RoutePanel routePanel;

    public MapPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // 初始化测试数据
        initializeTestData();

        // 添加鼠标监听器
        addMouseListeners();

        // 添加鼠标滚轮监听器
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double scaleFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                scale *= scaleFactor;
                repaint();
            }
        });
    }

    public void setRoutePanel(RoutePanel routePanel) {
        this.routePanel = routePanel;
    }

    private void initializeTestData() {
        // 创建节点
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String id = "N" + i + "_" + j;
                Node node = new Node(id, 100 + i * 80, 100 + j * 80);
                nodes.put(id, node);
            }
        }

        // 创建边
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String id1 = "N" + i + "_" + j;
                if (i < 9) {
                    String id2 = "N" + (i + 1) + "_" + j;
                    edges.add(new Edge(nodes.get(id1), nodes.get(id2), rand.nextInt(50) + 50));
                }
                if (j < 9) {
                    String id2 = "N" + i + "_" + (j + 1);
                    edges.add(new Edge(nodes.get(id1), nodes.get(id2), rand.nextInt(50) + 50));
                }
            }
        }

        // 创建公交路线
        List<Node> busStops1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            busStops1.add(nodes.get("N" + i + "_3"));
        }
        busRoutes.add(new BusRoute("公交1路", busStops1));

        List<Node> busStops2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            busStops2.add(nodes.get("N5_" + i));
        }
        busRoutes.add(new BusRoute("公交2路", busStops2));
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleNodeSelection(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMousePos = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePos != null && SwingUtilities.isRightMouseButton(e)) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;

                    viewOffset.x += dx / scale;
                    viewOffset.y += dy / scale;

                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });
    }

    private void handleNodeSelection(Point mousePoint) {
        Node closestNode = findClosestNode(mousePoint);
        if (closestNode != null) {
            if (selectedNode1 == null) {
                selectedNode1 = closestNode;
            } else if (selectedNode2 == null && !closestNode.equals(selectedNode1)) {
                selectedNode2 = closestNode;
                calculatePaths();
            } else {
                selectedNode1 = closestNode;
                selectedNode2 = null;
                calculatedPaths.clear();
            }
            repaint();

            if (routePanel != null) {
                routePanel.updateRoutes(calculatedPaths);
            }
        }
    }

    private Node findClosestNode(Point mousePoint) {
        double minDist = Double.MAX_VALUE;
        Node closestNode = null;

        for (Node node : nodes.values()) {
            double screenX = node.x * scale + viewOffset.x;
            double screenY = node.y * scale + viewOffset.y;

            double dist = mousePoint.distance(screenX, screenY);
            if (dist < NODE_RADIUS * 2 && dist < minDist) {
                minDist = dist;
                closestNode = node;
            }
        }

        return closestNode;
    }

    private void calculatePaths() {
        calculatedPaths.clear();

        if (selectedNode1 == null || selectedNode2 == null) {
            return;
        }

        // 简化的最短路径算法 (实际应用中可以使用Dijkstra或A*算法)
        // 这里只是示例，返回几条可能的路径
        List<Node> path1 = new ArrayList<>();
        path1.add(selectedNode1);
        path1.add(nodes.get("N" + selectedNode1.id.charAt(1) + "_" + selectedNode2.id.charAt(3)));
        path1.add(selectedNode2);
        calculatedPaths.add(path1);

        List<Node> path2 = new ArrayList<>();
        path2.add(selectedNode1);
        path2.add(nodes.get("N" + selectedNode2.id.charAt(1) + "_" + selectedNode1.id.charAt(3)));
        path2.add(selectedNode2);
        calculatedPaths.add(path2);

        if (calculatedPaths.size() > 5) {
            calculatedPaths = calculatedPaths.subList(0, 5);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 应用缩放和平移
        g2d.scale(scale, scale);
        g2d.translate(viewOffset.x, viewOffset.y);

        // 绘制边
        g2d.setColor(EDGE_COLOR);
        for (Edge edge : edges) {
            Line2D.Double line = new Line2D.Double(
                    edge.node1.x, edge.node1.y,
                    edge.node2.x, edge.node2.y
            );
            g2d.draw(line);

            // 绘制距离
            Point2D.Double midPoint = new Point2D.Double(
                    (edge.node1.x + edge.node2.x) / 2,
                    (edge.node1.y + edge.node2.y) / 2
            );
            g2d.drawString(String.valueOf(edge.distance), (float)midPoint.x, (float)midPoint.y);
        }

        // 绘制公交路线
        for (BusRoute route : busRoutes) {
            g2d.setColor(Color.ORANGE);
            for (int i = 0; i < route.stops.size() - 1; i++) {
                Node stop1 = route.stops.get(i);
                Node stop2 = route.stops.get(i + 1);

                Line2D.Double busLine = new Line2D.Double(
                        stop1.x, stop1.y,
                        stop2.x, stop2.y
                );
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(busLine);
            }
            g2d.setStroke(new BasicStroke(1));
        }

        // 绘制计算出的路径
        g2d.setColor(PATH_COLOR);
        g2d.setStroke(new BasicStroke(2));
        for (List<Node> path : calculatedPaths) {
            for (int i = 0; i < path.size() - 1; i++) {
                Node node1 = path.get(i);
                Node node2 = path.get(i + 1);

                Line2D.Double pathLine = new Line2D.Double(
                        node1.x, node1.y,
                        node2.x, node2.y
                );
                g2d.draw(pathLine);
            }
        }
        g2d.setStroke(new BasicStroke(1));

        // 绘制节点
        for (Node node : nodes.values()) {
            if (node.equals(selectedNode1)) {
                g2d.setColor(SELECTED_NODE_COLOR1);
            } else if (node.equals(selectedNode2)) {
                g2d.setColor(SELECTED_NODE_COLOR2);
            } else {
                g2d.setColor(NODE_COLOR);
            }

            Ellipse2D.Double circle = new Ellipse2D.Double(
                    node.x - NODE_RADIUS / 2,
                    node.y - NODE_RADIUS / 2,
                    NODE_RADIUS,
                    NODE_RADIUS
            );
            g2d.fill(circle);

            // 绘制节点ID
            g2d.setColor(Color.BLACK);
            g2d.drawString(node.id, (float)node.x + NODE_RADIUS, (float)node.y - NODE_RADIUS);
        }

        // 绘制公交站点
        g2d.setColor(Color.RED);
        for (BusRoute route : busRoutes) {
            for (Node stop : route.stops) {
                Rectangle2D.Double rect = new Rectangle2D.Double(
                        stop.x - NODE_RADIUS / 2,
                        stop.y - NODE_RADIUS / 2,
                        NODE_RADIUS,
                        NODE_RADIUS
                );
                g2d.fill(rect);
            }
        }
    }

    // 内部类
    public static class Node {
        String id;
        double x, y;

        Node(String id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    private static class Edge {
        Node node1, node2;
        int distance;

        Edge(Node node1, Node node2, int distance) {
            this.node1 = node1;
            this.node2 = node2;
            this.distance = distance;
        }
    }

    private static class BusRoute {
        String name;
        List<Node> stops;

        BusRoute(String name, List<Node> stops) {
            this.name = name;
            this.stops = stops;
        }
    }
}