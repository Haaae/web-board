package toy.board.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommentType {
    COMMENT, REPLY;

    @JsonCreator
    public static CommentType from(String s) {
        return CommentType.valueOf(s.toUpperCase());
    }
}
