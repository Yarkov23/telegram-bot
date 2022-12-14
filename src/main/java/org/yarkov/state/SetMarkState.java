package org.yarkov.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.yarkov.dto.StudentDTO;
import org.yarkov.entity.State;
import org.yarkov.entity.Student;
import org.yarkov.entity.StudentTheme;
import org.yarkov.json.StudentJsonConverter;
import org.yarkov.service.SendBotMessageServiceImpl;
import org.yarkov.service.StudentService;
import org.yarkov.service.StudentThemeService;

import java.util.Optional;

@Component
public class SetMarkState implements StateProcess {

    private StudentService studentService;
    private StudentThemeService studentThemeService;
    private final SendBotMessageServiceImpl sendBotMessageService;
    private StudentJsonConverter studentJsonConverter;

    public SetMarkState(SendBotMessageServiceImpl sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    public State getState() {
        return State.SET_MARK;
    }


    public void process(Update update, String step) {
        User from = update.getMessage().getFrom();
        Optional<Student> foundedStudent = studentService.findByTelegramId(from.getId().intValue());
        Student student = foundedStudent.get();

        String chatId = update.getMessage().getChatId().toString();

        switch (step) {
            case "Вибрати студента" -> {
                String text = update.getMessage().getText();
                Optional<Student> foundedStud = studentService.findByFullName(text);

                if (foundedStud.isPresent()) {
                    Student resultStudent = foundedStud.get();

                    StudentDTO studentDTO = new StudentDTO();

                    studentDTO.setFullName(resultStudent.getFullName());
                    studentDTO.setGroup(resultStudent.getGroup());

                    String converted = studentJsonConverter.convertToDatabaseColumn(studentDTO);

                    student.setStep("Поставити оцінку");
                    student.setStateObject(converted);
                    studentService.save(student);

                    sendBotMessageService.sendMessage(chatId,
                            "Введіть оцінку по п'ятибальній шкалі (від 1 до 5): ");

                } else {
                    sendBotMessageService.sendMessage(chatId,
                            "Студента не існує або він був введенний не вірно.");
                    exit(student);
                }
            }
            case "Поставити оцінку" -> {
                String mark = update.getMessage().getText();

                if (isNumeric(mark) && Integer.parseInt(mark) >= 1 && Integer.parseInt(mark) <= 5) {
                    StudentDTO studentDTO = studentJsonConverter.convertToEntityAttribute(student.getStateObject());
                    Optional<Student> currentStud = studentService.findByFullName(studentDTO.getFullName());
                    StudentTheme studentTheme = studentThemeService.findByStudentId(currentStud.get());
                    studentTheme.setMark(Integer.parseInt(mark));
                    studentThemeService.save(studentTheme);
                    exit(student);
                } else {
                    sendBotMessageService.sendMessage(chatId,
                            "Не вірно введено оцінку.");
                    exit(student);
                }
            }
        }
    }

    public void exit(Student student) {
        student.setStep("DEFAULT");
        student.setState(State.DEFAULT);
        student.setStateObject(null);
        studentService.save(student);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Autowired
    public void setStudentJsonConverter(StudentJsonConverter studentJsonConverter) {
        this.studentJsonConverter = studentJsonConverter;
    }

    @Autowired
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    public void setStudentThemeService(StudentThemeService studentThemeService) {
        this.studentThemeService = studentThemeService;
    }
}
