package cu.cujae.aica.signdigital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class GeneralResponse<T> {
    private T data;
}
