package cu.cujae.aica.signdigital.repositories;

import cu.cujae.aica.signdigital.repositories.dto.UserConfigDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConfigDB implements IConfigDb{
    private final JdbcTemplate jdbcTemplate;

    public ConfigDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    @Override
    public UserConfigDTO getConfigByUser(String username) {
        String query = "SELECT id, usuario as username, imagen as image FROM configuracion where usuario like ?";
        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserConfigDTO.class), username);
    }
}
