package com.tech.test.repository;

import com.tech.test.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {}
