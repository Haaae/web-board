package toy.board.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String resource;
    private final String field;
    private final String defaultMessage;

    public CustomException(String resource, String field, String defaultMessage) {
        this.resource = resource;
        this.field = field;
        this.defaultMessage = defaultMessage;
    }
}
