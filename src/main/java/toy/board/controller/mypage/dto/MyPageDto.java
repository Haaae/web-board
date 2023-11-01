package toy.board.controller.mypage.dto;

import toy.board.domain.user.Member;

public record MyPageDto(
        String username,
        String nickname,
        long postCount,
        long commentCount
) {
    public static MyPageDto of(Member member) {
        return new MyPageDto(
                member.getUsername(),
                member.getNickname(),
                member.getPostCount(),
                member.getCommentCount()
        );
    }
}
