package toy.board.controller.user.dto.response;

import toy.board.domain.user.Member;

public record LoginResponse(
        Long id,
        String username,
        String nickname
) {
    public static LoginResponse of(Member member) {
        return new LoginResponse(member.getId(), member.getUsername(), member.getProfile().getNickname());
    }
}
