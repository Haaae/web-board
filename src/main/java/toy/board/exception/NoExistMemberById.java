package toy.board.exception;

public class NoExistMemberById extends CustomException {

    private static final String RESOURCE = "database";

    private static final String MESSAGE = "해당 Id와 일치하는 유저가 존재하지 않습니다.";

    public NoExistMemberById(String field) {
        super(RESOURCE, field, MESSAGE);
    }
}
