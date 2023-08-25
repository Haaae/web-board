package toy.board.exception;

import toy.board.exception.ExceptionCode;

public record ErrorResponse(
        String code,
        String message
) {
}
