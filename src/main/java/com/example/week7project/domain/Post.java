package com.example.week7project.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Post extends Timestamped{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String title;

    @Column (nullable = false)
    private String status;

    @Column (nullable = false)
    private String category;

    @Column
    private String imageUrl;

    @Column (nullable = false)
    private long price;

    @Column (nullable = false)
    private String content;

    @Column
    private int numOfChat;

    @Column
    private int numOfWish;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
