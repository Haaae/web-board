package toy.board.controller.user.dto.response;

import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;

public record LoginResponse(
        Long id,
        String username,
        String nickname,
        UserRole role
) {
    public static LoginResponse of(Member member) {
        return new LoginResponse(member.getId(), member.getUsername(), member.getProfile().getNickname(), member.getRole());
    }
}
