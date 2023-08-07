package toy.board.exception.login;

import toy.board.exception.CustomException;

public class NotMatchLoginType extends CustomException implements LoginException {

    private static final String RESOURCE = "login type";

    private static final String MESSAGE = "로그인 방식이 회원의 로그인 타입과 일치하지 않습니다.";

    public NotMatchLoginType() {
        super(RESOURCE, FIELD, MESSAGE);
    }
}
