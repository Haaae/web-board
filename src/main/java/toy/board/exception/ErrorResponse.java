package toy.board.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
