package org.yarkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yarkov.entity.Subject;

@Repository
public interface SubjectRepo extends JpaRepository<Subject, Long> {
}
