package com.exam.controller;

import com.exam.controller.LoginController;
import com.exam.entity.Admin;
import com.exam.entity.ApiResult;
import com.exam.entity.Login;
import com.exam.entity.Student;
import com.exam.entity.Teacher;
import com.exam.serviceimpl.LoginServiceImpl;
import com.exam.util.ApiResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginServiceImpl loginService;

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void login_AdminLoginSuccess_ReturnsAdmin() throws Exception {
        Login login = new Login();
        login.setUsername(123);
        login.setPassword("password");

        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setAdminName("Admin");

        when(loginService.adminLogin(123, "password")).thenReturn(admin);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": 123, \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.adminId").value(1))
                .andExpect(jsonPath("$.data.adminName").value("Admin"));
    }

    @Test
    public void login_TeacherLoginSuccess_ReturnsTeacher() throws Exception {
        Login login = new Login();
        login.setUsername(123);
        login.setPassword("password");

        Teacher teacher = new Teacher();
        teacher.setTeacherId(1);
        teacher.setTeacherName("Teacher");

        when(loginService.adminLogin(123, "password")).thenReturn(null);
        when(loginService.teacherLogin(123, "password")).thenReturn(teacher);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": 123, \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.teacherId").value(1))
                .andExpect(jsonPath("$.data.teacherName").value("Teacher"));
    }

    @Test
    public void login_StudentLoginSuccess_ReturnsStudent() throws Exception {
        Login login = new Login();
        login.setUsername(123);
        login.setPassword("password");

        Student student = new Student();
        student.setStudentId(1);
        student.setStudentName("Student");

        when(loginService.adminLogin(123, "password")).thenReturn(null);
        when(loginService.teacherLogin(123, "password")).thenReturn(null);
        when(loginService.studentLogin(123, "password")).thenReturn(student);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": 123, \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentId").value(1))
                .andExpect(jsonPath("$.data.studentName").value("Student"));
    }

    @Test
    public void login_AllLoginAttemptsFail_ReturnsFailure() throws Exception {
        Login login = new Login();
        login.setUsername(123);
        login.setPassword("password");

        when(loginService.adminLogin(123, "password")).thenReturn(null);
        when(loginService.teacherLogin(123, "password")).thenReturn(null);
        when(loginService.studentLogin(123, "password")).thenReturn(null);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": 123, \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请求失败"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
