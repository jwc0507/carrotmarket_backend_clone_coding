package com.example.week7project.domain;

import com.example.week7project.domain.enums.Authority;
import com.example.week7project.dto.request.UpdateProfileDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Member extends Timestamped{

    // 아이디
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // 휴대폰 번호
    @Column (nullable = false, unique = true)
    private String phoneNumber;

    // 닉네임
    @Column (nullable = false, unique = true)
    private String nickname;

    // 비밀번호
    @Column (nullable = false)
    private String password;

    // 주소
    @Column (nullable = false)
    private String address;

    // 유저 권한
    @Column
    private Enum<Authority> userRole;

    // 매너온도
    @Column
    private double temperature;

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    //== 멤버 업데이트 메서드 ==//
    public void updateNickname(UpdateProfileDto updateProfileDto) {
        this.nickname = updateProfileDto.getValue();
    }

    public void updateAddress(UpdateProfileDto updateProfileDto) {
        this.address = updateProfileDto.getValue();
    }
}
