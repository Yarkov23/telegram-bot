package org.yarkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yarkov.entity.StudentTheme;

@Repository
public interface StudentThemeRepo extends JpaRepository<StudentTheme, Long>{
}
