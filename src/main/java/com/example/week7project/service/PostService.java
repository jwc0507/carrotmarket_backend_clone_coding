package com.example.week7project.service;

import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    //게시글 전체 조회
    public ResponseDto<List<Post>> readAllPosts(){
        List<Post> postList = postRepository.findAll();
        return ResponseDto.success(postList);
    }

    //특정 게시글 조회
    public  ResponseDto<Post> readPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        return ResponseDto.success(post);
    }

    public ResponseDto<String> writePost(PostRequestDto postRequestDto)
    {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .price(postRequestDto.getPrice())
                .imageUrl(postRequestDto.getImageUrl())
                .category(postRequestDto.getCategory())
                .content(postRequestDto.getContent())
                .build();

        return ResponseDto.success("글 작성 완료");
    }

    @Transactional
    public ResponseDto<String> updatePost(Long id, PostRequestDto requestDto)
    {

        Post post = postRepository.findById(id).orElseThrow( //findById로 레코드 조회할 때, orElseThrow 함수로 예외처리 안하면 에러 발생.
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        post.updatePost(requestDto);
        return ResponseDto.success("글 작성 완료");
    }

    public ResponseDto<String> deletePost(Long id) {
        postRepository.deleteById(id);
        return ResponseDto.success("글 삭제 완료");
    }


}
