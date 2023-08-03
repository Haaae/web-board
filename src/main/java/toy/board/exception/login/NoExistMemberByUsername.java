package toy.board.exception.login;

public class NoExistMemberByUsername extends LoginException {
    private static final String RESOURCE = "username";
    private static final String MESSAGE = "아이디가 존재하지 않습니다.";

    public NoExistMemberByUsername() {
        super(RESOURCE, MESSAGE);
    }
}
