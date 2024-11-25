package CSE4186.interview.entity;

import CSE4186.interview.controller.dto.CommentDto;
import CSE4186.interview.controller.dto.PostDto;
import CSE4186.interview.controller.dto.VideoDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    @Column(name = "view_count",nullable = false)
    private Integer viewCount = 0;

    @ColumnDefault("0")
    @Column(name = "like_count",nullable = false)
    private Integer likeCount = 0;

    @ColumnDefault("0")
    @Column(name = "dislike_count",nullable = false)
    private Integer dislikeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_field_id")
    private JobField jobField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @BatchSize(size=50)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createdAt asc")
    private List<Comment> comments = new ArrayList<>();

    @BatchSize(size=10)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("id asc")
    private List<PostVideo> postVideo = new ArrayList<>();

    @BatchSize(size=100)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @BatchSize(size=100)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Dislike> dislikes = new ArrayList<>();

    @OneToOne(mappedBy = "post", orphanRemoval = true)
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    public void updatePost(String title, String content, JobField jobField) {
        this.title = title;
        this.content = content;
        this.jobField = jobField;
    }

    public PostDto.Response toPostResponse() {
        return PostDto.Response.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(this.getContent().replaceAll(System.lineSeparator(), "<br>"))
                .createdAt(String.valueOf(this.getCreatedAt()))
                .updatedAt(String.valueOf(this.getUpdatedAt()))
                .like(this.getLikeCount())
                .dislike(this.getDislikeCount())
                .viewCount(this.getViewCount())
                .jobField(jobField.getField())
                .userId(user.getId())
                .userName(user.getName())
                .comments(getCommentList())
                .videoList(getVideoResponse())
                .build();
    }

    private List<CommentDto.Response> getCommentList() {
        return comments.stream()
                .map(Comment::toCommentResponse)
                .toList();
    }

    private List<VideoDto.Response> getVideoResponse() {
        return postVideo.stream()
                .map(postVideo -> postVideo.getVideo().toVideoResponse())
                .toList();
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void addLikeCount() { this.likeCount += 1; }

    public void subLikeCount() { this.likeCount -= 1; }

    public void addDislikeCount() { this.dislikeCount += 1; }

    public void subDislikeCount() { this.dislikeCount -= 1; }
}
