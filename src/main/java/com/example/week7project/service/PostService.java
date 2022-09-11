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
        if(null == postList)
            return ResponseDto.fail("BAD_REQUEST","글 전체 조회 오류");
        else
            return ResponseDto.success(postList);
    }

    //특정 게시글 조회
    public  ResponseDto<Post> readPost(Long id) {
        //Question. orElseThrow 써서 예외 처리 안해주면, 에러 뜸. findById로 레코드 조회할 때, 반드시 에러처리를 해줘야 하는건지?
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        if(post == null)
            return ResponseDto.fail("BAD_REQUEST","글 조회 오류");
        else
            return ResponseDto.success(post);
    }
    //게시글 작성
    public ResponseDto<Post> writePost(PostRequestDto postRequestDto)
    {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .price(postRequestDto.getPrice())
                .imageUrl(postRequestDto.getImageUrl())
                .category(postRequestDto.getCategory())
                .content(postRequestDto.getContent())
                .build();

        if(post == null)
            return ResponseDto.fail("BAD_REQUEST","글 작성 오류");
        else
            return ResponseDto.success(post);
    }
    //게시글 수정
    @Transactional
    public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto)
    {

        Post post = postRepository.findById(id).orElseThrow( //findById로 레코드 조회할 때, orElseThrow 함수로 예외처리 안하면 에러 발생.
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        if(post == null)
            return ResponseDto.fail("BAD_REQUEST","글 수정 오류");
        else {
            post.updatePost(requestDto);
            return ResponseDto.success(post);
        }
    }
    //게시글 삭제
    public ResponseDto<String> deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow( //findById로 레코드 조회할 때, orElseThrow 함수로 예외처리 안하면 에러 발생.
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        if(post == null)
            return ResponseDto.fail("DELETE_FAILDED","글 삭제에 실패하였습니다.");
        else {
            postRepository.delete(post);
            return ResponseDto.success("글 삭제 완료");
        }
    }


}
