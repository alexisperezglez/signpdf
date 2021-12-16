package cu.cujae.aica.signdigital.repositories.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserConfigDTO {
    private Long id;
    private String username;
    private String image;
}
