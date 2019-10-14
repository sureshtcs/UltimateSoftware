package com.ultimatesoftware.school.domain.subject;

import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

	private final SubjectRepository subjectRepository;

	@Autowired
	public SubjectService(final SubjectRepository subjectRepository) {
		this.subjectRepository = subjectRepository;
	}
	
	@Transactional(readOnly = true)
	public Optional<Subject> getSubject(Long id) {
		return subjectRepository.findById(id);
	}


	@Transactional(readOnly = true)
	public Collection<Subject> getAllSubjects() {
		return subjectRepository.findAll();
	}

	@Transactional
	public Subject save(Subject subject) {
		return subjectRepository.save(subject);
	}

	@Transactional
	public void delete(Long id) {
		subjectRepository.deleteById(id);
	}

	@Transactional
	public Double getAverage(Subject subject) {   //Comment 6: Un-used method, should be removed.
		return subject.getOriginalGrades().values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
	}

	@Transactional(readOnly = true)
	public Double adjustGrade(Double originalGrade, Double adjustmentFactor) {
		return Math.pow(100, 1-adjustmentFactor) * Math.pow(originalGrade, adjustmentFactor);
	}

}
