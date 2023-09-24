package toy.board.controller.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RolePromotionDto(
        @NotNull
        @Positive
        Long id
) {
}
