package toy.board.controller.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;

@Schema(description = "로그인 응답 DTO")
public record LoginResponse(
        @Schema(description = "로그인 사용자 Id")
        @Positive
        Long id,
        @Schema(description = "사용자 이메일")
        String username,
        @Schema(description = "사용자 닉네임")
        String nickname,
        @Schema(description = "사용자 역할")
        UserRole role
) {
    public static LoginResponse of(Member member) {
        return new LoginResponse(
                member.getId(),
                member.getUsername(),
                member.getNickname(),
                member.getRole()
        );
    }
}
