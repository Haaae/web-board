package toy.board.repository.user;

import toy.board.domain.user.Member;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberByUsernameWithFetchJoinLogin(final String username);

    Optional<Member> findMemberWithFetchJoinProfile(final Long id);

    boolean existsByNickname(final String nickname);
}
