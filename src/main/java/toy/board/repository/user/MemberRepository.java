package toy.board.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    boolean existsByUsername(String username);
}
