package com.example.testapi.repository;
import com.example.testapi.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {}
