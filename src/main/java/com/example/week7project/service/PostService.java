package com.example.week7project.service;

import com.example.week7project.domain.Post;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public ResponseDto<String> update(Long id, PostRequestDto requestDto)
    {

        Post post = postRepository.findById(id).orElseThrow( //findById로 레코드 조회할 때, orElseThrow 함수로 예외처리 안하면 에러 발생.
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        post.update(requestDto);
        return ResponseDto.success("글 작성 완료");
    }

}
