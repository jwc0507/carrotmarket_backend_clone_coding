package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.response.PostResponseDto;
import com.example.week7project.dto.response.PostListResponseDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.MemberRepository;
import com.example.week7project.repository.PostRepository;
import com.example.week7project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    //게시글 전체 조회
    @Transactional
    public ResponseDto<List<PostListResponseDto>> readAllPosts(){
        List<Post> postList = postRepository.findAll();
        if(null == postList) //Question. 여기 조건 어떻게 설정해야하는지?
            return ResponseDto.fail("BAD_REQUEST","글 전체 조회 오류");

        else {
            List<PostListResponseDto> postReponseDtoList = new ArrayList<>();
            for (Post post : postList) {
                postReponseDtoList.add(PostListResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .imgUrl(post.getImageUrl())
                        .price(post.getPrice())
                        .numOfChat(post.getNumOfChat())
                        .numOfWish(post.getNumOfWish())
                        .build()
                );
            }
            return ResponseDto.success(postReponseDtoList);
        }
    }

    //특정 게시글 조회
    @Transactional
    public  ResponseDto<PostResponseDto> readPost(Long id) {

        Post post = isPresentPost(id);

        if (null == post) {
            return ResponseDto.fail("BAD_REQUEST", "글 조회 오류");
        }
        else {
            Member member = post.getMember();
            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .temperature(member.getTemperature())
                            .title(post.getTitle())
                            .status(post.getStatus())
                            .category(post.getCategory())
                            .nickname(member.getNickname())
                            .address(member.getAddress())
                            .imgUrl(post.getImageUrl())
                            .price(post.getPrice())
                            .content(post.getContent())
                            .numOfChat(post.getNumOfChat())
                            .numOfWish(post.getNumOfWish())
                            .build()
            );
        }
    }
    //게시글 작성
    @Transactional
    public ResponseDto<PostResponseDto> writePost(PostRequestDto postRequestDto)
    {
        //토큰 인증 해야 됨. 현재 미구현 상태

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .price(postRequestDto.getPrice())
                .imageUrl(postRequestDto.getImageUrl())
                .category(postRequestDto.getCategory())
                .content(postRequestDto.getContent())
                .build();

        if(post == null)
            return ResponseDto.fail("BAD_REQUEST","글 작성 오류");
        else {
            Member member = post.getMember();
            postRepository.save(post);
            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .temperature(member.getTemperature())
                            .title(post.getTitle())
                            .status(post.getStatus())
                            .category(post.getCategory())
                            .nickname(member.getNickname())
                            .address(member.getAddress())
                            .imgUrl(post.getImageUrl())
                            .price(post.getPrice())
                            .content(post.getContent())
                            .numOfChat(post.getNumOfChat())
                            .numOfWish(post.getNumOfWish())
                            .build()
            );
        }
    }
    //게시글 수정
    @Transactional
    public ResponseDto<PostResponseDto> updatePost(Long id, PostRequestDto requestDto)
    {
        //토큰 인증 미구현 상태.

        Post post = isPresentPost(id);
        if(post == null)
            return ResponseDto.fail("BAD_REQUEST","글 수정 오류");
        else {
            Member member = post.getMember();
            post.updatePost(requestDto);
            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .temperature(member.getTemperature())
                            .title(post.getTitle())
                            .status(post.getStatus())
                            .category(post.getCategory())
                            .nickname(member.getNickname())
                            .address(member.getAddress())
                            .imgUrl(post.getImageUrl())
                            .price(post.getPrice())
                            .content(post.getContent())
                            .numOfChat(post.getNumOfChat())
                            .numOfWish(post.getNumOfWish())
                            .build()
            );
        }
    }
    //게시글 삭제
    @Transactional
    public ResponseDto<String> deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow( //findById로 레코드 조회할 때, orElseThrow 함수로 예외처리 안하면 에러 발생.
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );//optional로 바꾸기
        if(post == null)
            return ResponseDto.fail("DELETE_FAILDED","글 삭제에 실패하였습니다.");
        else {
            postRepository.delete(post);
            return ResponseDto.success("글 삭제 완료");
        }
    }
    @Transactional
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    @org.springframework.transaction.annotation.Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
