package toy.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.board.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class RestApiControllerAdvice {

    /**
     * Bean Validation 방식의 유효성 평가 중 오류 발생 시 오류 처리.
     * Bean Validation 중 발생한 오류는 자동으로 BindingReslut에 담기므로
     * BindingResult의 모든 필드에러를 RestResponse.object로 반환
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        ExceptionCode errorCode = ExceptionCode.INVALID_INPUT_VALUE;

        logAll(e);
        String message = buildMessage(e);

        return new ResponseEntity<>(
                new ErrorResponse(errorCode.getCode(), message),
                HttpStatus.valueOf(errorCode.getStatus())
        );
    }

    /**
     * controller에서 오류가 발생했을 때, 발생한 오류의 클래스타입에 따라 오류 처리 지정
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessException e) {
        ExceptionCode code = e.getCode();

        logging(e.getClass().getSimpleName(), code.getCode(), code.getDescription());

        return new ResponseEntity<>(
                new ErrorResponse(code.getCode(), code.getDescription()),
                HttpStatus.valueOf(code.getStatus())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ExceptionCode code = ExceptionCode.INTERNAL_SERVER_ERROR;

        logging(e.getClass().getSimpleName(), code.getCode(), code.getDescription());

        ErrorResponse response = new ErrorResponse(code.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(code.getStatus()));
    }

    private void logAll(MethodArgumentNotValidException e) {
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            logging(fieldError.getObjectName(), fieldError.getField(),
                    fieldError.getDefaultMessage());
        }
    }

    private String buildMessage(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(". ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private void logging(final String className, final String field, final String message) {
        log.info("=== Occurs exception. exception class: {}, field: {}, error message: {} ===", className, field, message);
    }
}
