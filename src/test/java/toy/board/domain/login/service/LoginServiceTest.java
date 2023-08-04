package toy.board.domain.login.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import toy.board.entity.auth.Login;
import toy.board.entity.user.LoginType;
import toy.board.entity.user.Member;
import toy.board.entity.user.Profile;
import toy.board.entity.user.UserRole;
import toy.board.repository.LoginRepository;
import toy.board.repository.MemberRepository;
import toy.board.exception.login.NoExistMemberByUsername;
import toy.board.exception.login.NotMatchLoginType;
import toy.board.exception.login.NotMatchPassword;
import toy.board.service.LoginService;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LoginRepository loginRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private String username = "username";
    private String password = "password";
    private String nickname = "nickname";

    @DisplayName("입력한 아이디와 일치하는 멤버가 없음")
    @Test
    public void member_no_exist_test() throws Exception {
        Optional<Member> findMember = Optional.ofNullable(null);
        doReturn(findMember).when(memberRepository).findMemberByUsername(anyString());

        //then
        assertThrows(NoExistMemberByUsername.class,
                () -> loginService.login(username, password));
    }

    @DisplayName("멤버의 로그인 타입이 로컬로그인이 아님")
    @Test
    public void not_match_member_login_type() throws  Exception {
        Member findMember = createMember(LoginType.SOCIAL_LOGIN);

//        doReturn(Optional.create(findMember)).when(memberRepository).findMemberByUsername(anyString());
        given(memberRepository.findMemberByUsername(anyString()))
                .willReturn(Optional.of(findMember));

        //then
        assertThrows(NotMatchLoginType.class, () -> loginService.login(username, password));
    }

    @DisplayName("패스워드 불일치")
    @Test
    public void not_match_password() throws  Exception {
        Member findMember = createMember(LoginType.LOCAL_LOGIN);
        doReturn(Optional.of(findMember)).when(memberRepository).findMemberByUsername(anyString());

        //when
        String wrongPassword = "not match password";

        //then
        assertThrows(NotMatchPassword.class, () -> loginService.login(username, wrongPassword));
    }

    private Member createMember(LoginType loginType) {
        return Member.builder()
                .username(username)
                .profile(
                        Profile.builder().nickname(nickname).build()
                )
                .loginType(loginType)
                .userRole(UserRole.USER)
                .login(new Login(password))
                .build();
    }
}