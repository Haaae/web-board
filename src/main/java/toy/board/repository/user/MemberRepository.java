package toy.board.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.user.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findMemberByUsernameWithFetchJoinLogin(String username);

    @Query(value = "SELECT m FROM Member m LEFT Join Fetch m.profile WHERE m.id = :memberId")
    Optional<Member> findMemberWithFetchJoinProfile(@Param("memberId") Long id);

    boolean existsByUsername(String username);
}
