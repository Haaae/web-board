package toy.board.entity.auth;

import org.junit.jupiter.api.Test;
import toy.board.entity.user.Member;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    public void throwExceptionWhenPasswordIsEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Login.builder().build());
        assertThrows(IllegalArgumentException.class, () -> Login.builder().encodedPassword("").build());
        assertThrows(IllegalArgumentException.class, () -> Login.builder().encodedPassword(null).build());
    }

}