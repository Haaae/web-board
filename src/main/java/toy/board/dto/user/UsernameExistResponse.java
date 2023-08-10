package toy.board.dto.user;

import toy.board.entity.user.Member;

public record UsernameExistResponse(
        String username,
        String nickname
) {
    public static UsernameExistResponse of(Member member) {
        return new UsernameExistResponse(member.getUsername(), member.getProfile().getNickname());
    }
}
