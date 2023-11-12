package toy.board.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    BAD_REQUEST_ARG(400, "COMMON-001", "유효하지 않은 요청값"),
    BAD_REQUEST_PAGING_ARG(400, "COMMON-002", "유효하지 않은 페이징 파라미터(size, page)"),
    INTERNAL_SERVER_ERROR(500, "COMMON-003", "서버에서 처리 불가"),
    NOT_FOUND(400, "COMMON-004", "대상을 찾을 수 없음"),
    INVALID_AUTHORITY(403, "COMMON-005", "권한 부족"),
    BAD_REQUEST_DUPLICATE(400, "COMMON-006", "중복에 의한 실패"),

    UNABLE_TO_SEND_EMAIL(500, "LOGIN-001", "이메일 검증 코드 발송 실패"),
    BAD_REQUEST_LOGIN_TYPE(400, "LOGIN-002", "로그인 타입이 일치하지 않음"),
    BAD_REQUEST_AUTHENTICATION(401, "LOGIN-003", "회원 인증 실패"),

    BAD_REQUEST_COMMENT_TYPE(400, "COMMENT-001", "유효하지 않은 댓글 타입"),
    BAD_REQUEST_POST_OF_COMMENT(400, "COMMENT-002", "댓글과 답글의 게시물 불일치"),

    ;

    private final int status;
    private final String code;
    private final String description;

    ExceptionCode(final int status, final String code, final String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }
}
