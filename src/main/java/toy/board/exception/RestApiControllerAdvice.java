package toy.board.exception;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestApiControllerAdvice {

    /**
     * Bean Validation 방식의 유효성 평가 중 오류 발생 시 오류 처리. Bean Validation 중 발생한 오류는 자동으로 BindingReslut에 담기므로 BindingResult의 모든
     * 필드에러를 RestResponse.object로 반환
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            final MethodArgumentNotValidException e) {
        ExceptionCode errorCode = ExceptionCode.BAD_REQUEST_ARG;

        logAll(e);
        String message = buildMessage(e);

        return new ResponseEntity<>(
                new ErrorResponse(errorCode.getCode(), message),
                HttpStatus.valueOf(errorCode.getStatus())
        );
    }

    /**
     * controller에서 오류가 발생했을 때, 발생한 오류의 클래스타입에 따라 오류 처리 지정
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(final BusinessException e) {
        ExceptionCode code = e.getCode();

        log(e, code);

        return new ResponseEntity<>(
                new ErrorResponse(code.getCode(), code.getDescription()),
                HttpStatus.valueOf(code.getStatus())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException e) {
        ExceptionCode code = ExceptionCode.INTERNAL_SERVER_ERROR;

        log(e, code);

        ErrorResponse response = new ErrorResponse(code.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(code.getStatus()));
    }

    private void log(final RuntimeException e, final ExceptionCode code) {
        logExceptionInfo(
                e.getClass()
                        .getSimpleName(),
                code.getCode(),
                code.getDescription()
        );
        logStackTrace(e.getStackTrace());
    }

    private void logExceptionInfo(final String className, final String field, final String message) {
        log.info(
                "=== Occurs exception. exception class: {}, field: {}, error message: {} ===",
                className,
                field,
                message
        );
    }

    private static void logStackTrace(final StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            log.info("{}", stackTraceElement);
        }
    }

    private void logAll(final MethodArgumentNotValidException e) {
        logAllExceptionInfo(e.getBindingResult().getFieldErrors());
        logStackTrace(e.getStackTrace());

    }

    private void logAllExceptionInfo(List<FieldError> fieldErrors) {
        for (FieldError fieldError : fieldErrors) {
            logExceptionInfo(
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
    }

    private String buildMessage(final MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getField());
            builder.append(" ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(". ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
