package toy.board.dto.login;

import toy.board.entity.user.Member;

public record JoinResponse(
        String username,
        String nickname
) {
    public static JoinResponse of(Member member) {
        return new JoinResponse(member.getUsername(), member.getProfile().getNickname());
    }
}
