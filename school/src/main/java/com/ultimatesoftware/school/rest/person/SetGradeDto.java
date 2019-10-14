package com.ultimatesoftware.school.rest.person;

public class SetGradeDto {    // Comment 1: Class name should be corrected!!!

	private Long subjectId;
	private Double grade;

	public Long getSubjectId() { return subjectId; }   // Comment 2: Lombok library can be used to reduce the boiler plate code over here!!!

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
