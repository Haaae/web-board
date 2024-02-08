package toy.board.controller.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.user.Member;

@Schema(description = "이메일 검증 요청 DTO")
public record EmailVerificationRequest(
        @Schema(description = "검증할 이메일")
        @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
        @NotBlank
        @Size(max = Member.USER_ID_LENGTH)
        String email,

        @Schema(description = "검증 코드")
        @Size(min = 6, max = 6, message = "이메일 인증번호는 6자리 숫자입니다.")
        String authCode
) {

}
