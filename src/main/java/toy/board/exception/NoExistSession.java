package toy.board.exception;

public class NoExistSession extends CustomException {

    private static final String RESOURCE = "session";

    private static final String MESSAGE = "해당 사용자의 세션이 존재하지 않습니다.";

    public NoExistSession(String field) {
        super(RESOURCE, field, MESSAGE);
    }
}
