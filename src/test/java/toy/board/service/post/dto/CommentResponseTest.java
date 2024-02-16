package toy.board.service.post.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentTest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;

class CommentResponseTest {

    @DisplayName("CommentResponseDto 생성 성공시 Comment 객체의 정보와 일치")
    @Test
    void 생성_성공시_Comment_객체의_정보와_일치() throws Exception {
        //given
        Member member = MemberTest.create();
        Post post = PostTest.create(member);
        Comment comment = CommentTest.create(post, CommentType.COMMENT);

        int countOfReply = 2;
        for (int i = 0; i < countOfReply; i++) {
            CommentTest.create(post, CommentType.REPLY, comment);
        }

        //when
        CommentResponse commentResponse = CommentResponse.createCommentTypeFrom(comment);

        //then
        assertThat(commentResponse.isCommentType())
                .isTrue();

        assertThat(commentResponse.commentId())
                .isEqualTo(comment.getId());

        assertThat(commentResponse.writerId())
                .isEqualTo(member.getId());

        assertThat(commentResponse.writer())
                .isEqualTo(member.getNickname());

        assertThat(commentResponse.content())
                .isEqualTo(post.getContent());

        assertThat(commentResponse.type())
                .isEqualTo(comment.getType());

        assertThat(commentResponse.isDeleted())
                .isEqualTo(comment.isDeleted());

        assertThat(commentResponse.isEdited())
                .isEqualTo(comment.isEdited());

        assertThat(commentResponse.createdDate())
                .isEqualTo(comment.getCreatedDate());

        assertThat(commentResponse.replies().count())
                .isEqualTo(countOfReply);
    }

    @DisplayName("CommentResponseDto 타입 체크 성공 : Comment Type인 경우")
    @Test
    void CommentResponseDto_Comment_타입_체크_성공() throws Exception {
        //given
        Comment comment = createComment();
        CommentResponse commentResponse = CommentResponse.createCommentTypeFrom(comment);

        //when
        boolean isCommentType = commentResponse.isCommentType();

        //then
        assertThat(isCommentType).isTrue();
    }

    @DisplayName("CommentResponseDto 타입 체크 성공 : Reply Type인 경우")
    @Test
    void CommentResponseDto_Reply_타입_체크_성공() throws Exception {
        //given
        Comment comment = createReply();
        CommentResponse commentResponse = CommentResponse.createCommentTypeFrom(comment);

        //when
        boolean isCommentType = commentResponse.isCommentType();

        //then
        assertThat(isCommentType).isFalse();
    }

    private Comment createComment() {
        Post post = PostTest.create(
                MemberTest.create()
        );
        return CommentTest.create(post, CommentType.COMMENT);
    }

    private Comment createReply() {
        Comment comment = createComment();
        return CommentTest.create(comment.getPost(), CommentType.REPLY, comment);
    }
}