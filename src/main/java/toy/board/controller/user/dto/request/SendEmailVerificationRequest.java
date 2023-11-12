package toy.board.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import toy.board.domain.user.Member;

@Schema(description = "이메일 검증 코드 발송 요청 DTO")
public record SendEmailVerificationRequest(
        @Schema(description = "검증할 이메일")
        @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
        @NotBlank
        @Size(max = Member.USER_ID_LENGTH)
        String email
) {

}
