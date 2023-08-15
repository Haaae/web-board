package toy.board.dto.login.request;

import jakarta.validation.constraints.Email;

public record EmailVerificationRequest(
        @Email String email,
        String authCode
) {

}
