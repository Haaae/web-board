package toy.board.controller.user.dto.response;

public record EmailVerificationResponse(
        boolean isCertificated
) {

    public static EmailVerificationResponse of(boolean result) {
        return new EmailVerificationResponse(result);
    }
}
