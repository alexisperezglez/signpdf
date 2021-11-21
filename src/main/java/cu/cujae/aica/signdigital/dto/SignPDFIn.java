package cu.cujae.aica.signdigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignPDFIn {
    @NotEmpty(message = "error.etapaRequerida")
    @NotBlank(message = "error.etapaRequerida")
    @NotNull(message = "error.etapaRequerida")
    private String stage;
    @NotEmpty(message = "error.claveRequerida")
    @NotBlank(message = "error.claveRequerida")
    @NotNull(message = "error.claveRequerida")
    private String password;
    @NotEmpty(message = "error.certificadoRequerido")
    @NotBlank(message = "error.certificadoRequerido")
    @NotNull(message = "error.certificadoRequerido")
    private String privateKey;
    @NotEmpty(message = "error.pdfRequerido")
    @NotBlank(message = "error.pdfRequerido")
    @NotNull(message = "error.pdfRequerido")
    private String file;
    @NotEmpty(message = "error.imageRequerido")
    @NotBlank(message = "error.imageRequerido")
    @NotNull(message = "error.imageRequerido")
    private String image;
}
