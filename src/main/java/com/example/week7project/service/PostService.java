package com.example.week7project.service;

import com.example.week7project.dto.ImageDeleteResponseDto;
import com.example.week7project.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PostService {
    // 이미지 업로드
    public ResponseDto<?> uploadImage(MultipartFile file, HttpServletRequest request) {
        return ResponseDto.success("업로드 완료");
    }

    // 이미지 수정
    public ResponseDto<?> updateImage(MultipartFile file, HttpServletRequest request) {
        return ResponseDto.success("수정 완료");
    }

    // 이미지 삭제
    public ResponseDto<?> deleteImage(ImageDeleteResponseDto responseDto, HttpServletRequest request) {
        return ResponseDto.success("삭제 완료");
    }


}
