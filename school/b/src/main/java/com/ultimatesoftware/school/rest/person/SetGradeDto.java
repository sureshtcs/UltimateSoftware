package com.ultimatesoftware.school.rest.person;

public class SetGradeDto {

	private Long subjectId;
	private Double grade;

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}
}
