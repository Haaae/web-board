package toy.board.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import java.util.Random;
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
import toy.board.domain.post.Comment;
import toy.board.domain.post.CommentTest;
import toy.board.domain.post.CommentType;
import toy.board.domain.post.Post;
import toy.board.domain.post.PostTest;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberCheckService memberCheckService;

    private final Random random = new Random();


    @Nested
    class LoginTest {

        @DisplayName("로그인 성공")
        @Test
        void 로그인_성공시_예외발생x() throws Exception {
            //given
            Member member = MemberTest.create();
            String username = member.getUsername();
            String password = member.getPassword();

            //when
            given(memberRepository.findByUsername(anyString()))
                    .willReturn(Optional.of(member));

            doNothing()
                    .when(memberCheckService)
                    .checkPassword(eq(password), eq(password));

            //then
            Member findMember = assertDoesNotThrow(
                    () -> memberService.login(username, password)
            );

            assertThat(findMember).isEqualTo(member);
        }

        @DisplayName("로그인 실패 : 아이디와 일치하는 사용자가 없으면 예외발생")
        @Test
        public void 로그인시_아이디와_일치하는_사용자가_없으면_예외발생() throws Exception {
            //given
            String username = "name";
            String password = "password";

            //when
            given(memberRepository.findByUsername(any()))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberService.login(username, password)
            );
            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);
        }

        @DisplayName("로그인 실패 : 패스워드 불일치 시 예외발생")
        @Test
        public void 패스워드가_다르면_예외발생() throws Exception {
            //given
            Member member = MemberTest.create();
            String username = member.getUsername();
            String password = member.getPassword();

            //when
            String invalidPassword = password + "1";

            given(memberRepository.findByUsername(anyString()))
                    .willReturn(Optional.of(member));

            doThrow(BusinessException.class)
                    .when(memberCheckService)
                    .checkPassword(eq(invalidPassword), eq(password));

            //then
            assertThrows(BusinessException.class, () -> memberService.login(username, invalidPassword));
        }
    }


    @Nested
    class JoinTest {
        @DisplayName("회원가입 성공")
        @Test
        public void 회원가입_성공시_회원객체반환() throws Exception {
            //given
            String username = "username";
            String password = "password";
            String nickname = "nickname";

            //when
            doNothing()
                    .when(memberCheckService)
                    .checkUsernameDuplication(eq(username));

            doNothing()
                    .when(memberCheckService)
                    .checkNicknameDuplication(eq(nickname));

            doReturn(password)
                    .when(passwordEncoder)
                    .encode(eq(password));

            Member result = memberService.join(username, password, nickname);

            //then
            assertThat(result.getUsername())
                    .isEqualTo(username);
            assertThat(result.getNickname())
                    .isEqualTo(nickname);
            assertThat(result.getPassword())
                    .isEqualTo(password);
        }

        @DisplayName("회원가입 실패 : 이메일이 중복이면 예외발생")
        @Test
        public void 이메일이_중복이면_예외발생() throws Exception {
            //given
            String username = "username";
            String password = "password";
            String nickname = "nickname";

            String wrongInput = "wrong input";

            //when
            doThrow(BusinessException.class)
                    .when(memberCheckService)
                    .checkUsernameDuplication(eq(wrongInput));

            doNothing()
                    .when(memberCheckService)
                    .checkUsernameDuplication(eq(username));

            doNothing()
                    .when(memberCheckService)
                    .checkNicknameDuplication(eq(nickname));

            doReturn(password)
                    .when(passwordEncoder)
                    .encode(eq(password));

            //then
            assertThrows(BusinessException.class,
                    () -> memberService.join(wrongInput, password, nickname));
        }

        @DisplayName("회원가입 실패 : 중복된 닉네임이면 예외발생")
        @Test
        public void 중복된_닉네임이면_예외발생() throws Exception {
            //given
            String username = "username";
            String password = "password";
            String nickname = "nickname";

            String wrongInput = "wrong input";

            //when
            doThrow(BusinessException.class)
                    .when(memberCheckService)
                    .checkNicknameDuplication(eq(wrongInput));

            //when
            doNothing()
                    .when(memberCheckService)
                    .checkUsernameDuplication(eq(username));

            doNothing()
                    .when(memberCheckService)
                    .checkNicknameDuplication(eq(nickname));

            doReturn(password)
                    .when(passwordEncoder)
                    .encode(eq(password));

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberService.join(username, password, wrongInput)
            );
        }
    }

    @Nested
    class WithdrawalTest {

        @DisplayName("탈퇴 성공")
        @Test
        void 탈퇴_성공시_예외발생x() throws Exception {
            //given
            Member member = MemberTest.create();
            Post post = PostTest.create(member);
            Comment comment = CommentTest.create(post, member, CommentType.COMMENT);

            long memberId = random.nextLong();

            //when
            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            //then
            assertDoesNotThrow(
                    () -> memberService.withdrawal(memberId)
            );

            assertThat(post.getWriter()).isNull();
            assertThat(comment.getWriter()).isNull();
        }

        @DisplayName("탈퇴 실패 : 사용자가 존재하지 않는다면 예외 발생")
        @Test
        void 사용자가_존재하지_않으면_예외발생() throws Exception {
            //given
            Member member = MemberTest.create();
            Post post = PostTest.create(member);
            Comment comment = CommentTest.create(post, member, CommentType.COMMENT);

            //when
            long notExistMemberId = random.nextLong();
            given(memberRepository.findById(eq(notExistMemberId)))
                    .willReturn(Optional.empty());

            //then
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> memberService.withdrawal(notExistMemberId)
            );

            assertThat(e.getCode()).isEqualTo(ExceptionCode.NOT_FOUND);

            assertThat(post.getWriter()).isEqualTo(member);
            assertThat(comment.getWriter()).isEqualTo(member);

        }
    }
}