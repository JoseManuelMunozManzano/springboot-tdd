package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControlledTest {

    // Estático porque se va a usar en el método estático @BeforeAll
    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDao studentDao;

    @BeforeAll
    static void setup() {
        // Vamos a informar un request con datos
        request = new MockHttpServletRequest();

        request.setParameter("firstname", "Adriana");
        request.setParameter("lastname", "Acosta");
        request.setParameter("emailAddress", "adri@gmail.com");
    }

    @BeforeEach
    void beforeEach() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) " +
                "values(1, 'José Manuel', 'Muñoz', 'jmunoz@gmail.com')");
    }

    @Test
    void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("José", "Muñoz",
                "jmunoz@gmail.com");

        CollegeStudent studentTwo = new GradebookCollegeStudent("Adriana", "Acosta",
                "adri@gmail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    void createStudentHttpRequest() throws Exception {
        CollegeStudent studentOne = new CollegeStudent("José", "Muñoz",
                "jmunoz@gmail.com");
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        // Esto hace un post pasando los parámetros. Espera un status Ok
        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress"))
        ).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("adri@gmail.com");

        assertNotNull(verifyStudent, "Student should be found");
    }

    @Test
    void deleteStudentHttpRequest() throws Exception {
        // Primero nos aseguramos que el estudiante existe
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        // Nos aseguramos que el estudiante ha sido borrado
        assertFalse(studentDao.findById(1).isPresent());
    }

    @AfterEach
    void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
    }
}
