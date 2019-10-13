package com.ultimatesoftware.school.rest.subject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultimatesoftware.school.domain.person.Student;
import com.ultimatesoftware.school.domain.person.StudentService;
import com.ultimatesoftware.school.domain.subject.Subject;
import com.ultimatesoftware.school.domain.subject.SubjectService;
import com.ultimatesoftware.school.rest.person.StudentDtoMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(SubjectController.class)
@EnableWebMvc
@Import({
		SubjectDtoMapper.class,
		StudentDtoMapper.class
})
public class SubjectControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SubjectService subjectService;

	@MockBean
	private StudentService studentService;

	private ObjectMapper objectMapper = new ObjectMapper();

	private Student testStudent;
	private Subject testSubject;

	@Before
	public void setUp() throws Exception {
		testSubject = mock(Subject.class);
		testSubject.setName("sName");

		testStudent = mock(Student.class);
		testStudent.setFirstName("fName");
		testStudent.setLastName("lName");
		testStudent.setSubjects(Collections.singleton(testSubject));
	}

	@Test
	public void getAllShouldReturnEmptyList() throws Exception {
		mockMvc.perform(get("/v1/subjects"))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}

	@Test
	public void getOneShouldThrowNotFound() throws Exception {
		mockMvc.perform(get("/v1/subjects/12345"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void createSavesSubject() throws Exception {
		SubjectDto subjectDto = new SubjectDto();
		subjectDto.setName("name");

		when(subjectService.save(any())).thenReturn(new Subject());

		mockMvc.perform(post("/v1/subjects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(subjectDto)))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteNonExistentSubject() throws Exception {
		mockMvc.perform(delete("/v1/subjects/1234"))
				.andExpect(status().isOk());
	}

	@Test
	public void getGrades() throws Exception {
		when(studentService.getStudent(anyLong())).thenReturn(Optional.of(testStudent));
		when(subjectService.getSubject(anyLong())).thenReturn(Optional.of(testSubject));
		when(testSubject.getStudents()).thenReturn(Collections.singleton(testStudent));
		when(testStudent.getOriginalGrade(any())).thenReturn(60.0);
		when(testStudent.getAdjustedGrade(any())).thenReturn(75.0);
		when(testStudent.getId()).thenReturn(1L);


		mockMvc.perform(get("/v1/subjects/1234/grades"))
				.andExpect(status().isOk())
				.andExpect(content().json("{'1':{'originalGrade':60.0,'adjustedGrade':75.0}}"));
	}

}
