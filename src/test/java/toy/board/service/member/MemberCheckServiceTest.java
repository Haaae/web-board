package toy.board.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class MemberCheckServiceTest {
    @InjectMocks
    private MemberCheckService memberCheckService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class UsernameTest {
        @DisplayName("이메일 중복 검증 : 중복이 아니라면 예외발생x")
        @Test
        void 이메일_중복이_아니라면_예외발생x() throws Exception {
            //given
            String username = "username";

            //when
            given(memberRepository.existsByUsername(eq(username)))
                    .willReturn(false);

            //then
            assertDoesNotThrow(
                    () -> memberCheckService.checkUsernameDuplication(username)
            );
        }

        @DisplayName("이메일 중복 검증 : 중복시 예외발생")
        @Test
        void 이메일_중복시_예외발생() throws Exception {
            //given
            String existUsername = "username";

            //when
            given(memberRepository.existsByUsername(eq(existUsername)))
                    .willReturn(true);

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberCheckService.checkUsernameDuplication(existUsername)
            );

            assertThat(e.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    @Nested
    class NicknameTest {
        @DisplayName("닉네임 중복 검증 : 중복이 아니라면 예외발생x")
        @Test
        void 닉네임_중복이_아니라면_예외발생x() throws Exception {
            //given
            String nickname = "nickname";

            //when
            given(memberRepository.existsByNickname(eq(nickname)))
                    .willReturn(false);

            //then
            assertDoesNotThrow(
                    () -> memberCheckService.checkNicknameDuplication(nickname)
            );
        }

        @DisplayName("닉네임 중복 검증 : 중복시 예외발생")
        @Test
        void 닉네임_중복시_예외발생() throws Exception {
            //given
            String existNickname = "nickname";

            //when
            given(memberRepository.existsByNickname(eq(existNickname)))
                    .willReturn(true);

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberCheckService.checkNicknameDuplication(existNickname)
            );

            assertThat(e.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_DUPLICATE);
        }
    }

    @Nested
    class PasswordTest {
        @DisplayName("패스워드 검증 : 패스워드 일치 시 예외발생x")
        @Test
        public void 패스워드_일치시_예외발생x() throws Exception {
            //given
            String password = "password";

            //when
            given(passwordEncoder.matches(eq(password), eq(password)))
                    .willReturn(true);

            //then
            assertDoesNotThrow(
                    () -> memberCheckService.checkPassword(password, password)
            );
        }

        @DisplayName("패스워드 검증 : 패스워드 불일치 시 예외발생")
        @Test
        public void 패스워드_불일치시_예외발생() throws Exception {
            //given
            String password = "password";
            String wrongPassword = "wrong password";

            //when
            given(passwordEncoder.matches(eq(password), eq(wrongPassword)))
                    .willReturn(false);

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberCheckService.checkPassword(password, wrongPassword)
            );
            assertThat(e.getCode())
                    .isEqualTo(ExceptionCode.BAD_REQUEST_PASSWORD);
        }
    }
}