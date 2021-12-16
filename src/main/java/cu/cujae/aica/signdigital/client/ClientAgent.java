package cu.cujae.aica.signdigital.client;

import cu.cujae.aica.signdigital.cross.Setting;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
public class ClientAgent implements IClientAgent{
    private final Setting setting;

    public ClientAgent(Setting setting) {
        this.setting = setting;
    }

    public Mono<Boolean> sendSignedPdf(File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);
        builder.part("name", "formulario");
        return WebClient.create(setting.getClientBase())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/document").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(Boolean.TRUE);
                    }
                    throw new RuntimeException("Error uploading file");
                })
                .doOnError(throwable -> Mono.just(Boolean.FALSE));
    }
}
