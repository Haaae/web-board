package toy.board.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
import toy.board.repository.user.MemberRepository;

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

    @DisplayName("닉네임으로 멤버 찾기 성공")
    @Test
    public void find_member_by_nickname_success() throws  Exception {
        // give

        //when
        Member findMember = memberRepository.findMemberByNickname(member.getProfile().getNickname()).get();

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @DisplayName("닉네임으로 멤버 가져오기 실패: 해당 닉네임과 일치하는 닉네임을 가진 멤버가 없음")
    @Test
    public void find_member_by_nickname_fail_cause_not_exists_nickname() throws  Exception {
        // give
        String wrongNickname = "wrong nickname";

        //when
        Optional<Member> findMember = memberRepository.findMemberByNickname(wrongNickname);

        //then
        assertThat(findMember.isEmpty()).isTrue();
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


        //when
        Optional<Member> findMember = memberRepository.findMemberByUsername(member.getUsername());
        Optional<Login> findLogin = findMember.map(Member::getLogin);
        Optional<Profile> findProfile = findMember.map(Member::getProfile);

        // then
        assertThat(findLogin.isEmpty()).isFalse();
        assertThat(findLogin.get().getPassword()).isEqualTo(login.getPassword());
        assertThat(findProfile.isEmpty()).isFalse();
        assertThat(findProfile.get().getNickname()).isEqualTo(profile.getNickname());
    }

    @DisplayName("username으로 member 존재여부 확인 - 성공")
    @Test
    public void exists_member_by_username_success() throws  Exception {
        //given

        //when
        boolean exists = memberRepository.existsByUsername(username);

        //then
        assertThat(exists).isTrue();
    }

    @DisplayName("nickname으로 member 존재여부 확인 - 성공")
    @Test
    public void exists_member_by_nickname_success() throws  Exception {
        //given

        //when
        boolean exists = memberRepository.existsByNickname(nickname);

        //then
        assertThat(exists).isTrue();
    }

    @DisplayName("nickanem으로 member 존재여부 확인 - 실패")
    @Test
    public void exists_member_by_nickname_fail() throws  Exception {
        //given
        String wrong_input = "adsfasdf";

        //when
        boolean exists = memberRepository.existsByNickname(wrong_input);

        //then
        assertThat(exists).isFalse();
    }

    @DisplayName("username으로 member 존재여부 확인 - 성공")
    @Test
    public void exists_member_by_username_fail() throws  Exception {
        //given
        String wrong_input = "adsfasdf";

        //when
        boolean exists = memberRepository.existsByUsername(wrong_input);

        //then
        assertThat(exists).isFalse();
    }

    // username으로 member 존재여부 확인 - 성공, 실패
    // nickanme으로 member 존재여부 확인 -성공, 실패
}