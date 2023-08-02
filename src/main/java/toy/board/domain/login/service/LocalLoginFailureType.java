package toy.board.domain.login.service;

public enum LocalLoginFailureType {
    NOT_MATCH_LOGIN_TYPE("회원의 로그인 타입과 로그인 방식이 올바르지 않습니다."),
    NOT_EXIST_USERNAME("아이디가 존재하지 않습니다."),
    NOT_MATCH_PASSWORD("비밀번호가 올바르지 않습니다.");

    private final String message;

    LocalLoginFailureType(String message) {
        this.message = message;
    }
}
