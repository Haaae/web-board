package toy.board.entity.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    public void defaultFieldTest() throws  Exception {
        //given
        Profile profile = Profile.builder().nickname("nickname").build();

        System.out.println("profile = " + profile);

        //when

        //then
    }

    @Test()
    public void throwExceptionWhenBuildHasNotNicknameTest() throws  Exception {
        assertThrows(IllegalArgumentException.class, () -> Profile.builder().build());
        assertThrows(IllegalArgumentException.class, () -> new Profile(null, "url", "introduction"));
    }

}