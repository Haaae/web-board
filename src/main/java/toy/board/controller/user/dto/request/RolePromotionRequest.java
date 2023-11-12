package toy.board.controller.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "권한 변경 요청 DTO")
public record RolePromotionRequest(
        @Schema(description = "권한 변경 대상 Id")
        @NotNull
        @Positive
        Long id
) {
}
