package toy.board.domain.user;

public class MemberTest {

    public static Member create(String username, String nickname, UserRole role) {
        return new Member(
                username,
                nickname,
                "password",
                role
        );
    }
}