package toy.board.dto.user;

import toy.board.entity.user.Member;

public record FindUserResponse(
        String username,
        String nickname
) {
    public static FindUserResponse of(Member member) {
        return new FindUserResponse(member.getUsername(), member.getProfile().getNickname());
    }
}
