package toy.board.controller.user.dto.response;

import toy.board.domain.user.Member;

public record JoinResponse(
        String username,
        String nickname
) {
    public static JoinResponse of(Member member) {
        return new JoinResponse(member.getUsername(), member.getProfile().getNickname());
    }
}
