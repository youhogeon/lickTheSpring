package com.spring.data.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public class JdbcMemberRepository implements MemberRepository{

    private final DataSource datasource;

    public JdbcMemberRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public String findNameById(Long id) {
        try {
            Connection connection = datasource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT * from `members` WHERE id = ?");
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(2);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> findAllNames() {
        List<String> names = new ArrayList<>();

        try {
            Connection connection = datasource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("SELECT * from `members`");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                names.add(rs.getString(2));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names;
    }

    @Override
    public Long save(String name) {
        try {
            Connection connection = datasource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO `members` (`name`) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getLong(1);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int update(Long id, String name) {
        int changedRow = 0;

        try {
            Connection connection = datasource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("UPDATE `members` SET `name` = ? WHERE `id` = ?");
            pstmt.setString(1, name);
            pstmt.setLong(2, id);

            changedRow = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return changedRow;
    }

    @Override
    public int delete(Long id) {
        int changedRow = 0;

        try {
            Connection connection = datasource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM `members` WHERE `id` = ?");
            pstmt.setLong(1, id);

            changedRow = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return changedRow;
    }

}
