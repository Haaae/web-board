package toy.board.domain.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    public void throwExceptionWhenPasswordIsEmpty() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Login.builder().build());
        assertThrows(IllegalArgumentException.class, () -> Login.builder().encodedPassword("").build());
        assertThrows(IllegalArgumentException.class, () -> Login.builder().encodedPassword(null).build());
    }

}