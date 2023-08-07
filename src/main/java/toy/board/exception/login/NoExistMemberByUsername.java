package toy.board.exception.login;

import toy.board.exception.CustomException;

public class NoExistMemberByUsername extends CustomException implements LoginException {
    private static final String RESOURCE = "username";
    private static final String MESSAGE = "아이디가 존재하지 않습니다.";

    public NoExistMemberByUsername() {
        super(RESOURCE, FIELD, MESSAGE);
    }
}
