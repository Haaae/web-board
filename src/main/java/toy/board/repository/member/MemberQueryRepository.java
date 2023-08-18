package toy.board.repository.member;

public interface MemberQueryRepository {

    boolean existsByNickname(String nickname);
}
