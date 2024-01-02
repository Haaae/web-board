package toy.board.domain.user;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberTest {

    public static Member create(String username, String nickname, UserRole role) {
        return Member.builder(
                username,
                nickname,
                "password",
                role
        ).build();
    }
}