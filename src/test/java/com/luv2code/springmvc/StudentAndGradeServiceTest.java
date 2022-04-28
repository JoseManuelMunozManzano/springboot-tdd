package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// Con la anotación @TestPropertySource se cargan, durante los test, ficheros de properties
@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Test
    void createStudentService() {
        // No existe StudentService, asi que esto falla (RED)
        studentService.createStudent("José Manuel", "Muñoz", "jmunoz@gmail.es");

        // CollegeStudent student = studentDao.findByEmailAddress("jmunoz@gmail.es");

        // assertEquals("jmunoz@gmail.es", student.getEmailAddress(), "find by email");
    }
}

