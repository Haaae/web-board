package toy.board.repository.profile;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;
import toy.board.domain.user.UserRole;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProfileRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ProfileRepository profileRepository;

    @DisplayName("멤버 id로 닉네임 가져오기 성공")
    @Test
    public void whenAttemptGetNicknameByMemberId_Success() throws Exception {
        //given
        Member member = MemberTest.create("username", "emankcin", UserRole.USER);
        em.persist(member);
        em.flush();
        em.clear();

        //when
        Optional<String> nickname = profileRepository.findNicknameByMemberId(member.getId());

        //then
        assertThat(nickname.get()).isEqualTo(member.getNickname());
    }

    @DisplayName("멤버 id로 닉네임 가져오기 실패: 저장되지 않는 멤버 아이디")
    @Test
    public void whenAttemptGetNicknameByNotExistMemberId_Fail() throws Exception {
        //given
        Member member = MemberTest.create("username", "emankcin", UserRole.USER);
        em.persist(member);
        em.flush();
        em.clear();

        Long notExistMemberId = 123L;

        //when
        Optional<String> nickname = profileRepository.findNicknameByMemberId(notExistMemberId);

        //then
        assertThrows(NoSuchElementException.class, nickname::get);
    }

}