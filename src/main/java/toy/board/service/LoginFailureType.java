package toy.board.service;

public enum LoginFailureType {
    NOT_MATCH_LOGIN_TYPE("회원의 로그인 타입과 로그인 방식이 올바르지 않습니다."),
    NOT_EXIST_USERNAME("아이디가 존재하지 않습니다."),
    NOT_MATCH_PASSWORD("비밀번호가 올바르지 않습니다.");

    private final String message;

    LoginFailureType(String message) {
        this.message = message;
    }
}
