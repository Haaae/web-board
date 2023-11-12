package toy.board.controller.api.response.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import toy.board.exception.ErrorResponse;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ApiResponse(
        responseCode = "400",
        description = "대상을 찾을 수 없음",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                        implementation = ErrorResponse.class
                )
        )
)
public @interface ApiFoundError {

}
