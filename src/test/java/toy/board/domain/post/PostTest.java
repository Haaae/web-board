package toy.board.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@SpringBootTest
@Transactional
public class PostTest {

    @Autowired
    private EntityManager em;

    @DisplayName("수정 삭제 등 권한 검증 테스트: Master, Admin 권한일 때 다른 사용자여도 권한 검증 성공")
    @Test
    public void 게시물_테스트_권한있는_다른_사용자() throws Exception {
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

    @DisplayName("수정 삭제 등 권한 검증 테스트: 작성자 검증 성공")
    @Test
    public void 게시물_테스트_작성자() throws Exception {
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

    @DisplayName("수정 삭제 등 권한 검증 테스트: 권한 없는 사용자 실패")
    @Test
    public void 게시물_테스트_권한없는_사용자() throws Exception {
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

    @DisplayName("수정 삭제 등 권한 검증 테스트: 작성자 탈퇴한 게시물")
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
        post.applyWriterWithdrawal();
        //then
        BusinessException writerException = assertThrows(
                BusinessException.class,
                () -> post.validateRight(writer)
        );
        assertThat(writerException.getCode())
                .isEqualTo(ExceptionCode.INVALID_AUTHORITY);

        BusinessException invalidMemberException = assertThrows(
                BusinessException.class,
                () -> post.validateRight(invalidMember)
        );
        assertThat(invalidMemberException.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);

        assertDoesNotThrow(() -> post.validateRight(master));
        assertDoesNotThrow(() -> post.validateRight(admin));
    }

    @DisplayName("권한 없는 사용자가 게시물을 수정하려 할 경우 예외 발생")
    @Test
    public void whenUpdatePostWithNotValidWriterId_thenThrowException() throws Exception {
        //given
        Post post = create("username", "emankcin");
        em.persist(post.getWriter());
        em.persist(post);
        String newContent = "new";
        //when
        Member invalidWriter = MemberTest.create("username1", "asdf", UserRole.USER);
        em.persist(invalidWriter);
        //then
        assertThrows(BusinessException.class,
                () -> post.update(newContent, invalidWriter),
                ExceptionCode.INVALID_AUTHORITY.getDescription()
        );
    }

    @DisplayName("사용자에 의한 게시물 수정 성공")
    @Test
    public void whenUpdatePostWithValidWriterId_thenSuccess() throws Exception {
        //given
        Post post = create("username", "emankcin");
        em.persist(post.getWriter());
        em.persist(post);
        String newContent = "new";
        Member writer = post.getWriter();
        //when
        post.update(newContent, writer);
        //then
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    public static Post create(String username, String nickname) {
        return new Post(MemberTest.create(username, nickname, UserRole.USER), "title", "content");
    }

}