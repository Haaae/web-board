package toy.board.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import toy.board.entity.auth.Login;
import toy.board.entity.user.LoginType;
import toy.board.entity.user.Member;
import toy.board.entity.user.Profile;
import toy.board.entity.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.member.MemberRepository;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;



    String username = "name";
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;
    String password = "password";
    String nickname = "nickname";
    Member member;
    Login login;
    Profile profile;

    @BeforeEach
    void init() {
        this.profile = Profile.builder().nickname(nickname).build();
        this.login = new Login(password);
        this.member = new Member(username, login, profile, loginType, userRole);

        member.changeLogin(login);
        memberRepository.save(member);
        given(memberRepository.save(any())).willReturn(member);
        given(memberRepository.existsByUsername(eq(username))).willReturn(false);
        given(memberRepository.existsByUsername(eq("wrong input"))).willReturn(true);
        given(memberRepository.existsByNickname(eq(nickname))).willReturn(false);
        given(memberRepository.existsByNickname(eq("wrong input"))).willReturn(true);

        given(passwordEncoder.encode(anyString())).willReturn(password);
    }


    // join
    // - 중복 검사가 잘 이루어지는가

    @DisplayName("이메일과 닉네임이 모두 기존에 존재하지 않음: 통과")
    @Test
    public void MemberServiceTest() throws  Exception {
        //given

        //when
        Member result = memberService.join(username, password, nickname);
        //then
        assertThat(result.getUsername()).isEqualTo(member.getUsername());
    }
    
    @DisplayName("이메일이 기존 이메일과 중복일 경우: 예외발생")
    @Test
    public void join_fail_cause_duplicate_username() throws  Exception {
        //given
        String wrongInput = "wrong input";
        //when
        //then
        BusinessException e = assertThrows(BusinessException.class, () -> memberService.join(wrongInput, password, nickname));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.DUPLICATE_USERNAME);
    }

    @DisplayName("닉네임이 기존 닉네임과 중복일 경우: 예외발생")
    @Test
    public void join_fail_cause_duplicate_nickname() throws  Exception {
        //given
        String wrongInput = "wrong input";

        //when
        //then
        BusinessException e = assertThrows(BusinessException.class, () -> memberService.join(username, password, wrongInput));
        assertThat(e.getCode()).isEqualTo(ExceptionCode.DUPLICATE_NICKNAME);
    }
}