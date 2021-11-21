package cu.cujae.aica.signdigital.controller;

import cu.cujae.aica.signdigital.application.IDigitalSignatureService;
import cu.cujae.aica.signdigital.dto.GeneralRequest;
import cu.cujae.aica.signdigital.dto.GeneralResponse;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign")
public class DigitalSignatureController {

    private final IDigitalSignatureService digitalSignatureService;

    public DigitalSignatureController(IDigitalSignatureService digitalSignatureService) {
        this.digitalSignatureService = digitalSignatureService;
    }

    @PostMapping("/v1/sign")
    public ResponseEntity<GeneralResponse<Boolean>> signPDF(@RequestBody GeneralRequest<SignPDFIn> request) {
        GeneralResponse<Boolean> response = new GeneralResponse<>();
        Boolean result = this.digitalSignatureService.signPdf(request.getParams());
        response.setData(result);
        return ResponseEntity.ok(response);
    }
}
