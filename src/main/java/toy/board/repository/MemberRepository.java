package toy.board.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import toy.board.entity.user.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByUsername(String username);

    Optional<Member> findMemberById(Long id);

    // TODO: 2023-08-10 test
    @Query("select m from Member m where m.profile.nickname = :nickname")
    Optional<Member> findMemberByNickname(String nickname);
}
