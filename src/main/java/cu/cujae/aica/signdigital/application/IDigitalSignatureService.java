package cu.cujae.aica.signdigital.application;

import cu.cujae.aica.signdigital.dto.SignPDFIn;

public interface IDigitalSignatureService {

    Boolean signPdf(SignPDFIn signPDFIn);
}
