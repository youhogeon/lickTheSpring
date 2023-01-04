package com.spring.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        try (
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test");
            PreparedStatement pstmt = connection.prepareStatement("SELECT * from `members`");
            ResultSet rs = pstmt.executeQuery();
        ){
            while (rs.next()) {
                System.out.println(rs.getString(1) + " : " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}