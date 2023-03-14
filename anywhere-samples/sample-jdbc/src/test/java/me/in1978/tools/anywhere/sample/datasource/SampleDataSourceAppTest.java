package me.in1978.tools.anywhere.sample.datasource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
class SampleDataSourceAppTest {

    @Resource
    JdbcTemplate tpl;

    @Test
    public void test1() {
        System.out.println(tpl.queryForObject("select now()", Object.class));
    }

}