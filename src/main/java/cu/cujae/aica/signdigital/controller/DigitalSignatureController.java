package cu.cujae.aica.signdigital.controller;

import cu.cujae.aica.signdigital.application.IDigitalSignatureService;
import cu.cujae.aica.signdigital.dto.DigitalSignConfIn;
import cu.cujae.aica.signdigital.dto.GeneralRequest;
import cu.cujae.aica.signdigital.dto.GeneralResponse;
import cu.cujae.aica.signdigital.dto.SignPDFIn;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign")

public class DigitalSignatureController implements IDigitalSignatureController {

    private final IDigitalSignatureService digitalSignatureService;

    public DigitalSignatureController(IDigitalSignatureService digitalSignatureService) {
        this.digitalSignatureService = digitalSignatureService;
    }

    @Override
    @Operation(summary = "Firmar digitalmente un pdf", description = "Documento PDF, certificado y password requeridos para ejecutar la firma")
    @PostMapping("/v1/sign")
    public ResponseEntity<GeneralResponse<Boolean>> signPDF(@RequestBody GeneralRequest<SignPDFIn> request) {
        GeneralResponse<Boolean> response = new GeneralResponse<>();
        Boolean result = this.digitalSignatureService.signPdf(request.getParams()).block();
        response.setData(result);
        return ResponseEntity.ok(response);
    }

    @Override
    @Operation(summary = "Almacenar configuracion de firma por usuario")
    @PostMapping("/v1/save")
    public ResponseEntity<GeneralResponse<Boolean>> saveConfig(@RequestBody GeneralRequest<DigitalSignConfIn> request) {
        GeneralResponse<Boolean> response = new GeneralResponse<>();
        Boolean result = this.digitalSignatureService.saveConfig(request.getParams());
        response.setData(result);
        return ResponseEntity.ok(response);
    }
}
