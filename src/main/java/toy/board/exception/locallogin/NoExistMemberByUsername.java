package toy.board.exception.locallogin;

public class NoExistMemberByUsername extends LocalLoginException {
    private static final String MESSAGE = "아이디가 존재하지 않습니다.";
    private static final String FIELD = "username";

    public NoExistMemberByUsername() {
        super(MESSAGE, FIELD);
    }
}
