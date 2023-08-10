package toy.board.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class RestResponse {
    private boolean success;
    private String message;
    private Object object;

    public static ResponseEntity<RestResponse> createWithResponseEntity(HttpStatus status, boolean success, String message, Object object) {
        return ResponseEntity
                .status(status)
                .body(new RestResponse(success, message, object));
    }
}
