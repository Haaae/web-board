package toy.board.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.domain.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {


    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    Optional<Member> findByUsername(String username);
}
