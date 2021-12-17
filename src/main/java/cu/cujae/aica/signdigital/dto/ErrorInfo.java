package cu.cujae.aica.signdigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ErrorInfo {
    private String message;
    private String code;
    private Integer statusCode;
    private String uriRequested;
}
