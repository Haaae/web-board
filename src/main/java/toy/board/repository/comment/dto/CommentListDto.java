package toy.board.repository.comment.dto;

import java.util.List;

public record CommentListDto(
        List<CommentDto> commentDtos
) {

}
