package toy.board.domain.user;

@lombok.Getter
public enum UserRole {
    USER(false),
    ADMIN(true),
    MASTER(true);

    private final boolean deleteRight;

    UserRole(boolean deleteRight) {
        this.deleteRight = deleteRight;
    }
}
