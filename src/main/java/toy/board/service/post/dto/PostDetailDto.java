package toy.board.service.post.dto;

import toy.board.repository.comment.dto.CommentListDto;

import java.util.HashMap;
import java.util.Map;

public record PostDetailDto(
        Map<String, Object> postDetail
) {

    public static PostDetailDto of(final PostDto postDto, final CommentListDto commentListDto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("post", postDto);
        map.put("comments", commentListDto.commentDtos());
        return new PostDetailDto(map);
    }
}
