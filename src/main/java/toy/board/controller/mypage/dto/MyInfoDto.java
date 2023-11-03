package toy.board.controller.mypage.dto;

import toy.board.domain.user.Member;

public record MyInfoDto(
        String username,
        String nickname,
        long postCount,
        long commentCount
) {
    public static MyInfoDto of(Member member) {
        return new MyInfoDto(
                member.getUsername(),
                member.getNickname(),
                member.getPostCount(),
                member.getCommentCount()
        );
    }
}
