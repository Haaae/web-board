package toy.board.exception;

public class NoExistMemberByNickname extends CustomException {

    private static final String RESOURCE = "Session";

    private static final String MESSAGE = "사용자의 로그인 정보를 세션에서 찾을 수 없습니다.";

    public NoExistMemberByNickname(String field) {
        super(RESOURCE, field, MESSAGE);
    }
}
