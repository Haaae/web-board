package toy.board.dto;

import toy.board.exception.ExceptionCode;

public record ErrorResponse(
        String code,
        String message
) {
}
