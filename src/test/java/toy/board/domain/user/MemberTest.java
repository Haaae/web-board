package toy.board.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentTest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.exception.BusinessException;

public class MemberTest {

    @Nested
    class CreateTest {

        /*
        - 생성 성공

        - 생성 실패 : username이 null일 때
        - 생성 실패 : username이 blank일 때
        - 생성 실패 : username이 길이제한을 초과할 때

        - 생성 실패 : nickname이 null일 때
        - 생성 실패 : nickname이 blank일 때
        - 생성 실패 : nickname이 길이제한을 초과할 때

        - 생성 실패 : passowrd가 null일 때
        - 생성 실패 : passowrd가 blank일 때
        - 생성 실패 : passowrd가 길이제한을 초과할 때

        - 생성 실패 : UserRole이 null일 때
         */

        @DisplayName("생성 성공")
        @Test
        void 생성_성공() throws Exception {
            //given
            String nickname = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;
            String username = "username";

            //when

            //then
            assertDoesNotThrow(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            );
        }

        @DisplayName("생성 실패 : username이 null일 때")
        @Test
        void username이_null이라면_실패() throws Exception {
            //given
            String nickname = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String username = null;

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : username이 blank일 때")
        @Test
        void username이_blank라면_실패() throws Exception {
            //given
            String nickname = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String username = "";

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : username이 길이제한을 초과할 때")
        @Test
        void username이_길이제한을_초과하면_실패() throws Exception {
            //given
            String nickname = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String username = String.format("%51s", "a").replace(" ", "a");

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : nickname이 null일 때")
        @Test
        void nickname이_null이라면_실패() throws Exception {
            //given
            String username = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String nickname = null;

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : nickname이 blank일 때")
        @Test
        void nickname가_blank라면_실패() throws Exception {
            //given
            String username = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String nickname = "";

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : nickname이 길이제한을 초과할 때")
        @Test
        void nickname이_길이제한을_초과하면_실패() throws Exception {
            //given
            String username = "username";
            String password = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String nickname = String.format("%9s", "a").replace(" ", "a");

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : password가 null일 때")
        @Test
        void password가_null이라면_실패() throws Exception {
            //given
            String username = "username";
            String nickname = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String password = null;

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : password가 blank일 때")
        @Test
        void password가_blank라면_실패() throws Exception {
            //given
            String username = "username";
            String nickname = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String password = "";

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : passowrd가 길이제한을 초과할 때")
        @Test
        void password가_길이제한을_초과하면_실패() throws Exception {
            //given
            String username = "username";
            String nickname = "nickname";
            UserRole userRole = UserRole.USER;

            //when
            String password = String.format("%61s", "a").replace(" ", "a");

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("생성 실패 : UserRole이 null일 때")
        @Test
        void UserRole이_null이면_실패() throws Exception {
            //given
            String username = "username";
            String nickname = "nickname";
            String password = "password";

            //when
            UserRole userRole = null;

            //then
            assertThatThrownBy(() ->
                    new Member(
                            username,
                            nickname,
                            password,
                            userRole
                    )
            )
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class UpdateTest {
        
        /*
        
        - 업데이트 성공
        - 업데이트 실패 : 업데이트 시키는 Member의 Role이 Master가 아닐 때
        - 업데이트 실패 : 업데이트 대상이 Master Role일 때
        
         */

        @DisplayName("업데이트 성공 : UserRole")
        @Test
        void UserRole_업데이트_성공() throws Exception {
            //given
            Member user = create("username", "nickname", UserRole.USER);
            Member admin = create("username", "nickname", UserRole.ADMIN);
            Member master = create("username", "nickname", UserRole.MASTER);

            //when
            //then

            // master가 user, admin을 업데이트 시 문제 x
            assertDoesNotThrow(() -> master.updateRole(user));
            assertDoesNotThrow(() -> master.updateRole(admin));
        }

        @DisplayName("업데이트 실패 : 업데이트 시키는 Member의 Role이 Master가 아닐 때")
        @Test
        void 업데이트_주체가_master가_아니면_실패() throws Exception {
            //given
            Member user = create("username", "nickname", UserRole.USER);
            Member admin = create("username", "nickname", UserRole.ADMIN);

            //when
            Member userNotMaster = create("username", "nickname", UserRole.USER);
            Member adminNotMaster = create("username", "nickname", UserRole.ADMIN);

            //then

            // user 역할 사용자일 때
            assertThatThrownBy(() -> userNotMaster.updateRole(user))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> userNotMaster.updateRole(admin))
                    .isInstanceOf(BusinessException.class);

            // admin 역할 사용자일 때
            assertThatThrownBy(() -> adminNotMaster.updateRole(user))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> adminNotMaster.updateRole(admin))
                    .isInstanceOf(BusinessException.class);
        }

        @DisplayName("업데이트 실패 : 업데이트 대상이 Master Role일 때")
        @Test
        void 업데이트_대상이_master면_실패() throws Exception {
            //given
            Member target = create("username", "nickname", UserRole.MASTER);

            //when
            Member master = create("username", "nickname", UserRole.MASTER);

            //then

            // user 역할 사용자일 때
            assertThatThrownBy(() -> master.updateRole(target))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    class FeatureTest {

        /*
        - Posts와 Comments의 작성자를 null로 변경 성공
         */

        @DisplayName("Posts와 Comments의 작성자를 null로 변경 성공")
        @Test
        void Posts와_Comments의_작성자를_null로_변경_성공() throws Exception {
            //given
            Member member = create("username", "nickname", UserRole.USER);

            Post post = PostTest.create(member);
            Comment comment = CommentTest.create(post, member, CommentType.COMMENT);

            //when
            member.changeAllPostAndCommentWriterToNull();

            //then
            assertThat(post.getWriter()).isNull();
            assertThat(comment.getWriter()).isNull();
        }

    }

    @Nested
    class AuthorityTest {
        
        /*
        - 유저 role에 따른 삭제 권한 검증
         */

        @DisplayName("유저 role에 따른 삭제 권한 검증")
        @Test
        void 삭제_권한_검증() throws Exception {
            //given
            Member user = create("username", "nickname", UserRole.USER);
            Member admin = create("username", "nickname", UserRole.ADMIN);
            Member master = create("username", "nickname", UserRole.MASTER);

            //when
            //then
            assertThat(user.hasDeleteRight()).isFalse();
            assertThat(admin.hasDeleteRight()).isTrue();
            assertThat(master.hasDeleteRight()).isTrue();

        }
    }

    public static Member create(String username, String nickname, UserRole role) {
        return new Member(
                username,
                nickname,
                "password",
                role
        );
    }

    public static Member create() {
        return MemberTest.create("username", "nickname", UserRole.USER);
    }
}