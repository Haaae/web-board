package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

public class CommentTest {

    @Nested
    class CreateTest {

        /*
        - 생성 성공
        - 생성 실패 : Post가 null인 경우
        - 생성 실패 : Writer가 null인 경우
        - 생성 실패 : CommentType이 null인 경우
        - 생성 실패 : Content가 빈 경우
        - 생성 실패 : Content가 길이 제한을 초과한 경우
        
        - 생성 실패 : 유효하지 않은 댓글 타입
        - 생성 실패 : 답글 생성시 답글과 부모 댓글의 게시물이 다른 경우
         */

        @DisplayName("생성 실패 : Writer가 null인 경우")
        @Test
        void Writer가_null이면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            //when
            Member member = null;

            //then
            assertThatThrownBy(() ->
                    new Comment(
                            post,
                            member,
                            "content",
                            CommentType.COMMENT,
                            null
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : Post가 null인 경우")
        @Test
        void Post가_null이면_실패() throws Exception {
            //given
            Member member = MemberTest.create("username", "nickname", UserRole.USER);

            //when
            Post post = null;

            //then
            assertThatThrownBy(() ->
                    new Comment(
                            post,
                            member,
                            "content",
                            CommentType.COMMENT,
                            null
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : CommentType이 null인 경우")
        @Test
        void CommentType이_null이면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");

            //when
            CommentType type = null;

            //then
            assertThatThrownBy(() ->
                    new Comment(
                            post,
                            post.getWriter(),
                            "content",
                            type,
                            null
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : Content가 빈 경우")
        @Test
        void 본문이_빈문자열이면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");

            //when
            String content = "";
            //then
            assertThatThrownBy(() ->
                    new Comment(
                            post,
                            post.getWriter(),
                            content,
                            CommentType.COMMENT,
                            null
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : Content가 길이 제한을 초과한 경우")
        @Test
        void 본문길이가_제한을_초과하면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");

            //when
            String content =
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                            + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

            //then
            assertThatThrownBy(() ->
                    new Comment(
                            post,
                            post.getWriter(),
                            content,
                            CommentType.COMMENT,
                            null
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : 유효하지 않은 댓글 타입")
        @Test
        public void 유효하지_않은_댓글타입이면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "emankcin");
            String content = "content";

            CommentType commentType = CommentType.COMMENT;
            CommentType replyType = CommentType.REPLY;
            Comment parent = new Comment(post, post.getWriter(), content, commentType, null);
            Comment reply = new Comment(post, post.getWriter(), content, replyType, parent);

            //when
            //then

            // 댓글 생성 시 부모가 null이 아닐 경우
            BusinessException e1 = assertThrows(
                    BusinessException.class,
                    () -> new Comment(post, post.getWriter(), content, commentType, reply)
            );
            assertThat(e1.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);

            // 답글 생성 시 부모가 null일 경우
            BusinessException e2 = assertThrows(
                    BusinessException.class,
                    () -> new Comment(post, post.getWriter(), content, replyType, null)
            );
            assertThat(e2.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);

            // 답글 생성 시 부모가 답글일 경우
            BusinessException e3 = assertThrows(
                    BusinessException.class,
                    () -> new Comment(post, post.getWriter(), content, replyType, reply)
            );
            assertThat(e3.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_COMMENT_TYPE);
        }

        @DisplayName("생성 실패 : 답글 생성시 답글과 부모 댓글의 게시물이 다른 경우")
        @Test
        public void 답글과_부모댓글의_게시물이_다르면_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "emankcin");
            String content = "content";

            CommentType commentType = CommentType.COMMENT;
            CommentType replyType = CommentType.REPLY;
            Comment parent = new Comment(post, post.getWriter(), content, commentType, null);

            //when
            Post otherPost = PostTest.create("invalid", "invalid");

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> new Comment(
                            otherPost,
                            otherPost.getWriter(),
                            content,
                            replyType,
                            parent
                    )
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.BAD_REQUEST_POST_OF_COMMENT);
        }
    }

    @Nested
    class UpdateTest {

        /*
        - 업데이트 성공
        - 업데이트 실패 : 빈 문자열
        - 업데이트 실패 : 권한 없음 - 작성자 외 모든 사용자
         */

        @DisplayName("업데이트 성공")
        @Test
        void 업데이트_성공() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            String newContent = "new content";

            //then
            assertDoesNotThrow(() -> comment.update(newContent, writer));
        }

        @DisplayName("업데이트 실패 : 빈 문자열")
        @Test
        void 빈_문자열이면_없데이트_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            String emptyContent = "";

            //then
            assertThatThrownBy(() -> comment.update(emptyContent, writer))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("업데이트 실패 : 권한 없음 - 작성자 외 모든 유형의 UserType")
        @Test
        void 작성자가_아니면_업데이트_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            String newContent = "new content";

            Member nullMember = null;
            Member master = MemberTest.create("username", "nickname", UserRole.MASTER);
            Member admin = MemberTest.create("username", "nickname", UserRole.ADMIN);

            //then
            assertThatThrownBy(() -> comment.update(newContent, nullMember))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> comment.update(newContent, admin))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> comment.update(newContent, master))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class DeleteTest {

        /*
        - 삭제 성공 : 작성자
        - 삭제 성공 : 작성자가 아닌 삭제 권한 있는 사용자
        - 삭제 실패 : Member Null
        - 삭제 실패 : 권한 없음 - 작성자 아닌 일반 사용자
        - 삭제 실패 : 탈퇴한 회원의 댓글을 권한 없는 일반 사용자가 삭제 시도
         */

        @DisplayName("삭제 성공 : 작성자")
        @Test
        void 작성자라면_삭제_성공() throws Exception {
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when

            //then
            assertDoesNotThrow(() -> comment.deleteBy(writer));
            assertThat(comment.isDeleted()).isTrue();
        }

        @DisplayName("삭제 성공 : 작성자가 아닌 삭제 권한 있는 사용자 - master")
        @Test
        void master_사용자라면_삭제_성공() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            Member master = MemberTest.create("username", "nickname", UserRole.MASTER);

            //then
            assertDoesNotThrow(() -> comment.deleteBy(master));
            assertThat(comment.isDeleted()).isTrue();
        }

        @DisplayName("삭제 성공 : 작성자가 아닌 삭제 권한 있는 사용자 - admin")
        @Test
        void admin_사용자라면_삭제_성공() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            Member admin = MemberTest.create("username", "nickname", UserRole.ADMIN);

            //then
            assertDoesNotThrow(() -> comment.deleteBy(admin));
            assertThat(comment.isDeleted()).isTrue();
        }

        @DisplayName("삭제 실패 : Member Null")
        @Test
        void 사용자가_null이면_삭제_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            //then
            assertThatThrownBy(() -> comment.deleteBy(null))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("삭제 실패 : 권한 없음 - 작성자 아닌 일반 사용자")
        @Test
        void 삭제권한없는_일반사용자라면_삭제_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            Member other = MemberTest.create("username", "other", UserRole.USER);

            //then
            assertThatThrownBy(() -> comment.deleteBy(other))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("삭제 실패 : 탈퇴한 회원의 댓글을 권한 없는 일반 사용자가 삭제 시도할 때 NPE이 발생하지 않고 비즈니스 예외가 발생한다")
        @Test
        void 탈퇴회원의_댓글을_삭제권한없는_일반사용자라면_삭제_실패() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            comment.applyWriterWithdrawal(writer);
            Member other = MemberTest.create("username", "other", UserRole.USER);

            //then
            assertThatThrownBy(() -> comment.deleteBy(other))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class WithdrawalTest {
        /*
        - 탈퇴 적용 성공
        - 탈퇴 적용 실패 : 작성자가 아닌 사용자 - 모든 UserRole
        - 탈퇴 적용 실패 : 이미 탈퇴 적용된 comment
         */

        @DisplayName("탈퇴 적용 성공 : 작성자")
        @Test
        void 작성자라면_탈퇴적용_성공() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when

            //then
            assertDoesNotThrow(() -> comment.applyWriterWithdrawal(writer));

            assertThat(comment.getWriter())
                    .isNull();
        }

        @DisplayName("탈퇴 적용 실패 : 작성자가 아닌 사용자 - 모든 UserRole")
        @Test
        void 작성자가_아니라면_탈퇴적용_실퍂() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            Member nullMember = null;
            Member master = MemberTest.create("username", "nickname", UserRole.MASTER);
            Member admin = MemberTest.create("username", "nickname", UserRole.ADMIN);

            //then
            assertThatThrownBy(() -> comment.applyWriterWithdrawal(nullMember))
                    .isInstanceOf(BusinessException.class);
            assertThat(comment.getWriter())
                    .isNotNull();

            assertThatThrownBy(() -> comment.applyWriterWithdrawal(master))
                    .isInstanceOf(BusinessException.class);
            assertThat(comment.getWriter())
                    .isNotNull();

            assertThatThrownBy(() -> comment.applyWriterWithdrawal(admin))
                    .isInstanceOf(BusinessException.class);
            assertThat(comment.getWriter())
                    .isNotNull();
        }

        @DisplayName("탈퇴 적용 실패 : 이미 탈퇴 적용된 comment. NPE 대신 비즈니스 예외가 발생한다")
        @Test
        void 이미_탈퇴적용된_댓글은_탈퇴적용_실퍂() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            comment.applyWriterWithdrawal(writer);

            Member nullMember = null;
            Member master = MemberTest.create("username", "nickname", UserRole.MASTER);
            Member admin = MemberTest.create("username", "nickname", UserRole.ADMIN);

            //then
            assertThat(comment.getWriter())
                    .isNull();

            assertThatThrownBy(() -> comment.applyWriterWithdrawal(nullMember))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> comment.applyWriterWithdrawal(master))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> comment.applyWriterWithdrawal(admin))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class BothDirectionsTest {
        /*
        - Post에 대한 양방향 매핑
        - Member에 대한 양방향 매핑
        - reply 생성 시 parent.replies에 소속 및 parent는 reply.parent에 매핑 [x]
         */

        @DisplayName("Post에 대한 양방향 매핑")
        @Test
        void Post에_대한_양방향_매핑() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            //then
            assertThat(post.getComments()).contains(comment);
            assertThat(comment.getPost()).isEqualTo(post);
        }

