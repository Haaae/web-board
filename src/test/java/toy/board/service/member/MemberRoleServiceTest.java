package toy.board.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
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
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.repository.user.MemberRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)   // 사용하지 않는 Mock 설정에 대해 오류를 발생하지 않도록 설정
class MemberRoleServiceTest {

    @InjectMocks
    MemberRoleService memberRoleService;
    @Mock
    MemberRepository memberRepository;

    Random random = new Random();

    @Nested
    class PromoteTest {

        @DisplayName("등급 변경 성공")
        @Test
        void 등급_변경_성공시_예외발생x() throws Exception {
            //given
            long memberId = random.nextLong();
            long adminId = random.nextLong();
            long masterId = random.nextLong();

            Member member = MemberTest.create(UserRole.USER);
            Member admin = MemberTest.create(UserRole.ADMIN);
            Member master = MemberTest.create(UserRole.MASTER);

            //when

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            given(memberRepository.findById(eq(adminId)))
                    .willReturn(Optional.of(admin));

            given(memberRepository.findById(eq(masterId)))
                    .willReturn(Optional.of(master));

            memberRoleService.promoteMemberRole(masterId, memberId);
            memberRoleService.promoteMemberRole(masterId, adminId);

            //then
            assertThat(member.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
        }

        @DisplayName("등급 변경 실패 : 권한 없는 사용자라면 예외발생")
        @Test
        void 등급_변경을_시도하는_사용자가_권한이_없다면_예외발생() throws Exception {
            //given
            long memberId = random.nextLong();
            long adminId = random.nextLong();
            long masterId = random.nextLong();

            Member member = MemberTest.create(UserRole.USER);
            Member admin = MemberTest.create(UserRole.ADMIN);
            Member master = MemberTest.create(UserRole.MASTER);

            //when

            given(memberRepository.findById(eq(memberId)))
                    .willReturn(Optional.of(member));

            given(memberRepository.findById(eq(adminId)))
                    .willReturn(Optional.of(admin));

            given(memberRepository.findById(eq(masterId)))
                    .willReturn(Optional.of(master));

            List<List<Long>> invalidParameters = List.of(
                    List.of(memberId, memberId),
                    List.of(memberId, adminId),
                    List.of(memberId, masterId),
                    List.of(adminId, memberId),
                    List.of(adminId, adminId),
                    List.of(adminId, masterId)
            );

            //then
            for (List<Long> parameter : invalidParameters) {
                Long invalidMemberId = parameter.get(0);
                Long targetId = parameter.get(1);

                BusinessException e = assertThrows(
                        BusinessException.class,
                        () -> memberRoleService.promoteMemberRole(invalidMemberId, targetId)
                );

                assertThat(e.getCode()).isEqualTo(ExceptionCode.INVALID_AUTHORITY);
            }
        }
    }
}