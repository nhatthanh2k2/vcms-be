package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashMap;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code = 1000;
    private String message;
    private boolean success;
    private T result = (T) new HashMap<>();
}

