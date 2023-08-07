package toy.board.exception.login;

import toy.board.exception.CustomException;

public class NotMatchPassword extends CustomException implements LoginException {
    private static final String RESOURCE = "password";
    private static final String MESSAGE = "비밀번호가 올바르지 않습니다.";

    public NotMatchPassword() {
        super(RESOURCE, FIELD, MESSAGE);
    }

}
