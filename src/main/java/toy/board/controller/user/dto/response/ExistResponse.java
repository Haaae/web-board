package toy.board.controller.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "존재 여부 확인 응답 DTO")
public record ExistResponse(
        @Schema(description = "존재 여부")
        boolean exist
) {
}
