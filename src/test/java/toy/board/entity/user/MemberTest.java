package toy.board.entity.user;

import org.junit.jupiter.api.Test;
import toy.board.entity.auth.Login;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    String username = "name";
    Login login = Login.builder().encodedPassword("password").build();
    Profile profile = Profile.builder("nickname").build();
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;

}