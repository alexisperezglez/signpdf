package cu.cujae.aica.signdigital.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StepFormsResponse {
    @JsonProperty("estado")
    private String status;
    @JsonProperty("orden")
    private Integer order;
    @JsonProperty("trabajadores")
    private List<WorkersResponse> workersResponses;
}
