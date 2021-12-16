package cu.cujae.aica.signdigital.client;

import reactor.core.publisher.Mono;

import java.io.File;

public interface IClientAgent {
    Mono<Boolean> sendSignedPdf(File file);
}
