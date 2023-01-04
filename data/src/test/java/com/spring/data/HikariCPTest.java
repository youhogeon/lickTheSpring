package com.spring.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPTest {
    
    @Test
    public void performanceCheck_HikariCP() {

        HikariConfig config = new HikariConfig();
                         
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        config.setUsername("test");
        config.setPassword("test");

        DataSource dataSource = new HikariDataSource(config);

        long begin = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                Connection connection = null;

                while (true) {
                    try {
                        connection = dataSource.getConnection();
                        PreparedStatement pstmt = connection.prepareStatement("SELECT * from `members`");
                        ResultSet rs = pstmt.executeQuery();

                        break;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        System.out.println(System.currentTimeMillis() - begin);

        ((HikariDataSource)dataSource).close();

    }

    @Test
    public void performanceCheck_NoPool() {
        for (int i = 0; i < 5000; i++) {
            new Thread(() -> {
                Connection connection = null;

                while (true) {
                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test");
                        PreparedStatement pstmt = connection.prepareStatement("SELECT * from `members`");
                        ResultSet rs = pstmt.executeQuery();

                        break;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

}
