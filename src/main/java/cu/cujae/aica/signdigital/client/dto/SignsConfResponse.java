package cu.cujae.aica.signdigital.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SignsConfResponse {
    private List<StepFormsResponse> stepFormsResponses;
}
