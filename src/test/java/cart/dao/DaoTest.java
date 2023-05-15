package cart.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test.sql")
@JdbcTest
public class DaoTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
}
