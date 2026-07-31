package com.example.testapi.dtos.post;
import com.example.testapi.dtos.post.PostContentDto;

public record PostSaveRequest(PostContentDto post, Long boardId) {}
