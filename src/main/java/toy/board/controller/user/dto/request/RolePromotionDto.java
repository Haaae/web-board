package toy.board.controller.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RolePromotionDto(
        @NotNull
        @Positive
        Long id
) {
}
