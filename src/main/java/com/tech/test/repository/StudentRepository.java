package com.tech.test.repository;

import com.tech.test.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.email = :email")
    List<Student> findAllByEmail(String email);

    Optional<Student> findFirstByEmail(String email);
}
