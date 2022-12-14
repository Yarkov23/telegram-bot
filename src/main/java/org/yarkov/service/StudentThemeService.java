package org.yarkov.service;

import org.springframework.stereotype.Service;
import org.yarkov.entity.Student;
import org.yarkov.entity.StudentTheme;
import org.yarkov.repository.StudentThemeRepo;

import java.util.List;

@Service
public class StudentThemeService {

    private StudentThemeRepo studentThemeRepo;

    public StudentThemeService(StudentThemeRepo studentThemeRepo) {
        this.studentThemeRepo = studentThemeRepo;
    }

    public List<StudentTheme> findAll() {
        return studentThemeRepo.findAll();
    }

    public void save(StudentTheme studentTheme) {
        studentThemeRepo.save(studentTheme);
    }
    public StudentTheme findByStudentId(Student student) {
        return studentThemeRepo.findByStudentId(student);
    }

}
