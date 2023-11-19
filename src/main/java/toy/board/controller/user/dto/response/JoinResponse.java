package toy.board.controller.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import toy.board.domain.user.Member;

@Schema(description = "회원가입 응답 DTO")
public record JoinResponse(
        @Schema(description = "신규 회원 이메일")
        String username,
        @Schema(description = "신규 회원 닉네임")
        String nickname
) {
    public static JoinResponse of(Member member) {
        return new JoinResponse(
                member.getUsername(),
                member.getNickname()
        );
    }
}
