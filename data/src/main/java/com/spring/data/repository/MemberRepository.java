package com.spring.data.repository;

import java.util.List;

public interface MemberRepository {

    public String findNameById(Long id);

    public List<String> findAllNames();

    public Long save(String name);

    public int update(Long id, String name);

    public int delete(Long id);

}
