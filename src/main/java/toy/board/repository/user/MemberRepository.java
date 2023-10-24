package toy.board.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findMemberByUsername(String username);

    @Query(value = "SELECT m FROM Member m LEFT Join Fetch m.profile WHERE m.id = :memberId")
    Optional<Member> findMemberById(@Param("memberId")Long id);

    @Query("select m from Member m where m.profile.nickname = :nickname")
    Optional<Member> findMemberByNickname(@Param("nickname")String nickname);

    boolean existsByUsername(String username);
}
