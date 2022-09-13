package com.example.week7project.service;

import com.example.week7project.domain.Member;
import com.example.week7project.domain.Post;
import com.example.week7project.domain.enums.Category;
import com.example.week7project.dto.request.PostRequestDto;
import com.example.week7project.dto.request.StatusRequestDto;
import com.example.week7project.dto.response.MyPostDto;
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

    // 피드백 : if문에서 return이 있다면 else를 굳이 넣으시지 않으셔도 됩니다.


    //게시글 전체 조회
    @Transactional
    public ResponseDto<?> readAllPosts() {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
//        if (postList.isEmpty()) //Question. 여기 조건 어떻게 설정해야하는지? , Answer : 만약 list가 비어있는걸 찾고싶으시면 empty쓰시면 됩니다. 그러나 리스트가 비어있는건 오류사항이 아니므로 체크는 안해도됩니다.
//            // 리스트가 비어있다 = 현재 작성된 글이 없다.
//            return ResponseDto.fail("글 전체 조회 오류");
        List<PostListResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            postResponseDtoList.add(PostListResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .imgUrl(post.getImageUrl())
                    .price(post.getPrice())
                    .numOfChat(post.getNumOfChat())
                    .numOfWish(post.getNumOfWish())
                    .build()
            );
        }
        return ResponseDto.success(postResponseDtoList);
    }

    //특정 게시글 조회
    @Transactional
    public ResponseDto<?> readPost(Long id) {
        Post post = isPresentPost(id);

        if (null == post) {
            return ResponseDto.fail("글 조회 오류 (NOT_EXIST)");
        }
        post.addWatch();

        Member member = post.getMember();
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .temperature(member.getTemperature())
                        .title(post.getTitle())
                        .status(post.getStatus())
                        .category(post.getCategory().toString())
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

    //게시글 작성
    @Transactional
    public ResponseDto<?> writePost(PostRequestDto postRequestDto, HttpServletRequest request) {
        //토큰 인증 해야 됨. 현재 미구현 상태
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .status("판매중")
                .price(postRequestDto.getPrice())
                .imageUrl(postRequestDto.getImageUrl())
                .category(Category.valueOf(postRequestDto.getCategory()))
                .content(postRequestDto.getContent())
                .numOfChat(0)
                .numOfWish(0)
                .member(updateMember)
                .build();

//        위에서 만든 객체는 생성당시 오류가 생긴다면 아래 if문까지 올 일이 없습니다. 도달 할 수 없는 if문 입니다.
//        if (post == null)
//            return ResponseDto.fail("글 작성 오류");
//        Member member = post.getMember(); // 코드대로라면 이 게시글에는 아직 멤버가 존재하지 않습니다. 아마 코드가 정상적으로 실행되지 않을겁니다.

        postRepository.save(post);

        // 프론트에서 요청했던 response는 리스트조회와 같은 responsedto입니다.

//        return ResponseDto.success(
//                PostResponseDto.builder()
//                        .id(post.getId())
//                        .temperature(updateMember.getTemperature())
//                        .title(post.getTitle())
//                        .status(post.getStatus())
//                        .category(post.getCategory())
//                        .nickname(updateMember.getNickname())
//                        .address(updateMember.getAddress())
//                        .imgUrl(post.getImageUrl())
//                        .price(post.getPrice())
//                        .content(post.getContent())
//                        .numOfChat(post.getNumOfChat())
//                        .numOfWish(post.getNumOfWish())
//                        .build()
//        );

        return ResponseDto.success(PostListResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .imgUrl(post.getImageUrl())
                .price(post.getPrice())
                .numOfChat(post.getNumOfChat())
                .numOfWish(post.getNumOfWish())
                .build());
    }

    //게시글 수정
    @Transactional
    public ResponseDto<?> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        //토큰 인증 미구현 상태.
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Post post = isPresentPost(id);
        if (post == null)
            return ResponseDto.fail("글 수정 오류 (NOT_EXIST)");
        // 작성자 검증
        if (post.validateMember(updateMember))
            return ResponseDto.fail("작성자가 아닙니다.");

        post.updatePost(requestDto);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .temperature(updateMember.getTemperature())
                        .title(post.getTitle())
                        .status(post.getStatus())
                        .category(post.getCategory().toString())
                        .nickname(updateMember.getNickname())
                        .address(updateMember.getAddress())
                        .imgUrl(post.getImageUrl())
                        .price(post.getPrice())
                        .content(post.getContent())
                        .numOfChat(post.getNumOfChat())
                        .numOfWish(post.getNumOfWish())
                        .build()
        );

    }

    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Post post = isPresentPost(id);
        if (post == null)
            return ResponseDto.fail("글 삭제에 실패하였습니다. (NOT_EXIST)");

        // 작성자 검증
        if (post.validateMember(updateMember))
            return ResponseDto.fail("작성자가 아닙니다.");

        postRepository.delete(post);
        return ResponseDto.success("글 삭제 완료");

    }

    // 글 id로 글찾기
    @Transactional
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // 토큰체크
    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    // refreshtoken으로 유저찾기
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


    // 연관 카테고리 상품목록 조회
    public ResponseDto<?> getCategoryList(Long postId) {
        Post post = isPresentPost(postId);
        List<MyPostDto> postDtoList = new ArrayList<>();
        if (null == post)
            return ResponseDto.success(postDtoList);    // 연관카테고리 글 없으면 그냥 빈 리스트 주면됨

        Category getCategory = post.getCategory();

        List<Post> posts = postRepository.findByCategory(getCategory);

        for (int i = 0, n = posts.size(); i < n && i < 4; i++) {
            Post p = posts.get(i);
            MyPostDto myPostDto = MyPostDto.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .imgUrl(p.getImageUrl())
                    .price(p.getPrice())
                    .build();
            postDtoList.add(myPostDto);

        }

        return ResponseDto.success(postDtoList);
    }

    // 판매자 상품 목록 조회
    public ResponseDto<?> getProductList(Long sellerId) {
        List<Post> posts = postRepository.findByMemberId(sellerId);
        List<MyPostDto> postDtoList = new ArrayList<>();

        for (int i = 0, n = posts.size(); i < n && i < 10; i++) {
            Post p = posts.get(i);
            MyPostDto myPostDto = MyPostDto.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .imgUrl(p.getImageUrl())
                    .price(p.getPrice())
                    .build();
            postDtoList.add(myPostDto);

        }
        return ResponseDto.success(postDtoList);
    }


    // 상품 상태 변경 (안쓸 예졍)
    @Transactional
    public ResponseDto<?> switchStatus(Long id, StatusRequestDto statusRequestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();
        // 유저 테이블에서 유저객체 가져오기
        Member updateMember = memberRepository.findByNickname(member.getNickname()).get();

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("글 조회 오류 (NOT_EXIST)");
        }
        // 작성자 검증
        if (post.validateMember(updateMember))
            return ResponseDto.fail("작성자가 아닙니다.");

        post.changeStatus(statusRequestDto);

        return ResponseDto.success(post.getStatus());
    }
}
