package com.ultimatesoftware.school.rest.subject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.ultimatesoftware.school.domain.person.Student;
import com.ultimatesoftware.school.domain.person.StudentService;
import com.ultimatesoftware.school.domain.subject.Subject;
import com.ultimatesoftware.school.domain.subject.SubjectService;
import com.ultimatesoftware.school.rest.person.StudentDtoMapper;

@RestController
@RequestMapping("/v1/subjects")
public class SubjectController {

	private final StudentService studentService;
	private final SubjectService subjectService;
	private final SubjectDtoMapper subjectDtoMapper;
	private final StudentDtoMapper studentDtoMapper;

	@Autowired
	public SubjectController(StudentService studentService, SubjectService subjectService,
			SubjectDtoMapper subjectDtoMapper, StudentDtoMapper studentDtoMapper) {
		this.studentService = studentService;
		this.subjectService = subjectService;
		this.subjectDtoMapper = subjectDtoMapper;
		this.studentDtoMapper = studentDtoMapper;
	}

	@GetMapping
	public Collection<SubjectDto> getAll() {
		return subjectService.getAllSubjects().stream()
				.map(subjectDtoMapper::mapToDto)
				.collect(Collectors.toSet());
	}

	@GetMapping("/{id}")
	public SubjectDto getOne(@PathVariable Long id) {
		Optional<Subject> subject = subjectService.getSubject(id);
		if (!subject.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found");
		}
		return subjectDtoMapper.mapToDto(subject.get());
	}

	@PostMapping
	public SubjectDto create(@RequestBody SubjectDto subjectDto) {
		Subject subject = new Subject();
		subjectDtoMapper.mapToDomain(subjectDto, subject);
		subjectService.save(subject);
		return subjectDtoMapper.mapToDto(subject);
	}

	@PutMapping("/{id}/students")
	public SubjectDto addStudents(@PathVariable Long id, @RequestBody Set<Long> studentIds) {
		Optional<Subject> subject = subjectService.getSubject(id);
		if (!subject.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found");
		}

		Set<Student> students = new HashSet<>(studentIds.size());
		for (Long subjectId :studentIds) {
			Optional<Student> student = studentService.getStudent(subjectId);
			if (!student.isPresent()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
			}
			students.add(student.get());
		}

		subject.get().setStudents(students);
		return subjectDtoMapper.mapToDto(subjectService.save(subject.get()));
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		subjectService.delete(id);
	}

	@GetMapping("/{id}/grades")
	public Map<Long, GradesDto> getGrades(@PathVariable Long id) {
		Optional<Subject> subject = subjectService.getSubject(id);
		if (!subject.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found");
		}

		Map<Long, GradesDto> result = new HashMap<>();
		for (Student student : subject.get().getStudents()) {
			GradesDto gradesDto = new GradesDto();
			gradesDto.setOriginalGrade(student.getOriginalGrade(subject.get()));
			gradesDto.setAdjustedGrade(student.getAdjustedGrade(subject.get()));

			result.put(student.getId(), gradesDto);
		}

		return result;
	}

	@PostMapping("/{id}/adjust-grades")
	public Map<Long, GradesDto> adjustGrades(@PathVariable Long id, @RequestParam Double adjustmentFactor) {
		Optional<Subject> subject = subjectService.getSubject(id);
		if (!subject.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found");
		}

		for (Student student : subject.get().getStudents()) {
			student.setAdjustedGrade(subjectService.adjustGrade(student.getOriginalGrade(subject.get()), adjustmentFactor), subject.get());
			studentService.save(student);
		}

		Map<Long, GradesDto> result = new HashMap<>();
		for (Student student : subject.get().getStudents()) {
			GradesDto gradesDto = new GradesDto();
			gradesDto.setOriginalGrade(student.getOriginalGrade(subject.get()));
			gradesDto.setAdjustedGrade(student.getAdjustedGrade(subject.get()));

			result.put(student.getId(), gradesDto);
		}

		return result;
	}
}
