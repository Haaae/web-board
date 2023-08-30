package toy.board.exception;

public enum ExceptionCode {
    INVALID_INPUT_VALUE(400, "COMMON-001", "유효성 검증 실패"),
    INTERNAL_SERVER_ERROR(500, "COMMON-002", "서버에서 처리 불가"),

    UNABLE_TO_SEND_EMAIL(500, "LOGIN-001", "이메일 발송 실패"),
    NOT_MATCH_LOGIN_TYPE(400, "LOGIN-002", "로그인 타입이 일치하지 않음"),
    NOT_MATCH_PASSWORD(400, "LOGIN-003", "비밀번호가 일치하지 않음"),
    NOT_LOGIN_USER(401, "LOGIN-004", "세션에 회원 로그인 정보가 존재하지 않음"),

    DUPLICATE_USERNAME(400, "ACCOUNT-001", "계정 이메일 중복"),
    DUPLICATE_NICKNAME(400, "ACCOUNT-002", "닉네임 중복"),
    UNAUTHORIZED(401, "ACCOUNT-003", "인증 실패"),
    ACCOUNT_NOT_FOUND(404, "ACCOUNT-004", "계정을 찾을 수 없음"),
    ROLE_NOT_EXISTS(403, "ACCOUNT-005", "권한 부족"),
    SESSION_NOT_EXISTS(401, "ACCOUNT-006", "세션이 존재하지 않음"),

    POST_NOT_FOUND(404, "POST-001", "게시물을 찾을 수 없음"),

    COMMENT_NOT_FOUND(404, "COMENT-001", "댓글을 찾을 수 없음");

    private final int status;
    private final String code;
    private final String description;

    ExceptionCode(final int status, final String code, final String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
