package toy.board.repository.profile;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.user.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query(value = "SELECT m.profile.nickname FROM Member m WHERE m.id = :memberId")
    Optional<String> findNicknameByMemberId(@Param("memberId") Long memberId);
}
