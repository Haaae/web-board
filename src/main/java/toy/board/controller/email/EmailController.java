package toy.board.controller.email;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import toy.board.controller.api.response.annotation.common.ApiBadRequestArgError;
import toy.board.controller.api.response.annotation.common.ApiDuplicationError;
import toy.board.controller.api.response.annotation.common.ApiFoundError;
import toy.board.controller.api.response.annotation.email.ApiCodeSendError;
import toy.board.controller.user.dto.request.EmailVerificationRequest;
import toy.board.controller.user.dto.request.SendEmailVerificationRequest;
import toy.board.controller.user.dto.response.EmailVerificationResponse;
import toy.board.controller.user.dto.response.ExistResponse;
import toy.board.service.mail.MailService;

@Tag(name = "Email", description = "Email Verification API Document")
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailController {

    private final MailService mailService;

    @ApiResponse(
            responseCode = "200",
            description = "이메일 검증 코드 발송 성공"
    )
    @ApiBadRequestArgError
    @ApiFoundError
    @ApiCodeSendError
    @Operation(summary = "이메일 검증 코드 발송", description = "입력한 이메일로 검증 코드를 발송합니다.")
    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestBody @Valid final SendEmailVerificationRequest request) {
        mailService.sendCodeToEmail(request.email());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponse(
            responseCode = "200",
            description = "이메일 검증 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExistResponse.class
                    )
            )
    )
    @ApiBadRequestArgError
    @ApiDuplicationError
    @Operation(summary = "이메일 검증", description = "입력 코드가 검증 코드와 같은지 확인합니다.")
    @PostMapping("/emails/verifications")
    public ResponseEntity<EmailVerificationResponse> verificationEmail(
            @RequestBody @Valid final EmailVerificationRequest request) {
        boolean result = mailService.verifiedCode(request.email(), request.authCode());
        return ResponseEntity.ok(
                EmailVerificationResponse.of(result)
        );
    }
}
