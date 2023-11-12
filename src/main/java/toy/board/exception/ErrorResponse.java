package toy.board.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외 관련 DTO")
public record ErrorResponse(
        @Schema(description = "예외 코드", example = "400")
        String code,
        @Schema(description = "예외 메세지", example = "게시물을 찾을 수 없음")
        String message
) {

}
