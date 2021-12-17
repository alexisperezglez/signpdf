package cu.cujae.aica.signdigital.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkersResponse {
    private String area;
    @JsonProperty("cargo")
    private String charge;
    private String ci;
    @JsonProperty("nombre")
    private String fullName;
}
