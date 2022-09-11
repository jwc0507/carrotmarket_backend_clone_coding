package com.example.week7project.service;

import com.example.week7project.domain.ImageFile;
import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.dto.ImageDeleteResponseDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.FilesRepository;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {
    private final TokenProvider tokenProvider;
    private final PostRepository postRepository;
    private final AmazonS3Service amazonS3Service;
    private final FilesRepository filesRepository;
    private final MemberRepository memberRepository;
    // 이미지 업로드
    @Transactional
    public ResponseDto<?> uploadImage(MultipartFile file, HttpServletRequest request) {
        // 로그인 확인
        ResponseDto<?> chkResponse =  validateCheck(request);
        if(!chkResponse.isSuccess())
            return chkResponse;

        ResponseDto<?> result = amazonS3Service.uploadFile(file);
        if(!result.isSuccess())
            return result;
        ImageFile imageFile = (ImageFile)result.getData();

        return ResponseDto.success(imageFile.getUrl());
    }

    // 이미지 수정
    @Transactional
    public ResponseDto<?> updateImage(Long id, MultipartFile file, HttpServletRequest request) {
        // 로그인 확인
        ResponseDto<?> chkResponse =  validateCheck(request);
        if(!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Optional<Post> getPost = postRepository.findById(id);
        if(getPost.isEmpty())
            return ResponseDto.fail("POST_NOT_FOUND", "게시글을 찾을 수 없습니다.");
        if(getPost.get().validateMember(updateMember))
            return ResponseDto.fail("AUTHOR_NOT_MATCHED", "작성자가 아닙니다.");

        ImageFile getCurrentFile = filesRepository.findByUrl(getPost.get().getImageUrl());
        filesRepository.deleteById(getCurrentFile.getId());

        ResponseDto<?> result = amazonS3Service.uploadFile(file);
        if(!result.isSuccess())
            return result;
        ImageFile imageFile = (ImageFile)result.getData();

        return ResponseDto.success(imageFile.getUrl());
    }

    // 이미지 삭제
    @Transactional
    public ResponseDto<?> deleteImage(Long id, ImageDeleteResponseDto responseDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse =  validateCheck(request);
        if(!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Optional<Post> getPost = postRepository.findById(id);
        if(getPost.isEmpty())
            return ResponseDto.fail("POST_NOT_FOUND", "게시글을 찾을 수 없습니다.");
        if(getPost.get().validateMember(updateMember))
            return ResponseDto.fail("AUTHOR_NOT_MATCHED", "작성자가 아닙니다.");

        ImageFile getCurrentFile = filesRepository.findByUrl(responseDto.getImageUrl());
        filesRepository.deleteById(getCurrentFile.getId());

        return ResponseDto.success("삭제 완료");
    }

    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if(null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if(null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
