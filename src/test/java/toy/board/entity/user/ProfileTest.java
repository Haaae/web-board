package toy.board.entity.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    public void defaultFieldTest() throws  Exception {
        //given
        Profile profile = Profile.builder("nickname").build();

        System.out.println("profile = " + profile);

        //when

        //then
    }
}