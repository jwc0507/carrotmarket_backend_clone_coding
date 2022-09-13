package com.example.week7project.domain;

import com.example.week7project.domain.enums.Category;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.request.StatusRequestDto;
import com.example.week7project.dto.response.ResponseDto;
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
    @Enumerated (EnumType.STRING)
    private Category category;

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

    @Column
    private int numOfWatch;

    //Question create_at은 멤버 정의 안 해줘도 되나?
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void updatePost(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.category = Category.valueOf(postRequestDto.getCategory());
        this.imageUrl = postRequestDto.getImageUrl();
        this.price = postRequestDto.getPrice();
        this.content = postRequestDto.getContent();
    }

    public void changeStatus(StatusRequestDto requestDto) {
        this.status = requestDto.getStatus();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

    public void addWish() {
        this.numOfWish++;
    }

    public void removeWish() {
        int tempWish = this.numOfWish - 1;
        if (tempWish < 0) {
            return;
        }
        this.numOfWish = tempWish;
    }

    public void addChatCount() {
        this.numOfChat++;
    }
    public void addWatch() {
        this.numOfWatch++;
    }
}
