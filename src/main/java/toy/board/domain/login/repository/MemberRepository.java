package toy.board.domain.login.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.login.entity.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByUsername(String username);
}
