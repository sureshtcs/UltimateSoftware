package com.ultimatesoftware.school.domain.person;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.ultimatesoftware.school.domain.subject.StudentSubject;
import com.ultimatesoftware.school.domain.subject.Subject;

@Entity
@Table(name = "student")
public class Student {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private String lastName;

	@NotNull
	private String firstName;

	@NotNull
	@OneToMany(mappedBy = "student", cascade = { CascadeType.ALL }, orphanRemoval = true)    // Comment 4 - Cascade Type All is not recommended, a more specific type should have to be given rather giving a generic type.
	private Set<StudentSubject> subjects = new HashSet<>();

	public Long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Set<Subject> getSubjects() {
		return subjects.stream().map(StudentSubject::getSubject).collect(Collectors.toSet());
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects.stream().map(subject -> new StudentSubject(this, subject)).collect(Collectors.toSet());
	}

	public Double getOriginalGrade(Subject subject) {
		Optional<StudentSubject> studentSubject = this.subjects.stream()
				.filter(s -> s.equals(subject))
				.findFirst();

		return studentSubject.get().getOriginalGrade();
	}

	public void setOriginalGrade(Double grade, Subject subject) {
		this.subjects.stream()
				.filter(s -> s.getSubject().equals(subject))
				.findFirst()
				.ifPresent(s -> s.setOriginalGrade(grade));
	}

	public Double getAdjustedGrade(Subject subject) {
		Optional<StudentSubject> studentSubject = this.subjects.stream()
				.filter(s -> s.equals(subject))
				.findFirst();

		return studentSubject.get().getAdjustedGrade();
	}

	public void setAdjustedGrade(Double grade, Subject subject) {
		this.subjects.stream()
				.filter(s -> s.getSubject().equals(subject))
				.findFirst()
				.ifPresent(s -> s.setAdjustedGrade(grade));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Student student = (Student) o;
		return Objects.equals(getId(), student.getId()) && getLastName().equals(student.getLastName()) && getFirstName()
				.equals(student.getFirstName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getLastName(), getFirstName());
	}
}
