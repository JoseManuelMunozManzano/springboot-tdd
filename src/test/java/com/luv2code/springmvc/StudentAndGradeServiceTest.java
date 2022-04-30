package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
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
import java.util.Collection;
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

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @BeforeEach
    void setupDatabase() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) " +
                "values(1, 'José Manuel', 'Muñoz', 'jmunoz@gmail.com')");
        jdbc.execute("INSERT INTO math_grade(id, student_id, grade) values(1, 1, 100.00)");
        jdbc.execute("INSERT INTO science_grade(id, student_id, grade) values(1, 1, 100.00)");
        jdbc.execute("INSERT INTO history_grade(id, student_id, grade) values(1, 1, 100.00)");
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
        // Hacemos también el test del borrado de las asignaturas
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        assertTrue(deletedMathGrade.isPresent());
        assertTrue(deletedScienceGrade.isPresent());
        assertTrue(deletedHistoryGrade.isPresent());

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
        assertFalse(deletedMathGrade.isPresent());
        assertFalse(deletedScienceGrade.isPresent());
        assertFalse(deletedHistoryGrade.isPresent());
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

    @Test
    void createGradeService() {
        // Create the grade
        // grade, id, type
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(80.50, 1, "science"));
        assertTrue(studentService.createGrade(80.50, 1, "history"));

        // Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        // Verify there is grades
        // Hago el cast a Collection para ver su size()
        assertEquals(2, ((Collection<MathGrade>) mathGrades).size(), "Student has math grades");
        assertEquals(2, ((Collection<ScienceGrade>) scienceGrades).size(), "Student has science grades");
        assertEquals(2, ((Collection<HistoryGrade>) historyGrades).size(), "Student has history grades");
    }

    @Test
    void createGradeServiceReturnFalse() {
        // Fuera del rango 0-100
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "math"));
        // No existe studentId 2 (solo tenemos el 1)
        assertFalse(studentService.createGrade(80.50, 2, "math"));
        // Asignatura inválida
        assertFalse(studentService.createGrade(80.50, 1, "literature"));
    }

    @Test
    void deleteGradeServices() {
        assertEquals(1, studentService.deleteGrade(1, "math"), "Return student id after delete");
        assertEquals(1, studentService.deleteGrade(1, "science"), "Return student id after delete");
        assertEquals(1, studentService.deleteGrade(1, "history"), "Return student id after delete");
    }

    @Test
    void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0, studentService.deleteGrade(0, "science"),
                "No student should have 0 id");
        assertEquals(0, studentService.deleteGrade(1, "literature"),
                "No student should have a literature class");
    }

    @Test
    void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);

        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("José Manuel", gradebookCollegeStudent.getFirstname());
        assertEquals("Muñoz", gradebookCollegeStudent.getLastname());
        assertEquals("jmunoz@gmail.com", gradebookCollegeStudent.getEmailAddress());

        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
    }

    @Test
    void studentInformationServiceReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);

        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");
    }
}

