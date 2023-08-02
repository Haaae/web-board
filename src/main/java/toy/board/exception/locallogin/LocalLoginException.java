package toy.board.exception.locallogin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalLoginException extends RuntimeException {
    private final String defaultMessage;
    private final String field;
}
