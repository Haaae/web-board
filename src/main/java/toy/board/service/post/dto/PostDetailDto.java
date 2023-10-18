package toy.board.service.post.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import toy.board.repository.comment.dto.CommentDto;
import toy.board.repository.post.dto.PostDto;

public record PostDetailDto(
        Map<String, Object> postDetail
) {

    public static PostDetailDto of(PostDto postDto, List<CommentDto> commentDtos) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("post", postDto);
        map.put("comments", commentDtos);
        return new PostDetailDto(map);
    }
}
