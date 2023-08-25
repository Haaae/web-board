package toy.board.repository.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.board.domain.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findMemberByUsername(String username);

    Optional<Member> findMemberById(Long id);

    // TODO: 2023-08-10 test
    @Query("select m from Member m where m.profile.nickname = :nickname")
    Optional<Member> findMemberByNickname(@Param("nickname")String nickname);

    boolean existsByUsername(String username);
}
