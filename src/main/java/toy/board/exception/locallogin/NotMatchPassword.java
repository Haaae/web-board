package toy.board.exception.locallogin;

public class NotMatchPassword extends LocalLoginException {
    private static final String MESSAGE = "비밀번호가 올바르지 않습니다.";
    private static final String FIELD = "password";

    public NotMatchPassword() {
        super(MESSAGE, FIELD);
    }

}
