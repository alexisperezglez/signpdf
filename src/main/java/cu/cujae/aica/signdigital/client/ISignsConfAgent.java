package cu.cujae.aica.signdigital.client;

import cu.cujae.aica.signdigital.client.dto.SignsConfResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ISignsConfAgent {
    Mono<List<SignsConfResponse>> getSignsConf(String code);
}
