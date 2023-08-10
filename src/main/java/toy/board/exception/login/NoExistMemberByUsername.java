package toy.board.exception.login;

import toy.board.exception.CustomException;

public class NoExistMemberByUsername extends CustomException {
    private static final String RESOURCE = "username";
    private static final String MESSAGE = "아이디가 존재하지 않습니다.";

    public NoExistMemberByUsername(String field) {
        super(RESOURCE, field, MESSAGE);
    }
}
