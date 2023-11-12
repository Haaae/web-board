package toy.board.controller.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 검증 응답 DTO")
public record EmailVerificationResponse(
        @Schema(description = "검증 결과")
        boolean isCertificated
) {

    public static EmailVerificationResponse of(boolean result) {
        return new EmailVerificationResponse(result);
    }
}
