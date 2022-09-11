package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.enums.Authority;
import com.example.week7project.dto.TokenDto;
import com.example.week7project.dto.request.DuplicationRequestDto;
import com.example.week7project.dto.request.LoginRequestDto;
import com.example.week7project.dto.request.SignupRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    // 회원가입
    @Transactional
    public ResponseDto<?> createMember(SignupRequestDto requestDto) {
        String phoneNumber = requestDto.getPhoneNumber();
        String nickName = requestDto.getNickname();
        String password = requestDto.getPassword();

        // null 인지 확인
        if(phoneNumber==null || nickName==null || password==null)
            return ResponseDto.fail("입력필드가 잘못되었습니다.");
        // blank 인지 확인 .isBlank()로도 가능함.
        if(phoneNumber.trim().isEmpty() || nickName.trim().isEmpty() || password.trim().isEmpty())
            return ResponseDto.fail("빈칸을 채워 다시 입력해주세요.");
        // 전화번호 닉네임 중복확인
        DuplicationRequestDto duplicationRequestDto = new DuplicationRequestDto();
        duplicationRequestDto.setValue(requestDto.getNickname());
        if (!checkNickname(duplicationRequestDto).isSuccess())
            return ResponseDto.fail("닉네임 중복검사를 다시 해주세요.");
        duplicationRequestDto.setValue(requestDto.getPhoneNumber());
        if(!checkPhoneNumber(duplicationRequestDto).isSuccess())
            return ResponseDto.fail("전화번호 중복검사를 다시 해주세요.");

        Member newMember = Member.builder()
                .phoneNumber(phoneNumber)
                .nickname(nickName)
                .password(passwordEncoder.encode(password))
                .userRole(Authority.ROLE_MEMBER)
                .temperature(36.5)
                .build();
        memberRepository.save(newMember);


        return ResponseDto.success("회원가입 완료");
    }

    // 로그인
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getPhoneNumber());
        if(member==null){
            return ResponseDto.fail("존재하지 않는 전화번호입니다.");
        }
        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("잘못된 비밀번호 입니다.");
        }

        // 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        //헤더에 토큰담기
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(member.getNickname());
    }

    // 로그아웃
    @Transactional
    public ResponseDto<?> logout(HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("RefreshToken")))
            return ResponseDto.fail("토큰 값이 올바르지 않습니다.");

        // 맴버객체 찾아오기
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member)
            return ResponseDto.fail("사용자를 찾을 수 없습니다.");
        tokenProvider.deleteRefreshToken(member);


        return ResponseDto.success("로그아웃 성공");
    }

    // 전화번호 중복 검사
    public ResponseDto<?> checkPhoneNumber(DuplicationRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.fail("중복된 전화번호 입니다.");
        return ResponseDto.success("사용 가능한 전화번호 입니다.");
    }

    // 닉네임 중복 검사
    public ResponseDto<?> checkNickname(DuplicationRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findByNickname(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.fail("중복된 닉네임 입니다.");
        return ResponseDto.success("사용 가능한 닉네임 입니다.");
    }

    // 전화번호로 멤버 검색
    @Transactional(readOnly = true)
    public Member isPresentMember(String phoneNumber) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);
        return optionalMember.orElse(null);
    }

    // 헤더에 토큰담기
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
    }
}
