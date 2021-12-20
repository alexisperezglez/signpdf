package cu.cujae.aica.signdigital.controller;

import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.dto.GeneralRequest;
import cu.cujae.aica.signdigital.dto.GeneralResponse;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IDigitalSignatureController {
    ResponseEntity<GeneralResponse<Boolean>> signPDF(@RequestBody GeneralRequest<SignPDFIn> request);
    ResponseEntity<GeneralResponse<Boolean>> saveConfig(@RequestBody GeneralRequest<DigitalSignConfIn> request);
}
