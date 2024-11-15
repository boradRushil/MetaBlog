package com.group3.metaBlog.Blog.Model;

import com.group3.metaBlog.Comment.Model.Comment;
import com.group3.metaBlog.Enum.BlogStatus;
import com.group3.metaBlog.User.Model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private User author;

    private String name;

    private String description;

    private Double createdOn;

    private Double reviewedOn;

    @Column(nullable = false)
    private int viewCount = 0;

    private int like_count = 0;

    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @OneToMany(mappedBy = "blog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;
}
