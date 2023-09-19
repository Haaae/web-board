package toy.board.repository.user;

public interface MemberQueryRepository {

    boolean existsByNickname(String nickname);
}
