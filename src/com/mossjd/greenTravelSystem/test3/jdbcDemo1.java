package com.mossjd.greenTravelSystem.test3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author MOSSJD
 * @create 2025-05-17-12:47
 */
public class jdbcDemo1 {
    public static void main(String[] args) throws
            ClassNotFoundException,
            SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/greentravelsystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root",
                "123456");
        String sql="update account set balance =500 where id=1";
        Statement statement =connection.createStatement();
        int count=statement.executeUpdate(sql);
        System.out.println(count);//
        statement.close();
        connection.close();
    }
}