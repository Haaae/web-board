package toy.board.domain.user;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;

@SpringBootTest
@Transactional
public class MemberTest {

    public static Member create(String username, String nickname, UserRole role) {
        return Member.builder(
                username,
                new Login("password"),
                new Profile(nickname),
                LoginType.LOCAL_LOGIN,
                role
        ).build();
    }
}