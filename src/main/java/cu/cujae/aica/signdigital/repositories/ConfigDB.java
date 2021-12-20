package cu.cujae.aica.signdigital.repositories;

import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.repositories.dto.UserConfigDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.Optional;

@Component
public class ConfigDB implements IConfigDb {
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

    @Override
    public Boolean saveConfig(DigitalSignConfIn digitalSignConfIn) {
        String query =
                "INSERT INTO configuracion(id, usuario, imagen, create_at, updtaed_at)" +
                        " VALUES (default, ?, ?, now(), now()) " +
                        "ON CONFLICT (usuario) DO UPDATE SET imagen = ?, updtaed_at = now();";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[] { "id" });
            ps.setObject(1, digitalSignConfIn.getUsername());
            ps.setObject(2, digitalSignConfIn.getImage());
            ps.setObject(3, digitalSignConfIn.getImage());
            return ps;
        }, keyHolder);
        return Optional.ofNullable(keyHolder.getKeys()).isPresent();
    }
}
