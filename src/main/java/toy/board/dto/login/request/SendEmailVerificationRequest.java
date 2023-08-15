package toy.board.dto.login.request;

import jakarta.validation.constraints.Email;

public record SendEmailVerificationRequest(
        @Email String email
) {

}
