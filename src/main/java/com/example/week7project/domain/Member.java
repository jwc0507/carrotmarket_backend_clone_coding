package com.example.week7project.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Member extends Timestamped{

    // 아이디 (핸드폰 번호)
    @Id
    private String phoneNumber;

    // 닉네임
    @Column (nullable = false, unique = true)
    private String nickName;

    // 주소
    @Column
    private String address;

    // 매너온도
    @Column
    private double temperature;
}