        @DisplayName("Member에 대한 양방향 매핑")
        @Test
        void Member에_대한_양방향_매핑() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            Comment comment = CommentTest.create(post, writer, CommentType.COMMENT);

            //when
            //then
            assertThat(writer.getComments()).contains(comment);
            assertThat(comment.getWriter()).isEqualTo(writer);
        }

        @DisplayName("부모 댓글에 대한 양방향 매핑: reply 생성 시 reply가 parent.replies에 소속 및 parent는 reply.parent에 매핑")
        @Test
        public void 부모_댓글에_대한_양방향매핑() throws Exception {
            //given
            Post post = PostTest.create("username", "emankcin");
            String content = "content";

            //when
            CommentType commentType = CommentType.COMMENT;
            CommentType replyType = CommentType.REPLY;

            //then
            Comment parent = new Comment(post, post.getWriter(), content, commentType, null);
            Comment reply = new Comment(post, post.getWriter(), content, replyType, parent);

            assertThat(
                    parent.getReplies()
                            .contains(reply)
            ).isTrue();

            assertThat(
                    reply.getParent()
                            .equals(parent)
            ).isTrue();
        }
    }

    public static Comment create(Post post, CommentType commentType) {
        return create(
                post,
                post.getWriter(),
                commentType
        );
    }

    public static Comment create(Post post, Member writer, CommentType commentType) {
        return new Comment(
                post,
                writer,
                "content",
                commentType,
                null
        );
    }
}