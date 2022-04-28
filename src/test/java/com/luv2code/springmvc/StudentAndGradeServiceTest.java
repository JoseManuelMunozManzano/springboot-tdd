package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Con la anotación @TestPropertySource se cargan, durante los test, ficheros de properties
@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    // Una clase helper proporcionada por Spring Framework que nos permite ejecutar operaciones JDBC
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @BeforeEach
    void setupDatabase() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) " +
                "values(1, 'José Manuel', 'Muñoz', 'jmunoz@gmail.com')");
    }

    @Test
    void createStudentService() {
        studentService.createStudent("José Manuel", "Muñoz", "jmunoz@gmail.es");

        CollegeStudent student = studentDao.findByEmailAddress("jmunoz@gmail.es");

        assertEquals("jmunoz@gmail.es", student.getEmailAddress(), "find by email");
    }

    @Test
    void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(1));

        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return True");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);

        assertFalse(deletedCollegeStudent.isEmpty(), "Return False");
    }


    // Se indica que, además de @BeforeEach, que siempre se va a ejecutar primero,
    // coja datos del fichero resources/insertData.sql antes de ejecutar este test
    @Sql("/insertData.sql")
    @Test
    void getGradebookService() {
        // Test funcionando (GREEN)
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        // Convertir Iterable en List
        for (CollegeStudent collegeStudent: iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @AfterEach
    void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
    }
}

