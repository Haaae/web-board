package toy.board.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestResponse {
    private boolean success;
    private String message;
    private Object object;

}
