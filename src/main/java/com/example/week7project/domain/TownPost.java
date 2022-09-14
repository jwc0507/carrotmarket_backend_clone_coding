package com.example.week7project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TownPost extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private String imgUrl;

    @Column(nullable = false)
    private int numOfComment;

    @Column(nullable = false)
    private int numOfWatch;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "townPost")
    private List<TownComment> townComments;

    public void addWatchCount() {
        this.numOfWatch++;
    }

    public void addCommentCount(int num) {
        if (this.numOfComment + num > 0)
            this.numOfComment += num;
        else
            this.numOfComment = 0;
    }
}
