package toy.board.repository.user;

import java.util.Optional;
import toy.board.domain.user.Member;

public interface MemberQueryRepository {

    Optional<Member> findMemberByUsernameWithFetchJoinLogin(final String username);

    Optional<Member> findMemberWithFetchJoinProfile(final Long id);

    boolean existsByNickname(final String nickname);
}
