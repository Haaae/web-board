package toy.board.dto.login.request;

import jakarta.validation.constraints.*;

public record JoinRequest(
        @Email
        @NotBlank
        @Max(value = 20)
        String username,

        @NotBlank
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다."
        )
        String password,

        @NotBlank
        @Size(min = 2, max = 8)
        String nickname) {
}
