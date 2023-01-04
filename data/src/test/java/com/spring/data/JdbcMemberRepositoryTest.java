package com.spring.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spring.data.repository.JdbcMemberRepository;
import com.spring.data.repository.MemberRepository;

public class JdbcMemberRepositoryTest {

    Connection connection;
    MemberRepository memberRepository;

    @BeforeEach
    public void createConnection () throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test");
        connection.setAutoCommit(false);

        //memberRepository = new JdbcMemberRepository(connection);
    }

    @AfterEach
    public void closeConnection () throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    public void findNameById() {
        String name = memberRepository.findNameById(2L);
        assertThat(name).isEqualTo("Chun Woo-Hee");

        name = memberRepository.findNameById(20L);
        assertThat(name).isNull();
    }

    @Test
    public void findAllNames() {
        List<String> name = memberRepository.findAllNames();
        assertThat(name)
            .containsSequence("Lee Ji-Eun", "Chun Woo-Hee", "Kim Min-Jeong")
            .size()
            .isEqualTo(3);
    }

    @Test
    public void update() {
        String newName = "Park Bo-Young";
        long id = 3L;

        int changedRow = memberRepository.update(id, newName);

        assertThat(changedRow).isEqualTo(1);

        String name = memberRepository.findNameById(id);
        assertThat(name).isEqualTo(newName);
    }

    @Test
    public void save() {
        String name = "Park Bo-Young";

        Long generatedId = memberRepository.save(name);

        assertThat(generatedId).isSameAs(4L);

        String selectedName = memberRepository.findNameById(generatedId);
        assertThat(selectedName).isEqualTo(name);
    }

    @Test
    public void delete() {
        Long id = 3L;

        List<String> beforeList = memberRepository.findAllNames();

        int deletedRow = memberRepository.delete(id);
        assertThat(deletedRow).isSameAs(1);

        List<String> afterList = memberRepository.findAllNames();

        assertThat(afterList.size()).isSameAs(beforeList.size() - 1);

        String name = memberRepository.findNameById(id);
        assertThat(name).isNull();
    }

}
