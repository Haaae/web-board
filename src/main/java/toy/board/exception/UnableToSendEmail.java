package toy.board.exception;

public class UnableToSendEmail extends CustomException {
    private static final String RESOURCE = "Email";

    private static final String MESSAGE = "메일 발송에 실패하였습니다.";

    public UnableToSendEmail(String field) {
        super(RESOURCE, field, MESSAGE);
    }
}
