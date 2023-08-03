package toy.board.exception.login;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private static final String FIELD = "login";

    private final String resource;
    private final String field;
    private final String defaultMessage;

    public LoginException(String resource, String defaultMessage) {
        this.resource = resource;
        this.field = FIELD;
        this.defaultMessage = defaultMessage;
    }
}
