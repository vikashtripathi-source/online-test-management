package com.tech.test.repository;

import com.tech.test.entity.StudentTestRecord;
import com.tech.test.enums.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTestRecordRepository extends JpaRepository<StudentTestRecord, Long> {

    List<StudentTestRecord> findByBranch(Branch branch);

    List<StudentTestRecord> findByStudentId(Long studentId);
}
