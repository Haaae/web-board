package toy.board.domain.user;

import org.junit.jupiter.api.Test;

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