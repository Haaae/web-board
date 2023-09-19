package toy.board.controller.user.dto.request;

import jakarta.validation.constraints.*;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;

public record JoinRequest(
        @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
        @NotBlank
        @Size(max = Member.USER_ID_LENGTH)
        String username,

        @NotBlank
        @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@!%*#?&])[A-Za-z\\d@!%*#?&]{8,20}$",
                message = "비밀번호는 영문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다."
        )
        String password,

        @NotBlank
        @Size(min = 2, max = Profile.NICKNAME_LENGTH)
        String nickname) {
}
