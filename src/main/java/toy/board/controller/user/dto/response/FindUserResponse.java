package toy.board.controller.user.dto.response;

import toy.board.domain.user.Member;

public record FindUserResponse(
        String username,
        String nickname
) {
    public static FindUserResponse of(Member member) {
        return new FindUserResponse(member.getUsername(), member.getProfile().getNickname());
    }
}
