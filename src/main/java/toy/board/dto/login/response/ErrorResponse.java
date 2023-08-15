package toy.board.dto.login.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * 대상의 리소스
     * ex) email, password, 권한
     */
    private String resource;
    
    /**
     * 대상의 필드
     * ex) login, post
     */
    private String field;

    /**
     * 에러 코드
     */
    private String code;
}
