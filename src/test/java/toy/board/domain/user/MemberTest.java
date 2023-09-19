package toy.board.domain.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.board.domain.auth.Login;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    EntityManager em;

    String username = "name";
    Login login = Login.builder().encodedPassword("password").build();
    Profile profile = Profile.builder("nickname").build();
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;
    Member member;
    
    @BeforeEach
    void init() {
        member = Member.builder(username, login, profile, loginType, userRole).build();
    }

    public static Member create() {
        return Member.builder(
                "username",
                Login.builder().encodedPassword("password").build(),
                Profile.builder("sdfjkhj").build(),
                LoginType.LOCAL_LOGIN,
                UserRole.USER
        ).build();
    }
}