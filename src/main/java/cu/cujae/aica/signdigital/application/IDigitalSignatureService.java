package cu.cujae.aica.signdigital.application;

import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import reactor.core.publisher.Mono;

public interface IDigitalSignatureService {

    Mono<Boolean> signPdf(SignPDFIn signPDFIn);
    Boolean saveConfig(DigitalSignConfIn digitalSignConfIn);
}
