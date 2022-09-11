package com.example.week7project.controller;

import com.example.week7project.dto.ImageDeleteResponseDto;
import com.example.week7project.dto.response.ResponseDto;
import com.example.week7project.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    // 이미지업로드
    @RequestMapping(value = "/api/post/image", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseDto<?> uploadImage(@RequestPart("image") MultipartFile file, HttpServletRequest request) {
        return fileService.uploadImage(file, request);
    }

    // 이미지변경
    @RequestMapping(value = "/api/post/image/{id}", method = RequestMethod.PUT, consumes = {"multipart/form-data"})
    public ResponseDto<?> updateImage(@PathVariable Long id, @RequestPart("image") MultipartFile file, HttpServletRequest request) {
        return fileService.updateImage(id, file, request);
    }

    // 이미지삭제 (사용되지 않는 메소드, 추후 여러개의 이미지관리를 위해 사용방법을 기록해 둘 겸 틀만 작성해 둔 것임. 이미지 한개 작업에는 deleteResponseDto 가 필요 없음.)
    @RequestMapping(value = "/api/post/image/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteImage(@PathVariable Long id, @RequestBody ImageDeleteResponseDto responseDto, HttpServletRequest request) {
        return fileService.deleteImage(id, responseDto, request);
    }
}
