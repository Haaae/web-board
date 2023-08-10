package toy.board.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsernameExistRequest(
        @NotBlank
        @Email
        String username
) {
}
