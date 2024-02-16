package toy.board.exception;

@lombok.Getter
@lombok.RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final ExceptionCode code;
}
