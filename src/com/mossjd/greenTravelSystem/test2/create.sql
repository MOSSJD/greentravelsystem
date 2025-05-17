-- 创建数据库
CREATE DATABASE IF NOT EXISTS GreenTravelSystem;
USE GreenTravelSystem;

-- 用户表
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    region VARCHAR(50),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 绿色出行线路表
CREATE TABLE travel_routes (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    route_type ENUM('公交', '地铁', '自行车道') NOT NULL,
    stations TEXT NOT NULL,
    operating_hours VARCHAR(100),
    region VARCHAR(50) NOT NULL
);

-- 共享交通工具表
CREATE TABLE shared_vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_type ENUM('共享单车', '共享电动车') NOT NULL,
    location VARCHAR(100) NOT NULL,
    available_count INT NOT NULL,
    region VARCHAR(50) NOT NULL,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 资讯表
CREATE TABLE news (
    news_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category ENUM('环保知识', '政策法规', '活动通知') NOT NULL
);

-- 出行记录表
CREATE TABLE travel_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    travel_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    travel_mode ENUM('公交', '地铁', '共享单车', '共享电动车', '步行') NOT NULL,
    start_location VARCHAR(100) NOT NULL,
    end_location VARCHAR(100) NOT NULL,
    distance_km DECIMAL(5,2),
    points_earned INT NOT NULL,
    carbon_reduction DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 积分表
CREATE TABLE user_points (
    user_id INT PRIMARY KEY,
    total_points INT NOT NULL DEFAULT 0,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 好友关系表
CREATE TABLE friendships (
    friendship_id INT AUTO_INCREMENT PRIMARY KEY,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    established_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(user_id),
    FOREIGN KEY (user2_id) REFERENCES users(user_id),
    UNIQUE KEY (user1_id, user2_id)
);

-- 动态表
CREATE TABLE posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    post_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 评论表
CREATE TABLE comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    comment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);