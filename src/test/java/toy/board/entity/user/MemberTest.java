package toy.board.entity.user;

import org.junit.jupiter.api.Test;
import toy.board.entity.auth.Login;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    String username = "name";
    Login login = Login.builder().encodedPassword("password").build();
    Profile profile = Profile.builder().nickname("nickname").build();
    LoginType loginType = LoginType.LOCAL_LOGIN;
    UserRole userRole = UserRole.USER;

    @Test
    public void throwExceptionWhenUsernameHasNoText() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Member.builder()
                        .login(login)
                        .loginType(loginType)
                        .profile(profile)
                        .userRole(userRole)
                        .build()
                );
    }

    @Test
    public void throwExceptionWhenLoginIsNullOrEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Member.builder()
                .username(username)
                .loginType(loginType)
                .profile(profile)
                .userRole(userRole)
                .build()
        );
    }

    @Test
    public void throwExceptionWhenProfileIsNullOrEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Member.builder()
                .username(username)
                .login(login)
                .profile(profile)
                .userRole(userRole)
                .build()
        );
    }

    @Test
    public void throwExceptionWhenLoginTypeIsNullOrEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Member.builder()
                .username(username)
                .login(login)
                .loginType(loginType)
                .userRole(userRole)
                .build()
        );
    }

    @Test
    public void throwExceptionWhenUserRoleIsNullOrEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Member.builder()
                .username(username)
                .login(login)
                .loginType(loginType)
                .profile(profile)
                .build()
        );
    }

}