package toy.board.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;
import toy.board.domain.user.LoginType;
import toy.board.domain.user.Member;
import toy.board.domain.user.UserRole;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    String username = "name";
    String nickname = "nickname";
    String password = "password";

    Member member;
    Login login;

    @BeforeEach
    void init() {
        this.login = new Login(password);
        this.member = Member.builder(
                        username,
                        nickname,
                        login,
                        LoginType.LOCAL_LOGIN,
                        UserRole.USER
                )
                .build();

        member.changeLogin(login);
        memberRepository.save(member);

        em.clear();
    }

    @DisplayName("username으로 member 존재여부 확인 - 성공")
    @Test
    public void exists_member_by_username_success() throws Exception {
        //given

        //when
        boolean exists = memberRepository.existsByUsername(username);

        //then
        assertThat(exists).isTrue();
    }

    @DisplayName("nickname으로 member 존재여부 확인 - 성공")
    @Test
    public void exists_member_by_nickname_success() throws Exception {
        //given

        //when
        boolean exists = memberRepository.existsByNickname(nickname);

        //then
        assertThat(exists).isTrue();
    }

    @DisplayName("nickanem으로 member 존재여부 확인 - 실패")
    @Test
    public void exists_member_by_nickname_fail() throws Exception {
        //given
        String wrong_input = "adsfasdf";

        //when
        boolean exists = memberRepository.existsByNickname(wrong_input);

        //then
        assertThat(exists).isFalse();
    }

    @DisplayName("username으로 member 존재여부 확인 - 실패")
    @Test
    public void exists_member_by_username_fail() throws Exception {
        //given
        String wrong_input = "adsfasdf";

        //when
        boolean exists = memberRepository.existsByUsername(wrong_input);

        //then
        assertThat(exists).isFalse();
    }
}