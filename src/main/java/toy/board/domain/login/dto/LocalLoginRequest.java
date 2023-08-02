package toy.board.domain.login.dto;

import lombok.Data;

@Data
public class LocalLoginRequest {

    private String id;
    private String password;
}
