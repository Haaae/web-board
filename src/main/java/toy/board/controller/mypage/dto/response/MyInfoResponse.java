package toy.board.controller.mypage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import toy.board.domain.user.Member;

@Schema(description = "내 정보 DTO")
public record MyInfoResponse(
        @Schema(description = "사용자 아이디", example = "google@gmail.com")
        String username,
        @Schema(description = "사용자 닉네임", example = "ImUser")
        String nickname,
        @Schema(description = "사용자가 작성한 게시물 개수", example = "10")
        long postCount,
        @Schema(description = "사용자가 작성한 댓글 개수", example = "10")
        long commentCount
) {

    public static MyInfoResponse of(Member member) {
        return new MyInfoResponse(
                member.getUsername(),
                member.getNickname(),
                member.countPosts(),
                member.countComments()
        );
    }
}
