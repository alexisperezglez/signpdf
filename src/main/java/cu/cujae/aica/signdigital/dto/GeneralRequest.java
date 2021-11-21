package cu.cujae.aica.signdigital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneralRequest<T> {
    @JsonProperty
//    @Valid
    private T params;

    public GeneralRequest() {
    }

    public T getParams() {
        return this.params;
    }

    @JsonProperty
    public void setParams(final T params) {
        this.params = params;
    }
}
