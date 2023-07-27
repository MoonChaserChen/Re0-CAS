package ink.akira.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

public class JdbcService {
    public static final Logger LOGGER = LoggerFactory.getLogger(JdbcService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSource dataSource;

    public static final String QUERY_USERNAME_BY_EMAIL = "select user_name from user_base where email = ?";

    public JdbcService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
    }

    public String queryUsernameByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(QUERY_USERNAME_BY_EMAIL, String.class, email);
        } catch (DataAccessException e) {
            LOGGER.error("queryUsernameByEmail failed.", e);
            return null;
        }
    }
}
