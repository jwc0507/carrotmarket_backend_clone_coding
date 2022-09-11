package com.example.week7project.domain;

import com.example.week7project.dto.request.PostRequestDto;
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

    //Question create_at은 멤버 정의 안 해줘도 되나?
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void updatePost(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.category = postRequestDto.getCategory();
        this.imageUrl = postRequestDto.getImageUrl();
        this.price = postRequestDto.getPrice();
        this.content = postRequestDto.getContent();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
