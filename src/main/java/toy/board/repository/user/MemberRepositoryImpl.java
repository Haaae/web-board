package toy.board.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import toy.board.domain.user.Member;
import toy.board.repository.support.Querydsl4RepositorySupport;

import static toy.board.domain.user.QMember.member;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements
        MemberQueryRepository {

    protected MemberRepositoryImpl() {
        super(Member.class);
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
