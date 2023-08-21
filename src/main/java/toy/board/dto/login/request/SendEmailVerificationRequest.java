package toy.board.dto.login.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequest(
        @Email
        @NotBlank
        @Max(value = 20)
        String email
) {

}
