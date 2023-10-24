package toy.board.repository.profile;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.user.Member;
import toy.board.domain.user.MemberTest;

@SpringBootTest
@Transactional
class ProfileRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ProfileRepository profileRepository;

    @DisplayName("멤버 id로 닉네임 가져오기 성공")
    @Test
    public void whenAttemptGetNicknameByMemberId_Success() throws  Exception {
        //given
        Member member = MemberTest.create();
        em.persist(member);
        em.flush(); em.clear();

        //when
        Optional<String> nickname = profileRepository.findNicknameByMemberId(member.getId());

        //then
        assertThat(nickname.get()).isEqualTo(member.getProfile().getNickname());
    }

    @DisplayName("멤버 id로 닉네임 가져오기 실패: 저장되지 않는 멤버 아이디")
    @Test
    public void whenAttemptGetNicknameByNotExistMemberId_Fail() throws  Exception {
        //given
        Member member = MemberTest.create();
        em.persist(member);
        em.flush(); em.clear();

        Long notExistMemberId = 123L;

        //when
        Optional<String> nickname = profileRepository.findNicknameByMemberId(notExistMemberId);

        //then
        assertThrows(NoSuchElementException.class, nickname::get);
    }

}