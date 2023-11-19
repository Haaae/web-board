package toy.board.domain.user;

import lombok.Getter;


@Getter
public enum UserRole {
    USER(false),
    ADMIN(true),
    MASTER(true);

    private final boolean deleteRight;

    UserRole(boolean deleteRight) {
        this.deleteRight = deleteRight;
    }
}
