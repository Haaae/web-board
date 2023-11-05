package toy.board.repository.user;

import toy.board.domain.user.Member;
import toy.board.repository.support.Querydsl4RepositorySupport;

import java.util.Optional;

import static toy.board.domain.user.QMember.member;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements
        MemberQueryRepository {

    protected MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<Member> findMemberByUsernameWithFetchJoinLogin(String username) {
        return Optional.ofNullable(
                selectFrom(member)
                        .leftJoin(member.login).fetchJoin()
                        .where(member.username.eq(username))
                        .fetchFirst()
        );
    }

    @Override
    public Optional<Member> findMemberWithFetchJoinProfile(Long id) {
        return Optional.ofNullable(
                selectFrom(member)
                        .leftJoin(member.profile).fetchJoin()
                        .where(member.id.eq(id))
                        .fetchFirst()
        );
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return selectFrom(member)
                .where(
                        member.profile.nickname.eq(nickname)
                )
                .fetchFirst() != null;
    }
}
