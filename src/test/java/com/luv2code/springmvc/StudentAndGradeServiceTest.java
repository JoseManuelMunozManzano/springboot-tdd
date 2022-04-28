package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

// Con la anotación @TestPropertySource se cargan, durante los test, ficheros de properties
@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Test
    void createStudentService() {
        // Se crea StudentAndGradeService y lo necesario para que el test pase (GREEN)
        studentService.createStudent("José Manuel", "Muñoz", "jmunoz@gmail.es");

        // Se crea el método findByEmailAddress
        CollegeStudent student = studentDao.findByEmailAddress("jmunoz@gmail.es");

        assertEquals("jmunoz@gmail.es", student.getEmailAddress(), "find by email");
    }
}

