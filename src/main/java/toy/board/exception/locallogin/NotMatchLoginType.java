package toy.board.exception.locallogin;

public class NotMatchLoginType extends LocalLoginException {

    private static final String MESSAGE = "로그인 방식이 회원의 로그인 타입과 일치하지 않습니다.";
    private static final String FIELD = "login type";

    public NotMatchLoginType() {
        super(MESSAGE, FIELD);
    }
}
