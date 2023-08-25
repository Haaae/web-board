package toy.board.domain.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    @DisplayName("열거형_상수_객체_동일여부확인")
    void adf() {
        UserRole role1 = UserRole.USER;
        UserRole role2 = UserRole.USER;

        System.out.println("role1 = " + System.identityHashCode(role1));
        System.out.println("role2 = " + System.identityHashCode(role2));
        assertThat(role1).isEqualTo(role2);
        assertThat(System.identityHashCode(role1)).isEqualTo(System.identityHashCode(role2));

    }
}