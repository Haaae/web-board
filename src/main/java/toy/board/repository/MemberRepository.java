package toy.board.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import toy.board.entity.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByUsername(String username);
}
