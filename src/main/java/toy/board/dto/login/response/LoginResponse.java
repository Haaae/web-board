package toy.board.dto.login.response;

import toy.board.entity.user.Member;

public record LoginResponse(
        Long id,
        String username,
        String nickname
) {
    public static LoginResponse of(Member member) {
        return new LoginResponse(member.getId(), member.getUsername(), member.getProfile().getNickname());
    }
}
