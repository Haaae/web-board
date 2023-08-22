package toy.board.dto.login.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailVerificationRequest(
        @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
        @NotBlank
        @Size(max = 50)
        String email,

        @Size(min = 6, max = 6, message = "이메일 인증번호는 6자리 숫자입니다.")
        String authCode
) {

}
