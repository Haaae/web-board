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

public class PostTest {

    @Nested
    class CreateTest {
        
        /*
        - 생성 성공
        - 생성 실패 : Member writer가 null일 때
        - 생성 실패 : 제목이 빈 문자열일 때
        - 생성 실패 : 제목이 길이 제한을 넘을 때
        - 생성 실패 : 본문이 빈 문자열일 때
        - 생성 실패 : 본문이 길이 제한을 넘을 때
         */

        @DisplayName("생성 실패 : Member writer가 null일 때")
        @Test
        void 작성자가_null이면_실패() throws Exception {
            //given
            String content = "content";
            String title = "title";

            //when
            Member writer = null;

            //then
            assertThatThrownBy(() -> new Post(
                    writer,
                    title,
                    content
            ))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : 제목이 빈 문자열일 때")
        @Test
        void 제목이_빈_문자열이면_실패() throws Exception {
            //given
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            String content = "content";

            //when
            String title = "";

            //then
            assertThatThrownBy(() -> new Post(
                    writer,
                    title,
                    content
            ))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : 제목이 길이 제한을 넘을 때")
        @Test
        void 제목이_길이제한을_넘기면_실패() throws Exception {
            //given
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            String content = "content";

            //when
            String title = String.format("%51s", "a").replace(" ", "a");

            //then
            assertThatThrownBy(() -> new Post(
                    writer,
                    title,
                    content
            ))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : 본문이 빈 문자열일 때")
        @Test
        void 본문이_빈_문자열이면_실패() throws Exception {
            //given
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            String title = "title";

            //when
            String content = "";

            //then
            assertThatThrownBy(() -> new Post(
                    writer,
                    title,
                    content
            ))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : 본문이 길이 제한을 넘을 때")
        @Test
        void 본문이_길이제한을_넘기면_실패() throws Exception {
            //given
            Member writer = MemberTest.create("username", "nickname", UserRole.USER);
            String title = "title";

            //when
            String content = String.format("%10001s", "a").replace(" ", "a");

            //then
            assertThatThrownBy(() -> new Post(
                    writer,
                    title,
                    content
            ))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class UpdateTest {
        
        /*
        - 사용자에 의한 게시물 수정 성공
        -
         */

        @DisplayName("수정 성공 : 사용자에 의한 게시물")
        @Test
        public void whenUpdatePostWithValidWriterId_thenSuccess() throws Exception {
            //given
            Post post = create("username", "emankcin");
            String newContent = "new";
            Member writer = post.getWriter();

            //when
            post.update(newContent, writer);

            //then
            assertThat(post.getContent()).isEqualTo(newContent);
        }

        @DisplayName("수정 성공 : 작성자가 아닌 사용자가 게시물을 수정하려 할 경우 예외 발생")
        @Test
        public void whenUpdatePostWithNotValidWriterId_thenThrowException() throws Exception {
            //given
            Post post = create("username", "emankcin");
            String newContent = "new";

            //when
            Member user = MemberTest.create("username1", "asdf", UserRole.USER);
            Member admin = MemberTest.create("username1", "asdf", UserRole.ADMIN);
            Member master = MemberTest.create("username1", "asdf", UserRole.MASTER);

            //then
            assertThrows(BusinessException.class,
                    () -> post.update(newContent, user),
                    ExceptionCode.INVALID_AUTHORITY.getDescription()
            );

            assertThrows(BusinessException.class,
                    () -> post.update(newContent, admin),
                    ExceptionCode.INVALID_AUTHORITY.getDescription()
            );

            assertThrows(BusinessException.class,
                    () -> post.update(newContent, master),
                    ExceptionCode.INVALID_AUTHORITY.getDescription()
            );
        }
    }

    @Nested
    class AuthorityVerificationTest {
        @DisplayName("권한 검증 테스트: Master, Admin 권한일 때 다른 사용자여도 권한 검증 성공")
        @Test
        public void 권한검증_권한있는_다른_사용자() throws Exception {
            //given
            Member writer = MemberTest.create(
                    "username",
                    "emankcin",
                    UserRole.USER
            );
            Post post = new Post(
                    writer,
                    "title1",
                    "content1"
            );
            //when
            Member master = MemberTest.create(
                    "Master",
                    "master",
                    UserRole.MASTER
            );
            Member admin = MemberTest.create(
                    "Master",
                    "master",
                    UserRole.ADMIN
            );
            //then
            assertDoesNotThrow(() -> post.validateRight(master));
            assertDoesNotThrow(() -> post.validateRight(admin));
        }

        @DisplayName("권한 검증 테스트: 작성자 검증 성공")
        @Test
        public void 권한검증_작성자() throws Exception {
            //given
            Member writer = MemberTest.create(
                    "username",
                    "emankcin",
                    UserRole.USER
            );
            Post post = new Post(
                    writer,
                    "title1",
                    "content1"
            );
            //when then
            assertDoesNotThrow(() -> post.validateRight(writer));
        }

        @DisplayName("권한 검증 테스트: 권한 없는 사용자 실패")
        @Test
        public void 권한검증__권한없는_사용자() throws Exception {
            //given
            Member writer = MemberTest.create("username", "emankcin", UserRole.USER);
            Post post = new Post(writer, "title1", "content1");
            //when
            Member user = MemberTest.create("user", "user", UserRole.USER);
            //then
            BusinessException e = assertThrows(BusinessException.class,
                    () -> post.validateRight(user));
            assertThat(e.getCode())
                    .isEqualTo(ExceptionCode.INVALID_AUTHORITY);
        }

        @DisplayName("권한 검증 테스트: 작성자 탈퇴한 게시물")
        @Test
        public void 게시물_테스트_작성자_탈퇴_후_권한없는_사용자() throws Exception {
            //given
            Member writer = MemberTest.create(
                    "username",
                    "emankcin",
                    UserRole.USER
            );
            Post post = new Post(
                    writer,
                    "title1",
                    "content1"
            );

            Member invalidMember = MemberTest.create(
                    "user",
                    "user",
                    UserRole.USER
            );

            Member master = MemberTest.create(
                    "Master",
                    "master",
                    UserRole.MASTER
            );
            Member admin = MemberTest.create(
                    "Master",
                    "master",
                    UserRole.ADMIN
            );

            //when
            post.applyWriterWithdrawal(writer);

            //then

            // 작성자에 의한 검증 실패
            BusinessException writerException = assertThrows(
                    BusinessException.class,
                    () -> post.validateRight(writer)
            );
            assertThat(writerException.getCode())
                    .isEqualTo(ExceptionCode.INVALID_AUTHORITY);

            // 일반 사용자 검증 실패
            BusinessException invalidMemberException = assertThrows(
                    BusinessException.class,
                    () -> post.validateRight(invalidMember)
            );
            assertThat(invalidMemberException.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);

            // admin, master 검증 성공
            assertDoesNotThrow(() -> post.validateRight(master));
            assertDoesNotThrow(() -> post.validateRight(admin));
        }
    }

    @Nested
    class FeatureTest {

        /*
        - 조회수 증가
        - 댓글 개수 : 삭제된 댓글 제외
         */

        @DisplayName("조회수 증가")
        @Test
        void 조회수_증가_정상동작() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Long hits = post.getHits();

            //when
            post.increaseHits();
            Long increasedHits = post.getHits();

            //then
            assertThat(hits).isEqualTo(0);
            assertThat(increasedHits).isEqualTo(1);
        }

        @DisplayName("댓글 개수 : 삭제된 댓글 제외하고 카운트")
        @Test
        void 댓글카운트시_삭제된_댓글_제외() throws Exception {
            //given
            Post post = PostTest.create("username", "nickname");
            Comment comment = CommentTest.create(post, CommentType.COMMENT);
            Comment deletedComment = CommentTest.create(post, CommentType.COMMENT);

            //when
            deletedComment.deleteBy(post.getWriter());

            //then
            assertThat(post.countComments()).isEqualTo(1);
        }
    }

    public static Post create(String username, String nickname) {
        return new Post(MemberTest.create(username, nickname, UserRole.USER), "title", "content");
    }

    public static Post create(Member member) {
        return new Post(member, "title", "content");
    }
}