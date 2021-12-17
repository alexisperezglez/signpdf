package cu.cujae.aica.signdigital.client.impl;

import cu.cujae.aica.signdigital.client.ISignsConfAgent;
import cu.cujae.aica.signdigital.client.dto.SignsConfResponse;
import cu.cujae.aica.signdigital.cross.Setting;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
public class SignsConfAgent implements ISignsConfAgent {
    private final Setting setting;

    public SignsConfAgent(Setting setting) {
        this.setting = setting;
    }

    @Override
    public Mono<List<SignsConfResponse>> getSignsConf(String code) {
        return WebClient.create(setting.getSignsBase())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/" + code).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SignsConfResponse>>() {})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                        .filter(throwable -> (
                                throwable instanceof WebClientResponseException && ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()
                        ))
                )
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable);
                });
    }
}
