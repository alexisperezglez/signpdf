package cu.cujae.aica.signdigital.repositories;

import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.repositories.dto.UserConfigDTO;

public interface IConfigDb {
    UserConfigDTO getConfigByUser(String username);
    Boolean saveConfig(DigitalSignConfIn digitalSignConfIn);
}
