package cu.cujae.aica.signdigital.client.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

@Getter
@Builder
public class SignedPdfRequest {
    private File file;
}
