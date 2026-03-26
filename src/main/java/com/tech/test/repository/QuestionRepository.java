package com.tech.test.repository;

import com.tech.test.entity.Question;
import com.tech.test.enums.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByBranch(Branch branch);
}
