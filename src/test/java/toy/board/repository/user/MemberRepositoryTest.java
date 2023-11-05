package toy.board.repository.user;

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
import toy.board.domain.user.Profile;
import toy.board.domain.user.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;

    Member member;
    Login login;
    Profile profile;

    @BeforeEach
    void init() {
        this.profile = Profile.builder(nickname).build();
        this.login = new Login(password);
        this.member = Member.builder(username, login, profile, LoginType.LOCAL_LOGIN, UserRole.USER).build();

        member.changeLogin(login);
        memberRepository.save(member);

        em.clear();
    }

    @DisplayName("memberId로 멤버 찾을 때 profile도 가져옴")
    @Test
    public void MemberRepositoryTest() throws Exception {
        //given

        //when
        Optional<Member> findMember = memberRepository.findMemberWithFetchJoinProfile(member.getId());

        //then
        assertThat(findMember.isPresent()).isTrue();
        assertThat(findMember.get().getProfile()).isNotNull();
    }

    @DisplayName("cascade: member만 저장해도 profile과 login이 같이 save됨")
    @Test
    void save_with_cascade() {

        /*
        cascade 설정을 통해 member가 의존하는 profile과 login이 영속성 컨텍스트에 등록되지 않아도 member를 등록할 때 자동으로 등록된다.
        실제로 쿼리를 보면 profile과 login의 insert 쿼리가 member보다 먼저 요청된다.
        - profileRepository.save(profile); => 필요없음
        - loginRepository.save(login); => 필요없음
        - memberRepository.save(member) => 이 메서드만 수행하면 위 두 메서드를 같이 수행한 것과 같다.
         */

        //given
        // then
        assertThat(member.getId()).isNotNull();
        assertThat(profile.getId()).isNotNull();
        assertThat(login.getId()).isNotNull();
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

    @DisplayName("username으로 member 존재여부 확인 - 성공")
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