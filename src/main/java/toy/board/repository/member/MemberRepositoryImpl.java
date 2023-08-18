package toy.board.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static toy.board.entity.user.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByNickname(String nickname) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(member)
                .where(
                        member.profile.nickname.eq(nickname)
                )
                .fetchFirst();
        return fetchFirst != null;
    }
}
