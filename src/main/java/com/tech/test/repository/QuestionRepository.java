package com.tech.test.repository;

import com.tech.test.entity.Question;
import com.tech.test.enums.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByBranch(Branch branch);
}
