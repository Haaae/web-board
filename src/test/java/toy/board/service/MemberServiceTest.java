package toy.board.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.entity.auth.Login;
import toy.board.entity.user.LoginType;
import toy.board.entity.user.Member;
import toy.board.entity.user.Profile;
import toy.board.entity.user.UserRole;
import toy.board.repository.LoginRepository;
import toy.board.repository.MemberRepository;
import toy.board.repository.ProfileRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LoginRepository loginRepository;
    @Autowired
    ProfileRepository profileRepository;

    String username = "name";
    Login login = Login.builder().encodedPassword("password").build();
    Profile profile = Profile.builder().nickname("nickname").build();
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;

    @Transactional
    @Test
    void save() {
        // give
        Profile profile = Profile.builder().nickname("nickname").build();
        Login login = new Login("password");
        Member member = new Member(username, login, profile, loginType, userRole);

        member.changeLogin(login);

        // cascade 설정을 통해 member가 의존하는 profile과 login이 영속성 컨텍스트에 등록되지 않아도 member를 등록할 때 자동으로 등록된다.
        // 실제로 쿼리를 보면 profile과 login의 insert 쿼리가 member보다 먼저 요청된다.
//        profileRepository.save(profile);
//        loginRepository.save(login);
        memberRepository.save(member);

        //when
        Optional<Member> findMember = memberRepository.findMemberByUsername(member.getUsername());
        Optional<Login> findLogin = findMember.map(Member::getLogin);
        System.out.println("findLogin = " + findLogin);
        System.out.println("login = " + login);

        // then
        assertThat(findLogin.isEmpty()).isFalse();
        assertThat(findLogin.get().getPassword()).isEqualTo(login.getPassword());
    }
}