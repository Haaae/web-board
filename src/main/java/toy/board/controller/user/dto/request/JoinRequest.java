package toy.board.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import toy.board.domain.user.Member;
import toy.board.domain.user.Profile;

@Schema(description = "회원가입 요청 DTO")
public record JoinRequest(
        @Schema(description = "이메일")
        @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
        @NotBlank
        @Size(max = Member.USER_ID_LENGTH)
        String username,

        @Schema(description = "비밀번호")
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@!%*#?&])[A-Za-z\\d@!%*#?&]{8,20}$",
                message = "비밀번호는 영문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다."
        )
        String password,

        @Schema(description = "비밀번호")
        @NotBlank
        @Size(min = 2, max = Profile.NICKNAME_LENGTH)
        String nickname) {
}
